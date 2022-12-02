package xyz.faber.adventofcode.year2022.day1

import xyz.faber.adventofcode.util.getInput
import xyz.faber.adventofcode.util.getInputFromLines
import xyz.faber.adventofcode.util.split
import xyz.faber.adventofcode.util.splitOnEmpty

class Day1 {
    val input = getInput(2022, 1).lines()

    fun part1(input: List<String>) : Int {
        return input
            .splitOnEmpty()
            .map { it
                .map { it.toInt() }
                .sum()
            }
            .max()
    }

    fun part2(input: List<String>) : Int {
        return input
            .splitOnEmpty()
            .map { it
                .map { it.toInt() }
                .sum()
            }
            .sortedDescending()
            .take(3)
            .sum()
    }

}

fun main(args: Array<String>) {
    val d = Day1()
    println(d.part1(d.input))
    println(d.part2(d.input))
}
