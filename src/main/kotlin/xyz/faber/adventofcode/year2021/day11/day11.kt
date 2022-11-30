package xyz.faber.adventofcode.year2021.day11

import xyz.faber.adventofcode.util.XYMap
import xyz.faber.adventofcode.util.getInputFromLines
import xyz.faber.adventofcode.util.toIntXYMap

class Day11 {
    val input = getInputFromLines(2021, 11)

    private fun step(map: XYMap<Int>): Int {
        var flashCount = 0
        map.positions().forEach { map[it]++ }
        do {
            val flashes = map.positions().filter { map[it] > 9 }
            flashes.forEach { map[it] = 0 }
            flashCount += flashes.size
            val reflashes = flashes.flatMap { it.adjacent() }
                .filter { map.isInBounds(it) }
                .filter { map[it] > 0 }
            reflashes.forEach { map[it]++ }
        } while (reflashes.isNotEmpty())
        return flashCount
    }

    fun part1(input: List<String>): Int {
        val map = input.toIntXYMap()
        return (1..100).sumOf { step(map) }
    }


    fun part2(input: List<String>): Int {
        val map = input.toIntXYMap()
        val all = map.positions().size
        return generateSequence(1) { it + 1 }
            .first {
                step(map) == all
            }
    }
}


fun main(args: Array<String>) {
    val d = Day11()

    println(d.part1(d.input))
    println(d.part2(d.input))
}
