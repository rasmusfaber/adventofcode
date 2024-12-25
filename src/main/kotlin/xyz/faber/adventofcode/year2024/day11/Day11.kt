package xyz.faber.adventofcode.year2024.day11

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Day11 : AdventSolution<Long>() {
    fun blink(c: List<Long>): List<Long> {
        val res = mutableListOf<Long>()
        for (s in c) {
            if (s == 0L) {
                res += 1L
            } else {
                val str = s.toString()
                if (str.length % 2 == 0) {
                    val s1 = str.substring(0, str.length / 2).toLong()
                    val s2 = str.substring(str.length / 2).toLong()
                    res += s1
                    res += s2
                } else {
                    res += s * 2024
                }
            }
        }
        return res
    }

    override fun part1(input: String): Long {
        val initial = input.trim().split(" ").map { it.toLong() }
        var c = initial
        for (i in 0 until 25) {
            c = blink(c)
        }
        return c.size.toLong()
    }

    fun blinkNCount(s: Long, c: Int, mem: MutableMap<Pair<Long, Int>, Long>): Long {
        if (c == 0) {
            return 1
        }
        val memmed = mem[s to c]
        if (memmed != null) {
            return memmed
        }

        val res = if (s == 0L) {
            blinkNCount(1L, c - 1, mem)
        } else {
            val str = s.toString()
            if (str.length % 2 == 0) {
                val s1 = str.substring(0, str.length / 2).toLong()
                val s2 = str.substring(str.length / 2).toLong()
                blinkNCount(s1, c - 1, mem) + blinkNCount(s2, c - 1, mem)
            } else {
                blinkNCount(s * 2024, c - 1, mem)
            }
        }
        mem[s to c] = res
        return res
    }

    override fun part2(input: String): Long {
        val initial = input.trim().split(" ").map { it.toLong() }
        val mem = mutableMapOf<Pair<Long, Int>, Long>()
        return initial.sumOf { blinkNCount(it, 75, mem) } // too low
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 11, Day11()).run()
}