package xyz.faber.adventofcode.year2019.day1

import xyz.faber.adventofcode.util.getInputFromLines

fun part1(input: List<Long>) = input.map { it / 3 - 2 }.sum()

fun part2(input: List<Long>) = input.map { totalFuel(it) }.sum();

fun totalFuel(mass: Long): Long {
    var res = 0L
    var fuel = mass / 3 - 2
    while (fuel > 0) {
        res += fuel
        fuel = fuel / 3 - 2
    }
    return res
}

fun main(args: Array<String>) {
    val input = getInputFromLines(2019, 1).map { it.toLong() }
    println(part1(input))
    println(part2(input))
}
