package xyz.faber.adventofcode.year2021.day2

import xyz.faber.adventofcode.util.Pos
import xyz.faber.adventofcode.util.getInputFromLines

class Day2 {
    val input = getInputFromLines(2021, 2)
    val inputParsed = input.map { it.split(" ") }.map { it[0] to it[1].toInt() }

    fun part1() {
        var pos = Pos(0, 0)
        inputParsed.forEach {
            when (it.first) {
                "up" -> pos = Pos(pos.x, pos.y - it.second)
                "down" -> pos = Pos(pos.x, pos.y + it.second)
                "forward" -> pos = Pos(pos.x + it.second, pos.y)
            }
        }
        println(pos.x * pos.y)
    }

    operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = Pair(this.first + other.first, this.second + other.second)

    fun part1b(input: List<String>): Int {
        return input
            .map { it.split(" ") }
            .map { it[0] to it[1].toInt() }
            .fold(Pair(0, 0)) { acc, ins ->
                when (ins.first) {
                    "up" -> acc + Pair(0, -ins.second)
                    "down" -> acc + Pair(0, ins.second)
                    "forward" -> acc + Pair(ins.second, 0)
                    else -> throw IllegalArgumentException("Unknown instruction")
                }
            }
            .let { it.first * it.second }
    }


    fun part2() {
        var aim = 0
        var pos = 0
        var depth = 0
        inputParsed.forEach {
            when (it.first) {
                "up" -> aim -= it.second
                "down" -> aim += it.second
                "forward" -> {
                    pos += it.second; depth += aim * it.second
                }
            }
        }
        println(pos * depth)
    }

    operator fun Triple<Int, Int, Int>.plus(other: Triple<Int, Int, Int>) = Triple(this.first + other.first, this.second + other.second, this.third + other.third)

    fun part2b(input: List<String>): Int = input
        .map { it.split(" ") }
        .map { it[0] to it[1].toInt() }
        .fold(Triple(0, 0, 0)) { (pos, depth, aim), (op, i) ->
            when (op) {
                "up" -> Triple(pos, depth, aim - i)
                "down" -> Triple(pos, depth, aim + i)
                "forward" -> Triple(pos + i, depth + aim * i, aim)
                else -> throw IllegalArgumentException("Unknown instruction")
            }
        }
        .let { (pos, depth, _aim) -> pos * depth }

}

fun main(args: Array<String>) {
    val d = Day2()
    d.part1()
    println(d.part1b(d.input))
    d.part2()
    println(d.part2b(d.input))
}
