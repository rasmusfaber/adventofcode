package xyz.faber.adventofcode.year2023.day12

import arrow.core.Tuple5
import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.AdventSolutionWithTransform

class Day12 : AdventSolutionWithTransform<Long, Pair<String, List<Int>>>() {

  override fun transformLine(input: String): Pair<String, List<Int>> {
    return input.split(' ')
      .let { split -> split[0] to split[1].split(',').map { it.toInt() } }
  }

  fun possibleArrangements(s: String, conditions: List<Int>) = possibleArrangements(s, 0, null, conditions, 0)

  val cache = mutableMapOf<Tuple5<String, Int, Int?, List<Int>, Int>, Long>()

  tailrec fun possibleArrangements(s: String, ps: Int, condition: Int?, conditions: List<Int>, pc: Int): Long {
    if (ps >= s.length) {
      return if ((condition == 0 || condition == null) && pc >= conditions.size) {
        1
      } else {
        0
      }
    }
    return when (s[ps]) {
      '.' -> handleWorking(condition, s, ps, conditions, pc)
      '#' -> handleBroken(condition, s, ps, conditions, pc)
      '?' -> handleUnknown(condition, s, ps, conditions, pc)
      else -> throw IllegalArgumentException("Bad character")
    }
  }

  private fun handleWorking(condition: Int?, s: String, ps: Int, conditions: List<Int>, pc: Int): Long {
    return if (condition == 0 || condition == null) {
      possibleArrangements(s, ps + 1, null, conditions, pc)
    } else {
      0
    }
  }

  private fun handleBroken(condition: Int?, s: String, ps: Int, conditions: List<Int>, pc: Int): Long {
    return if (condition == 0) {
      0
    } else if (condition == null) {
      if (pc < conditions.size) {
        possibleArrangements(s, ps + 1, conditions[pc] - 1, conditions, pc + 1)
      } else {
        0
      }
    } else {
      possibleArrangements(s, ps + 1, condition - 1, conditions, pc)
    }
  }

  private fun handleUnknown(condition: Int?, s: String, ps: Int, conditions: List<Int>, pc: Int): Long {
    if (condition == null) {
      if (pc >= conditions.size) {
        return possibleArrangements(s, ps + 1, 0, conditions, pc)
      }
      val cacheKey = Tuple5(s, ps, condition, conditions, pc)
      return cache.getOrElse(cacheKey) {
        val res1 = possibleArrangements(s, ps + 1, null, conditions, pc)
        val res2 = possibleArrangements(s, ps + 1, conditions[pc] - 1, conditions, pc + 1)
        cache[cacheKey] = res1 + res2
        return res1 + res2
      }
    } else {
      if (condition > 0) {
        return possibleArrangements(s, ps + 1, condition - 1, conditions, pc)
      } else {
        return possibleArrangements(s, ps + 1, null, conditions, pc)
      }
    }
  }


  override fun part1(input: List<Pair<String, List<Int>>>): Long {
    return input.sumOf { (string, conditions) -> possibleArrangements(string, conditions) }
  }

  override fun part2(input: List<Pair<String, List<Int>>>): Long {
    return input.sumOf { (string, conditions) ->
      val expandedString = (1..5).map { _ -> string }.joinToString("?")
      val expandedConditions = (1..5).flatMap { _ -> conditions }
      possibleArrangements(
        expandedString,
        expandedConditions
      )
    }
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 12, Day12()).run()

}
