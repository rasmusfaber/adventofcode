package xyz.faber.adventofcode.year2023.day1

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Day1 : AdventSolution<Int>() {
  override fun part1(input: List<String>): Int {
    return input.map { "" + it.first { it in '0'..'9' } + it.last { it in '0'..'9' } }.sumOf { it.toInt() }
  }

  val numbers = (0..9).map { it.toString() to it }
    .plus(
      listOf("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
        .mapIndexed { index, it -> it to index }
    )

  fun hasNumberAtIndex(s: String, i: Int): Int? {
    val num = numbers.firstOrNull { s.startsWith(it.first, i) } ?: return null
    return num.second
  }

  override fun part2(input: List<String>): Int {
    return input.map {
      it.indices.mapNotNull { i -> hasNumberAtIndex(it, i) }.first() * 10 + it.indices.reversed().mapNotNull { i -> hasNumberAtIndex(it, i) }.first()
    }.sum()
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 1, Day1()).run()
}
