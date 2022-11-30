package xyz.faber.adventofcode.year2021.day3

import xyz.faber.adventofcode.util.getInputFromLines

class Day3 {
    val input = getInputFromLines(2021, 3)

    fun part1(input: List<String>): Int {
        val freq = (0 until input.first().length)
            .map { i ->
                input.map { it[i] }
                    .groupingBy { it }
                    .eachCount()
            }
        val gamma = freq.map { it.maxByOrNull { it.value }?.key }
            .joinToString("")
            .toInt(2)
        val epsilon = freq.map { it.minByOrNull { it.value }?.key }
            .joinToString("")
            .toInt(2)
        return gamma * epsilon
    }

    fun part2(input: List<String>): Int {
        var oxygen = input
        var i = 0
        while (oxygen.size > 1) {
            val freq = oxygen.map { it[i] }.groupingBy { it }.eachCount()
            if (freq['1'] ?: 0 >= freq['0'] ?: 0) {
                oxygen = oxygen.filter { it[i] == '1' }
            } else {
                oxygen = oxygen.filter { it[i] == '0' }
            }
            i++
        }
        var co2 = input
        i = 0
        while (co2.size > 1) {
            val freq = co2.map { it[i] }.groupingBy { it }.eachCount()
            if (freq['0'] ?: 0 <= freq['1'] ?: 0) {
                co2 = co2.filter { it[i] == '0' }
            } else {
                co2 = co2.filter { it[i] == '1' }
            }
            i++
        }
        return oxygen.first().toInt(2) * co2.first().toInt(2)
    }

    fun calculateRating(values: Set<String>, index: Int, bitCriteria: (Map<Char, Int>) -> Char): String {
        if (values.size == 1) {
            return values.first()
        }
        val freq = values.map { it[index] }.groupingBy { it }.eachCount()
        val bit = bitCriteria(freq)
        val remaining = values.filter { it[index] == bit }.toSet()
        return calculateRating(remaining, index + 1, bitCriteria)
    }

    fun oxygen(values: Set<String>): Int {
        return calculateRating(values, 0) { freq -> setOf('1', '0').maxByOrNull { freq[it] ?: 0 }!! }.toInt(2)
    }

    fun co2(values: Set<String>): Int {
        return calculateRating(values, 0) { freq -> setOf('0', '1').minByOrNull { freq[it] ?: 0 }!! }.toInt(2)
    }

    fun part2b(input: Set<String>): Int {
        return oxygen(input) * co2(input)
    }

}

fun main(args: Array<String>) {
    val d = Day3()
    println(d.part1(d.input))
    println(d.part2(d.input))
    println(d.part2b(d.input.toSet()))
}
