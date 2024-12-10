package xyz.faber.adventofcode.year[[${year}]].day[[${day}]]

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Day[[${day}]] : AdventSolution<Int>() {
    override fun part1(input: List<String>): Int {
        return 0
    }

    override fun part2(input: List<String>): Int {
        throw NotImplementedError()
    }
}

fun main(args: Array<String>) {
    AdventRunner([[${year}]], [[${day}]], Day[[${day}]]()).run()
}