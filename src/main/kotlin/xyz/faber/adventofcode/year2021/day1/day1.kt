package xyz.faber.adventofcode.year2021.day1

import xyz.faber.adventofcode.util.getInputFromLines

class Day1 {
    fun part1(input: List<Int>): Int {
        return input.windowed(2)
            .count { it[0] < it[1] }
    }


    fun part2(input: List<Int>): Int {
        return input.windowed(3)
            .map { it.sum() }
            .windowed(2)
            .count { it[0] < it[1] }
    }

    fun part2b(input: List<Int>): Int {
        return input.windowed(4)
            .count { it[0] < it[3] }
    }
}

fun main(args: Array<String>) {
    val d = Day1()
    val input = getInputFromLines(2021, 1).map { it.toInt() }

    println(d.part1(input))
    println(d.part2(input))
    println(d.part2b(input))
}
