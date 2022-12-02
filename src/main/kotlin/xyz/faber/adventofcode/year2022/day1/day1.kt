package xyz.faber.adventofcode.year2022.day1

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.splitOnEmpty

class Day1: AdventSolution<Int>() {
    override fun part1(input: List<String>) : Int {
        return input
            .splitOnEmpty()
            .map { it
                .map { it.toInt() }
                .sum()
            }
            .max()
    }

    override fun part2(input: List<String>) : Int {
        return input
            .splitOnEmpty()
            .map { it
                .map { it.toInt() }
                .sum()
            }
            .sortedDescending()
            .take(3)
            .sum()
    }

}

fun main(args: Array<String>) {
    AdventRunner(2022, 1, Day1()).run()
}
