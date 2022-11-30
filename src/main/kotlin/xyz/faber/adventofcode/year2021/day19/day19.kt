package xyz.faber.adventofcode.year2021.day19

import xyz.faber.adventofcode.util.*
import kotlin.math.abs

class Day19 {
    val input = getInput(2021, 19).split("\n\n")
        .map {
            it.lines().drop(1).filter { it.isNotBlank() }
                .map { it.split(",").map { it.toInt() } }.map { (x, y, z) -> Pos3D(x, y, z) }
        }

    fun countMatches(s1: List<Pos3D>, rot1: Transform3D, delta1: Pos3D, s2: List<Pos3D>, rot2: Transform3D, delta2: Pos3D): Int {
        val s2set = s2.map { it.transform(rot2) + delta2 }.toSet()
        return s1.count { p1 -> (p1.transform(rot1) + delta1) in s2set }
    }

    private fun scanForMatch(scanner1: List<Pos3D>, scanner2: List<Pos3D>, delta1: Pos3D, rot1: Transform3D): Pair<Pos3D, Transform3D>? {
        for (rot2 in rotations3D) {
            for (i in scanner1.indices) {
                for (j in scanner2.indices) {
                    val delta2 = (scanner1[i].transform(rot1) + delta1) - (scanner2[j].transform(rot2))
                    val matches = countMatches(scanner1, rot1, delta1, scanner2, rot2, delta2)
                    if (matches >= 12) {
                        return delta2 to rot2
                    }
                }
            }
        }
        return null
    }

    fun part1and2() {
        val missing = input.indices.toMutableSet()
        missing.remove(0)
        val found = mutableMapOf<Int, Pair<Pos3D, Transform3D>>()
        found[0] = Pos3D(0, 0, 0) to rotations3D[0]
        val distances = input.mapIndexed { i, scanner ->
            i to getDistances(scanner)
        }.toMap()
        val queue = mutableListOf(0)
        while (queue.isNotEmpty()) {
            val current = queue.removeAt(0)
            val (delta, rot) = found[current]!!
            val thisFound = mutableMapOf<Int, Pair<Pos3D, Transform3D>>()
            for (scanner in missing) {
                if(distances[current]!!.intersect(distances[scanner]!!).size>=66) {
                    val match = scanForMatch(input[current], input[scanner], delta, rot)
                    if (match != null) {
                        thisFound[scanner] = match
                        println("$current $scanner $match")
                    }
                }
            }
            missing.removeAll(thisFound.keys)
            found.putAll(thisFound)
            queue.addAll(thisFound.keys)
        }
        val beacons = input.mapIndexed { scanner, bs ->
            bs.map { it.transform(found[scanner]!!.second) + found[scanner]!!.first }
        }.flatten().toSet()
        //println(beacons.sortedWith(compareBy<Pos3D>{ it.x }.thenBy { it.y }.thenBy { it.z }).joinToString("\n"))
        println(beacons.size)

        val maxDistance = input.indices.flatMap { i ->
            input.indices.filter { it != i }.map { j ->
                abs(found[i]!!.first.x - found[j]!!.first.x) + abs(found[i]!!.first.y - found[j]!!.first.y) + abs(found[i]!!.first.z - found[j]!!.first.z)
            }
        }.maxOrNull()!!
        println(maxDistance)
    }

    private fun getDistances(scanner: List<Pos3D>): Set<Int> =
        scanner.indices.flatMap{
            i-> scanner.indices.map {
                j -> manhattanDistance(scanner[i], scanner[j])
        }}.toSet()

}

fun main(args: Array<String>) {
    val d = Day19()

    d.part1and2()
}
