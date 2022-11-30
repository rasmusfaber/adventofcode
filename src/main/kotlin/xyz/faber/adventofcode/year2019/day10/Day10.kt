package xyz.faber.adventofcode.year2019.day10

import xyz.faber.adventofcode.util.*

class Day10 {
    val input = getInputFromLines(2019, 10)
    val map = input.toXYMap()
    val asteroids = map.positions().filter { map[it] == '#' }.toSet()

    fun part1(): Int {
        return asteroids.map { countVisible(it) }.maxOrNull()!!
    }

    //fun countVisible(p: Pos): Int = asteroids.filter { hasPath(it, p) }.count()
    fun countVisible(p: Pos): Int = asteroids.filter { it != p }.map { it - p }.map { it.normalize() }.distinct().count()

    fun hasPath(p1: Pos, p2: Pos): Boolean {
        return !asteroids.filter{it != p1 && it != p2}
                .filter { (p1-p2).normalize() == (p1-it).normalize() }
                .filter { distanceSquared(p1,p2)<distanceSquared(p1, it) }
                .any()
    }

    fun part2(): Pos {
        val station = asteroids.maxByOrNull { countVisible(it) }!!
        val asteroidsInOrder = asteroids.filter { it != station }.sortedBy { (it-station).angleWith(Pos(0,-1)) }.toMutableList()
        var blasted: Pos? = null
        for (i in 1..200) {
            blasted = asteroidsInOrder.first { hasPath(station, it) }
            asteroidsInOrder.remove(blasted)
        }
        return blasted!!
    }
}

fun main(args: Array<String>) {
    val d = Day10()
    println(d.part1())
    println(d.part2())
}
