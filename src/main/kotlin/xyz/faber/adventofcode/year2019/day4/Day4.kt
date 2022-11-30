package xyz.faber.adventofcode.year2019.day4

import xyz.faber.adventofcode.util.getInput

fun part1(from: String, to: String): Int {
    return (from.toInt()..to.toInt())
            .map { it.toString().toCharArray().toList() }
            .filter { it == it.sorted() }
            .filter { it.groupingBy { it }.eachCount().entries.any { it.value >= 2 } }
            .count()
}

fun part2(from: String, to: String): Int {
    return (from.toInt()..to.toInt())
            .map { it.toString().toCharArray().toList() }
            .filter { it == it.sorted() }
            .filter { it.groupingBy { it }.eachCount().entries.any { it.value == 2 } }
            .count()
}


fun main(args: Array<String>) {
    val input = getInput(2019, 4).split("-").map { it.trim() }

    println(part1(input[0], input[1]))
    println(part2(input[0], input[1]))
}
