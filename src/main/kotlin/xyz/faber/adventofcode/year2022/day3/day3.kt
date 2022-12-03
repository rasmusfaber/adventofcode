package xyz.faber.adventofcode.year2022.day3

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Day3 : AdventSolution<Int>() {
    private fun priority(it: Char) = if (it in 'a'..'z') it - 'a' + 1 else it - 'A' + 27
    override fun part1(input: List<String>): Int {
        return input.map { it }
            .map { it.chunked(it.length / 2) }
            .map { it.map { it.toSet() } }
            .map { (b1, b2) -> b1.intersect(b2).single() }
            .sumOf { priority(it) }
    }

    override fun part2(input: List<String>): Int {
        return input.map { it }
            .chunked(3)
            .map { it.map { it.toSet() } }
            .map { (b1, b2, b3) -> b1.intersect(b2).intersect(b3).single()
            }.sumOf { priority(it) }
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 3, Day3()).run()

}
