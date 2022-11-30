package xyz.faber.adventofcode.year2021.day4

import xyz.faber.adventofcode.util.getInputFromLines

class Day4 {
    val input = getInputFromLines(2021, 4)

    private fun parseBoard(lines: List<String>): Board {
        return Board(lines.map { it.split(" ").filter { it.isNotBlank() }.map { it.toInt() } }.flatten())
    }

    private class Board(val board: List<Int>) {
        fun won(drawn: List<Int>): Boolean {
            val drawnSet = drawn.toSet()
            return (0..4).any { x -> (0..4).all { y -> board[x + y * 5] in drawnSet } }
                    || (0..4).any { y -> (0..4).all { x -> board[x + y * 5] in drawnSet } }
        }

        fun score(drawn: List<Int>): Int {
            val drawnSet = drawn.toSet()
            return board.filter { it !in drawnSet }.sum() * drawn.last()
        }
    }

    fun part1(input: List<String>): Int {
        val numbers = input[0].split(",").map { it.toInt() }
        val boards = input.subList(1, input.size)
            .chunked(5)
            .map { parseBoard(it) }
        return (1 until numbers.size).map { numbers.subList(0, it) }
            .first { boards.any { b -> b.won(it) } }
            .let { it to boards.first { b -> b.won(it) } }
            .let { it.second.score(it.first) }
    }

    fun part2(input: List<String>): Int {
        val numbers = input[0].split(",").map { it.toInt() }
        val boards = input.subList(1, input.size)
            .chunked(5)
            .map { parseBoard(it) }
        return (1 until numbers.size).map { numbers.subList(0, it) }
            .first { boards.all { b -> b.won(it) } }
            .let { val before = it.subList(0, it.size - 1); it to boards.first { b -> !b.won(before) } }
            .let { it.second.score(it.first) }
    }

}

fun main(args: Array<String>) {
    val d = Day4()
    println(d.part1(d.input))
    println(d.part2(d.input))
}
