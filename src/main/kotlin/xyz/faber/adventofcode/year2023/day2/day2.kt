package xyz.faber.adventofcode.year2023.day2

import arrow.core.Tuple3
import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolutionWithTransform

data class Game(val id: Int, val rounds: List<Round>)
data class Round(val red: Int, val green: Int, val blue: Int)

class Day2 : AdventSolutionWithTransform<Int, Game>() {
  override fun transformLine(input: String): Game {
    val parts = input.split(": ", "; ")
    val id = parts.first().split(' ')[1].toInt()
    return Game(id, parts.drop(1).map { transformRound(it) })
  }

  private fun transformRound(round: String): Round {
    val cubes = round.split(", ").map { it.split(' ') }
    return Round(countColor(cubes, "red"), countColor(cubes, "green"), countColor(cubes, "blue"))
  }

  private fun countColor(it: List<List<String>>, c: String): Int {
    return it.filter { it2: List<String> -> it2[1] == c }.sumOf { it2 -> it2[0].toInt() }
  }

  override fun part1(input: List<Game>): Int {
    val possible = input.filter {
      it.rounds.all { it.red <= 12 && it.green <= 13 && it.blue <= 14 }
    }
    return possible.sumOf { it.id }
  }

  override fun part2(input: List<Game>): Int {
    return input.sumOf { it.rounds.maxOf { it.red } * it.rounds.maxOf { it.green } * it.rounds.maxOf { it.blue } }
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 2, Day2()).run()

}
