package xyz.faber.adventofcode.year2022.day2

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Day2 : AdventSolution<Int>() {
    override fun part1(input: List<String>): Int {
        return input
            .map { it.split(" ").map { it[0] } }
            .map { (a, b) -> a - 'A' to b - 'X' }
            .map { (move1, move2) ->
                val outcome = (move2 - move1 + 1).mod(3)

                (move2 + 1) + 3 * outcome
            }.sum()
    }

    override fun part2(input: List<String>): Int {
        return input
            .map { it.split(" ").map { it[0] } }
            .map { (a, c) -> a - 'A' to c - 'X' }
            .map { (move1, outcome) ->
                val move2 = (move1 + outcome - 1).mod(3)

                (move2 + 1) + 3 * outcome
            }.sum()
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 2, Day2()).run()
}
