package xyz.faber.adventofcode.year2022.day4

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.AdventSolutionWithTransform
import xyz.faber.adventofcode.util.extractNumbers

class Day4 : AdventSolutionWithTransform<Int, Pair<IntRange, IntRange>>() {
    override fun transformLine(input: String): Pair<IntRange, IntRange> {
        return input.split('-', ',')
            .map { it.toInt() }
            .chunked(2)
            .map { (x, y) -> x..y }
            .let { (a, b) -> a to b }
    }

    override fun part1(input: List<Pair<IntRange, IntRange>>): Int {
        return input
            .count { (a, b) ->
                (a.first <= b.first && a.last >= b.last)
                        || (b.first <= a.first && b.last >= a.last)
            }
    }

    override fun part2(input: List<Pair<IntRange, IntRange>>): Int {
        return return input
            .count { (a, b) ->
                (a.first <= b.last) && (b.first <= a.last)
            }
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 4, Day4()).run()

}
