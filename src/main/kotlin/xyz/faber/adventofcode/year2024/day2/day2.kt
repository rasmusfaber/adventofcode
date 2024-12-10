package xyz.faber.adventofcode.year2024.day2

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import kotlin.math.abs

class Day2 : AdventSolution<Int>() {
    fun isSafe(report: List<Int>): Boolean {
        val checkAscending = report[0] < report[1]
        val ascendingOkay = if (checkAscending) {
            report.windowed(2).all { it[0] < it[1] }
        } else {
            report.windowed(2).all { it[0] > it[1] }
        }
        if (!ascendingOkay) {
            return false
        }
        val distanceOkay = report.windowed(2).map { abs(it[1] - it[0]) }.all { it in 1..3 }
        return distanceOkay
    }

    override fun part1(input: List<String>): Int {
        return input.map { it.split(" ").map { it.toInt() } }.count { isSafe(it) }
    }

    fun isSafe2(report: List<Int>): Boolean {
        return report.indices.any { i -> isSafe(report.filterIndexed { index, _ -> index != i }) }
    }

    override fun part2(input: List<String>): Int {
        return input.map { it.split(" ").map { it.toInt() } }.count { isSafe2(it) }
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 2, Day2()).run()

}
