package xyz.faber.adventofcode.year2024.day25

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.split

class Day25 : AdventSolution<Int>() {
    private fun parseLock(lock: List<String>): List<Int> {
        return (0..4).map { column ->
            (0..5).filter { row -> lock[row + 1][column] == '.' }.min()
        }
    }

    private fun parseKey(key: List<String>): List<Int> {
        return (0..4).map { column ->
            (0..5).filter { row -> key[5 - row][column] == '.' }.min()
        }
    }

    override fun part1(input: List<String>): Int {
        val elements = input.split(listOf(""))
        val lockElements = elements.filter { it[0] == "#####" }
        val keyElements = elements.filter { it[6] == "#####" }
        val locks = lockElements.map { parseLock(it) }
        val keys = keyElements.map { parseKey(it) }

        val fits = locks.flatMap { lock ->
            keys.map { key -> lock to key }.filter { (lock, key) ->
                key.zip(lock).all { it.first + it.second <= 5 }
            }
        }

        return fits.size
    }

    override fun part2(input: List<String>): Int {
        throw NotImplementedError()
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 25, Day25()).run()
}