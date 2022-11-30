package xyz.faber.adventofcode.year2021.day8

import com.google.common.collect.Collections2
import xyz.faber.adventofcode.util.getInputFromLines

class Day8 {
    val input = getInputFromLines(2021, 8)

    fun part1(input: List<String>): Int {
        return input.map { it.split("|") }.map { it[1] }
            .map { it.split(" ") }
            .flatten()
            .map { it.trim() }
            .count { it.length in setOf(2, 4, 3, 7) }

    }

    val correctDigits = mapOf(
        "abcefg" to "0", "cf" to "1", "acdeg" to "2", "acdfg" to "3", "bcdf" to "4",
        "abdfg" to "5", "abdefg" to "6", "acf" to "7", "abcdefg" to "8", "abcdfg" to "9"
    )
    val validPatterns = correctDigits.keys.toSet()
    val permutations = Collections2.permutations("abcdefg".toSet())

    fun List<Char>.permute(c: Char): Char {
        return this[c - 'a']
    }

    fun List<Char>.permute(s: String): String {
        return s.toCharArray().map { this.permute(it) }.sorted().joinToString("")
    }

    fun works(digits: List<String>, permutation: List<Char>): Boolean {
        return digits.map { permutation.permute(it) }.all { it in validPatterns }
    }

    fun solve(line: String): Int {
        val parts = line.split("|").map { it.trim() }
        val digits = parts.map { it.split(" ") }.flatten()
        val permutation = permutations.first { works(digits, it) }
        return parts[1].split(" ").map { permutation.permute(it) }
            .map { correctDigits[it] }.joinToString("").toInt()
    }

    fun part2(input: List<String>): Int {
        return input.map { solve(it) }
            .sum()
    }

    fun solveb(line: String): Int {
        val parts = line.split("|").map { it.trim() }
        val digits = parts.map { it.split(" ") }.flatten()
            .map { it.toSet() }
        val remaining = digits.toMutableSet()

        val _1 = remaining.filter { it.size == 2 }.single(); remaining.remove(_1)
        val _7 = remaining.filter { it.size == 3 }.single(); remaining.remove(_7)
        val _4 = remaining.filter { it.size == 4 }.single(); remaining.remove(_4)
        val _8 = remaining.filter { it.size == 7 }.single(); remaining.remove(_8)
        val _9 = remaining.filter { (it.intersect(_4)).size == 2 }.single(); remaining.remove(_9)
        val _3 = remaining.filter { (it - _7).size == 2 }.single(); remaining.remove(_3)
        val _2 = remaining.filter { (it.intersect(_9)).size == 4 }.single(); remaining.remove(_2)
        val _0 = remaining.filter { (it - _1).size == 2 }.single(); remaining.remove(_0)
        val _6 = remaining.filter { it.size == 6 }.single(); remaining.remove(_6)
        val _5 = remaining.single()

        val permutation = listOf(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9)
        return digits.takeLast(4).map { '0' + permutation.indexOf(it) }.joinToString("").toInt()
    }

    fun part2b(input: List<String>): Int {
        return input.map { solve(it) }
            .sum()
    }

}

fun main(args: Array<String>) {
    val d = Day8()
    println(d.part1(d.input))
    println(d.part2(d.input))
    println(d.part2b(d.input))
}
