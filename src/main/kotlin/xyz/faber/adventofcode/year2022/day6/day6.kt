package xyz.faber.adventofcode.year2022.day6

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Day6 : AdventSolution<Int>() {
    override fun part1(input: String): Int {
        return input.map { it }.windowed(4)
            .indexOfFirst { it.toSet().size == 4 } + 4
    }

    override fun part2(input: String): Int {
        return input.map { it }.windowed(14)
            .indexOfFirst { it.toSet().size == 14 } + 14
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 6, Day6()).run()

}
