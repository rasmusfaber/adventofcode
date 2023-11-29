package xyz.faber.adventofcode.year2022.day25

import com.google.common.math.LongMath
import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import kotlin.math.max

class Day25 : AdventSolution<String>() {
    override fun part1(input: List<String>): String {
        return input.reduce(this::addSnafu) // 2--2-0=--0--100-=210
    }

    fun addSnafu(a: String, b: String): String {
        val padLength = max(a.length,b.length)+1
        return a.padStart(padLength, '0')
            .zip(b.padStart(padLength, '0'))
            .foldRight("" to 0, { (ac, bc), (acc, carry) -> addSnafu(ac, bc, carry).let { it.first + acc to it.second } })
            .first.trimStart('0')
    }

    fun addSnafu(a: Char, b: Char, carry: Int): Pair<String, Int> {
        val sum = snafuDigitToInt(a) + snafuDigitToInt(b) + carry
        if (sum < -2) {
            return intToSnafuDigit(sum + 5) to -1
        } else if (sum > 2) {
            return intToSnafuDigit(sum - 5) to 1
        } else {
            return intToSnafuDigit(sum) to 0
        }
    }

    fun snafuDigitToInt(c: Char) = when (c) {
        '=' -> -2
        '-' -> -1
        else -> c.digitToInt()
    }

    fun intToSnafuDigit(i: Int) = when (i) {
        -2 -> "="
        -1 -> "-"
        else -> i.toString()
    }

    fun part1b(input: List<String>): String {
        val sum = input.sumOf { snafuToLong(it) }
        return longToSnafu(sum)
    }

    fun snafuToLong(snafu: String): Long {
        return snafu.map { it }.reversed().withIndex()
            .sumOf {
                LongMath.pow(5L, it.index) * when (it.value) {
                    '=' -> -2L
                    '-' -> -1L
                    '0' -> 0L
                    '1' -> 1L
                    '2' -> 2L
                    else -> throw IllegalArgumentException("Bad value: ${it.value}")
                }
            }
    }

    fun longToSnafu(v: Long): String {
        val base5 = v.toString(5).map { it.digitToInt() }
        val res = StringBuilder()
        var carry = 0
        for (i in base5.reversed()) {
            when (i + carry) {
                in 0..2 -> {
                    res.append(i + carry)
                    carry = 0
                }

                3 -> {
                    res.append('=')
                    carry = 1
                }

                4 -> {
                    res.append('-')
                    carry = 1
                }

                5 -> {
                    res.append('0')
                    carry = 1
                }
            }
        }
        if (carry != 0) {
            res.append(carry)
        }
        return res.toString().reversed()
    }

    override fun part2(input: List<String>): String {
        return ""
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 25, Day25()).run()

}
