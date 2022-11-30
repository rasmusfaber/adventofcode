package xyz.faber.adventofcode.year2021.day21

import xyz.faber.adventofcode.util.getInputFromLines
import xyz.faber.adventofcode.util.parse

class Day21 {
    val input = getInputFromLines(2021, 21)

    fun part1() {
        val start1 = input.parse("Player 1 starting position: (.*)").single().component1().toInt()
        val start2 = input.parse("Player 2 starting position: (.*)").single().component1().toInt()

        var score1 = 0
        var score2 = 0
        var pos1 = start1
        var pos2 = start2
        var die = 1
        var i = 0

        while (true) {
            pos1 = (pos1 + 3 * die + 3 - 1) % 10 + 1
            score1 += pos1
            die = (die + 3 - 1) % 100 + 1
            i += 3
            if (score1 >= 1000) {
                println(score2 * i)
                return
            }
            pos2 = (pos2 + 3 * die + 3 - 1) % 10 + 1
            score2 += pos2
            die = (die + 3 - 1) % 100 + 1
            i += 3
            if (score2 >= 1000) {
                println(score1 * i)
                return
            }
        }
    }

    data class State(val pos1: Int, val score1: Int, val pos2: Int, val score2: Int)

    val memo = mutableMapOf<State, Pair<Long, Long>>()

    fun results(state: State, roll: Int): Pair<Long, Long> {
        val newPos = (state.pos1 + roll - 1) % 10 + 1
        val newScore = state.score1 + newPos
        if (newScore >= 21) {
            return 1L to 0L
        } else {
            return results(State(state.pos2, state.score2, newPos, newScore))
        }
    }

    fun results(state: State): Pair<Long, Long> = memo.getOrPut(state) {
        val win3 = results(state, 3)
        val win4 = results(state, 4)
        val win5 = results(state, 5)
        val win6 = results(state, 6)
        val win7 = results(state, 7)
        val win8 = results(state, 8)
        val win9 = results(state, 9)

        val win1 = win3.second + 3 * win4.second + 6 * win5.second + 7 * win6.second +
                6 * win7.second + 3 * win8.second + win9.second
        val win2 = win3.first + 3 * win4.first + 6 * win5.first + 7 * win6.first +
                6 * win7.first + 3 * win8.first + win9.first

        return win1 to win2
    }


    fun part2() {
        val start1 = input.parse("Player 1 starting position: (.*)").single().component1().toInt()
        val start2 = input.parse("Player 2 starting position: (.*)").single().component1().toInt()
        val results = results(State(start1, 0, start2, 0))
        println(results.toList().maxOrNull()!!)
    }
}

fun main(args: Array<String>) {
    val d = Day21()

    d.part1()
    d.part2()
}
