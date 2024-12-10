package xyz.faber.adventofcode.year2024.day8

import xyz.faber.adventofcode.util.*

class Day8 : AdventSolution<Int>() {
    override fun part1(input: List<String>): Int {
        val map = input.toXYMap()
        val antennasByFrequency = map.filter { it.value != '.' }.groupBy { it.value }
        val nodeLocations = antennasByFrequency.flatMap { locations(it.value) }.filter { map.isInBounds(it) }.distinct()
        return nodeLocations.count()
    }

    private fun locations(antennas: Collection<MapEntry<Char>>): Collection<Pos> {
        return antennas.flatMap { a -> antennas.filter { it != a }.map { b -> b.pos + b.pos - a.pos } }
    }

    override fun part2(input: List<String>): Int {
        val map = input.toXYMap()
        val antennasByFrequency = map.filter { it.value != '.' }.groupBy { it.value }
        val nodeLocations =
            antennasByFrequency.flatMap { locations2(it.value, map) }.filter { map.isInBounds(it) }.distinct()
        return nodeLocations.count()
    }

    private fun locations2(antennas: Collection<MapEntry<Char>>, map: CharXYMap): Collection<Pos> {
        val res = mutableSetOf<Pos>()
        for (a in antennas) {
            for (b in antennas) {
                if (b != a) {
                    var c = b.pos
                    val d = b.pos - a.pos
                    while (map.isInBounds(c)) {
                        res += c
                        c += d
                    }
                }
            }
        }
        return res
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 8, Day8()).run()

}
