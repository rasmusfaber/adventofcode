package xyz.faber.adventofcode.year2024.day3

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Day3 : AdventSolution<Int>() {
    override fun part1(input: String): Int {
        return "mul\\((\\d+),(\\d+)\\)".toRegex().findAll(input).map { match ->
            val (a, b) = match.destructured
            a.toInt() * b.toInt()
        }
            .sum()
    }

    override fun part2(input: String): Int {
        val doRegex = "do\\(\\)".toRegex()
        val dontRegex = "don't\\(\\)".toRegex()
        var res = 0
        var i = 0
        while (i < input.length) {
            val dontMatch = dontRegex.find(input.substring(i))
            if (dontMatch != null) {
                res += part1(input.substring(i, i + dontMatch.range.first))
                val doMatch = doRegex.find(input.substring(i + dontMatch.range.last))
                if (doMatch == null) {
                    break
                }
                i = i + dontMatch.range.last + doMatch.range.last
            } else {
                res += part1(input.substring(i)) // 131540125 too high
                break
            }
        }
        return res
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 3, Day3()).run()

}
