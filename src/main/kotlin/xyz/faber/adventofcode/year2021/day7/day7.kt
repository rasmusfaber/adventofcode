package xyz.faber.adventofcode.year2021.day7

import xyz.faber.adventofcode.util.getInputIntsFromCsv

class Day7 {
    val input = getInputIntsFromCsv(2021, 7)

    fun part1(input: List<Int>): Int {
        val min = input.minOrNull()!!
        val max = input.maxOrNull()!!
        val res = (min..max).map { p ->
            input.map { Math.abs(it - p) }
                .sum()
        }
            .minOrNull()!!
        return res
    }

    fun part2(input: List<Int>): Int {
        val min = input.minOrNull()!!
        val max = input.maxOrNull()!!
        val res = (min..max).map { p ->
            input.map { Math.abs(it - p) }
                .map { it * (it + 1) / 2 }
                .sum()
        }.minOrNull()!!
        return res
    }

}

fun main(args: Array<String>) {
    val d = Day7()
    println(d.part1(d.input))
    println(d.part2(d.input))
}
