package xyz.faber.adventofcode.year2023.day4

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Day4 : AdventSolution<Int>() {
  override fun part1(input: List<String>): Int {
    return input.map { it.split(": ")[1] }
      .map {
        it.split(" | ")
          .map { it.split(' ').filter { it.isNotBlank() }.map { it.toInt() } }
      }
      .sumOf {
        val winningNumbers = it[0].toSet()
        1 shl (it[1].count { it in winningNumbers } - 1)
      }
  }

  override fun part2(input: List<String>): Int {
    val wins = input.map { it.split(": ")[1] }
      .map {
        it.split(" | ")
          .map { it.split(' ').filter { it.isNotBlank() }.map { it.toInt() } }
      }
      .map {
        val winningNumbers = it[0].toSet()
        it[1].count { it in winningNumbers }
      }
    val counts = input.map { 1 }.toMutableList()

    for (i in counts.indices) {
      for (j in 1..wins[i]) {
        counts[i + j] += counts[i]
      }
    }

    return counts.sum()
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 4, Day4()).run()

}
