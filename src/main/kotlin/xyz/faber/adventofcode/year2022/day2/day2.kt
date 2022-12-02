package xyz.faber.adventofcode.year2022.day2

import xyz.faber.adventofcode.util.getInputFromLines

class Day2 {
    val input = getInputFromLines(2022, 2)

    fun part1() {
        val res = input.map { it.split(" ").map { it[0] } }
            .map { (ac, bc) ->
                val a = ac - 'A'
                val b = bc - 'X'
                val score1 = b + 1
                val score2 = if (a == b) 3 else if ((a + 1) % 3 == b) 6 else 0
                score1 + score2
            }.sum()
        println(res)
    }

    fun part2() {
        val res = input.map { it.split(" ").map { it[0] } }
            .map { (ac, cc) ->
            val a = ac - 'A'
            val c = cc - 'X'
            val b = if (c == 0) (a + 2) % 3 else if (c == 1) a else (a + 1) % 3
            val score1 = b + 1
            val score2 = if (a == b) 3 else if ((a + 1) % 3 == b) 6 else 0
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
