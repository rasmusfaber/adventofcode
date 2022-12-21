package xyz.faber.adventofcode.year2022.day21

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Day21 : AdventSolution<Long>() {
    override fun part1(input: List<String>): Long {
        val monkeys = parseMonkeys(input)
        return calc("root", monkeys)
    }

    override fun part2(input: List<String>): Long {
        val monkeys = parseMonkeys(input)
        val references = extractReferences(monkeys)
        return solve("humn", monkeys, references)
    }

    private fun parseMonkeys(input: List<String>) = input.map { it.split(": ") }.map { it[0] to it[1] }.toMap()

    private fun extractReferences(monkeys: Map<String, String>) =
        monkeys.entries.map { it.value.split(" ") to it.key }.filter { it.first.size == 3 }.flatMap { listOf(it.first[0] to it.second, it.first[2] to it.second) }.toMap()

    private fun calc(target: String, monkeys: Map<String, String>): Long {
        val s = monkeys[target]!!.split(" ")
        if (s.size == 1) {
            return s[0].toLong()
        }
        val a = calc(s[0], monkeys)
        val b = calc(s[2], monkeys)
        return when (s[1]!!) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> a / b
            else -> throw IllegalArgumentException(s[1])
        }
    }

    private fun solve(target: String, monkeys: Map<String, String>, references: Map<String, String>): Long {
        val ref = references[target]!!
        val m = monkeys[ref]!!
        val s = m.split(" ")
        if (target == s[0]) {
            val b = calc(s[2], monkeys)
            if (ref == "root") {
                return b
            }
            val r = solve(ref, monkeys, references)
            return when (s[1]!!) {
                "+" -> r - b
                "-" -> r + b
                "*" -> r / b
                "/" -> r * b
                else -> throw IllegalArgumentException(s[1])
            }
        } else {
            val a = calc(s[0], monkeys)
            if (ref == "root") {
                return a
            }
            val r = solve(ref, monkeys, references)
            return when (s[1]!!) {
                "+" -> r - a
                "-" -> a - r
                "*" -> r / a
                "/" -> a / r
                else -> throw IllegalArgumentException(s[1])
            }
        }
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 21, Day21()).run()

}
