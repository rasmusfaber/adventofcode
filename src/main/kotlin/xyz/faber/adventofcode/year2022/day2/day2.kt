package xyz.faber.adventofcode.year2022.day2

import xyz.faber.adventofcode.util.getInputFromLines

class Day2 {
    val input = getInputFromLines(2022, 2)

    fun part1() {
        val res = input
            .map { it.split(" ").map { it[0] } }
            .map { (a, b) -> a - 'A' to b - 'X' }
            .map { (move1, move2) ->
                val outcome = (move2 - move1 + 4) % 3
                val score1 = move2 + 1
                val score2 = outcome * 3
                score1 + score2
            }.sum()
        println(res)
    }

    fun part2() {
        val res = input
            .map { it.split(" ").map { it[0] } }
            .map { (a, c) -> a - 'A' to c - 'X' }
            .map { (move1, outcome) ->
                val move2 = (move1 + outcome + 2) % 3
                val score1 = move2 + 1
                val score2 = outcome * 3
                score1 + score2
            }.sum()
        println(res)
    }
}

fun main(args: Array<String>) {
    val d = Day2()
    d.part1()
    d.part2()

}
