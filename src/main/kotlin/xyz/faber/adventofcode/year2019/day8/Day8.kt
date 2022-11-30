package xyz.faber.adventofcode.year2019.day8

import xyz.faber.adventofcode.util.detectText
import xyz.faber.adventofcode.util.getInputAsDigits
import xyz.faber.adventofcode.util.toXYMap

class Day8 {
    val input = getInputAsDigits(2019, 8)

    fun part1(): Int {
        return input.chunked(25 * 6)
                .minByOrNull { it.count { it == 0 } }!!
                .let { it.count { it == 1 } * it.count { it == 2 } }

    }


    fun part2() {
        val layers = input.chunked(25 * 6)
        val map = (0 until 25 * 6).map { index ->
            layers.first { it[index] != 2 }[index]
        }.toXYMap(25, 6)
        map.printBlockIf { it == 1 }
        println(map.detectText { it == 1 })
    }

}

fun main(args: Array<String>) {
    val d = Day8()
    println(d.part1())
    d.part2()
}
