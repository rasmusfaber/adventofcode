package xyz.faber.adventofcode.year2024.day7

import org.apache.pdfbox.contentstream.operator.state.Concatenate
import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import kotlin.math.pow

class Day7 : AdventSolution<Long>() {
    fun parseLine(line: String): Pair<Long, List<Long>> {
        val parts = line.split(": ")
        val result = parts[0].toLong()
        val values = parts[1].split(" ").map { it.toLong() }
        return result to values
    }

    fun isSolvable(res: Long, values: List<Long>, i: Int, allowConcatenate: Boolean): Boolean {
        val current = values[i]
        if (i == 0) {
            return res == current
        }
        if (res % current == 0L) {
            if (isSolvable(res / current, values, i - 1, allowConcatenate)) {
                return true
            }
        }
        if (res > current) {
            if (isSolvable(res - current, values, i - 1, allowConcatenate)) {
                return true
            }
        }
        if (allowConcatenate) {
            val len = if (current == 0L) 1 else (Math.log10(current.toDouble()).toInt() + 1)
            val power = 10.0.pow(len).toLong()

            if (res % power == current) {
                val newRes = res / power
                if (isSolvable(newRes, values, i - 1, allowConcatenate)) {
                    return true
                }
            }
        }
        return false;
    }

    fun isSolvable(equation: Pair<Long, List<Long>>, allowConcatenate: Boolean): Boolean {
        return isSolvable(equation.first, equation.second, equation.second.size - 1, allowConcatenate)
    }

    override fun part1(input: List<String>): Long {
        val equations = input.map { parseLine(it) }
        val solvable = equations.filter { isSolvable(it, false) }
        return solvable.sumOf { it.first }
    }

    override fun part2(input: List<String>): Long {
        val equations = input.map { parseLine(it) }
        val solvable = equations.filter { isSolvable(it, true) }
        return solvable.sumOf { it.first } // 59002246504791
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 7, Day7()).run()

}
