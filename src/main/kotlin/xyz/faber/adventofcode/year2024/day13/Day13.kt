package xyz.faber.adventofcode.year2024.day13

import org.jetbrains.kotlinx.multik.api.linalg.solve
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import xyz.faber.adventofcode.util.*
import kotlin.math.abs
import kotlin.math.roundToInt
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import kotlin.math.roundToLong

class Day13 : AdventSolution<Long>() {
    override fun part1(input: List<String>): Long {
        val machines = input.split(listOf("")).map { parseMachine(it) }
        val res = machines.map { solution(it.first, it.second) }
        return res.filterNotNull().sumOf { it.first * 3 + it.second } // 23297 too low // 31019 too low
    }

    private fun parseMachine(lines: List<String>): Pair<D2Array<Long>, D1Array<Long>> {
        val aButton = lines[0].extractNumbers().map { it.toLong() }
        val bButton = lines[1].extractNumbers().map { it.toLong() }
        val prize = lines[2].extractNumbers().map { it.toLong() }
        val buttonMatrix = mk.ndarray(
            mk[
                mk[aButton[0], bButton[0]],
                mk[aButton[1], bButton[1]]
            ]
        )
        val prizeVector = mk.ndarray(
            mk[prize[0], prize[1]]
        )
        return buttonMatrix to prizeVector
    }

    private fun solution(buttonMatrix: D2Array<Long>, prizeVector: D1Array<Long>): Pair<Long, Long>? {
        val solution = mk.linalg.solve(buttonMatrix, prizeVector)
        if (abs(solution[0].roundToLong().toDouble() - solution[0]) > 0.01
            || abs(solution[1].roundToLong().toDouble() - solution[1]) > 0.01
        ) {
            return null
        }
        return solution[0].roundToLong() to solution[1].roundToLong()
    }

    override fun part2(input: List<String>): Long {
        val delta = mk.ndarray(
            mk[10000000000000L, 10000000000000L]
        )
        val machines = input.split(listOf("")).map { parseMachine(it) }.map{it.first to (it.second + delta)}
        val res = machines.map { solution(it.first, it.second) }
        return res.filterNotNull().sumOf { it.first * 3 + it.second } // 23297 too low // 31019 too low
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 13, Day13()).run()
}