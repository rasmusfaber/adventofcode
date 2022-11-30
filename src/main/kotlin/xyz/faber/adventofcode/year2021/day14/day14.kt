package xyz.faber.adventofcode.year2021.day14

import xyz.faber.adventofcode.util.getInputFromLines
import xyz.faber.adventofcode.util.parse
import xyz.faber.adventofcode.util.toMap

class Day14 {
    val input = getInputFromLines(2021, 14)

    fun iterate(input: List<String>, iterations: Int): Long {
        val initial = input[0]
        val pairCounts = initial
            .windowed(2)
            .groupBy { it }
            .mapValues { it.value.size.toLong() }
        val rules = input.parse("(.*) -> (.*)")
            .map { (from, to) -> from to setOf(from[0] + to, to + from[1]) }
            .toMap()

        val res = (1..iterations).fold(pairCounts) { acc, _ ->
            acc.flatMap { (k, v) ->
                (rules[k] ?: listOf(k)).map { it to v }
            }.toMap { m1, m2 -> m1 + m2 }
        }
        val counts = res.map { it.key[0] to it.value }
            .plus(initial.last() to 1L)
            .toMap { m1, m2 -> m1 + m2 }
        val max = counts.maxByOrNull { it.value }!!.value
        val min = counts.minByOrNull { it.value }!!.value
        return max - min
    }

    fun part1(input: List<String>): Long {
        return iterate(input, 10)
    }


    fun part2(input: List<String>): Long {
        return iterate(input, 40)
    }
}

fun main(args: Array<String>) {
    val d = Day14()

    println(d.part1(d.input))
    println(d.part2(d.input))
}
