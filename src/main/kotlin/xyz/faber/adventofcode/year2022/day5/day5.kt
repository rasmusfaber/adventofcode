package xyz.faber.adventofcode.year2022.day5

import xyz.faber.adventofcode.util.*
import java.util.Stack

class Day5 : AdventSolution<String>() {
    fun parseInput(input: List<String>): Pair<List<Stack<Char>>, List<List<Int>>> {
        val (setup, moves) = input.split(listOf(""))
        val chunkedStart = setup.subList(0, setup.size - 1).map { it.chunked(4) }
        val stacks = (1..chunkedStart[0].size).map { Stack<Char>() }
        chunkedStart.reversed().forEach {
            it.withIndex().forEach { (i, c) ->
                if (c[1] != ' ') {
                    stacks[i].push(c[1])
                }
            }
        }
        return stacks to moves.map { it.extractNumbers() }
    }

    override fun part1(input: List<String>): String {
        val (stacks, moves) = parseInput(input)
        moves.forEach { (num, from, to) ->
            for (i in 1..num) {
                val c = stacks[from - 1].pop()
                stacks[to - 1].push(c)
            }
        }

        return stacks.map { it.pop() }.joinToString("")
    }

    override fun part2(input: List<String>): String {
        val (stacks, moves) = parseInput(input)

        moves.forEach { (num, from, to) ->
            val c = (1..num).map { stacks[from - 1].pop() }
            c.reversed().forEach { stacks[to - 1].push(it) }

        }

        return stacks.map { it.pop() }.joinToString("")
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 5, Day5()).run()

}
