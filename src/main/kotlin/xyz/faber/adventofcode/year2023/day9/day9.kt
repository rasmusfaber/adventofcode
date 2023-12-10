package xyz.faber.adventofcode.year2023.day9

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Day9 : AdventSolution<Int>() {

  fun nextNumber(input: List<Int>): Int {
    if (input.all { it == 0 }) {
      return 0
    }
    val differences = input.windowed(2).map { it[1] - it[0] }
    val nextDifference = nextNumber(differences)
    return input.last() + nextDifference
  }

  override fun part1(input: List<String>): Int {
    return input.map { it.split(' ').map { it.toInt() } }
      .map { nextNumber(it) }
      .sum()
  }

  fun prevNumber(input: List<Int>): Int {
    if (input.all { it == 0 }) {
      return 0
    }
    val differences = input.windowed(2).map { it[1] - it[0] }
    val prevDifference = prevNumber(differences)
    return input.first() - prevDifference
  }

  override fun part2(input: List<String>): Int {
    return input.map { it.split(' ').map { it.toInt() } }
      .map { prevNumber(it) }
      .sum()
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 9, Day9()).run()

}
