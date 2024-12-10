package xyz.faber.adventofcode.year2024.day1

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import kotlin.math.abs

class Day1 : AdventSolution<Int>() {
  override fun part1(input: List<String>): Int {
    val split = input.map { it.split("   ").map { it.toInt() } }
    val left = split.map { it[0] }
    val right = split.map { it[1] }
    val leftSorted = left.sorted()
    val rightSorted = right.sorted()
    val res = leftSorted.zip(rightSorted).map { abs(it.first - it.second) }.sum()
    return res
  }

  override fun part2(input: List<String>): Int {
    val split = input.map { it.split("   ").map { it.toInt() } }
    val left = split.map { it[0] }
    val right = split.map { it[1] }
    val score = left.map{it * right.count { it2 -> it2 == it }}.sum()
    return score
  }
}

fun main(args: Array<String>) {
  AdventRunner(2024, 1, Day1()).run()

}
