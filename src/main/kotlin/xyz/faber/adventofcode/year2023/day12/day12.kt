package xyz.faber.adventofcode.year2023.day12

import arrow.core.Tuple5
import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Day12 : AdventSolution<Long>() {

  fun possibleArrangements(s: String, conditions: List<Int>) = possibleArrangements(s, 0, null, conditions, 0)

  val cache = mutableMapOf<Tuple5<String, Int, Int?, List<Int>, Int>, Long>()

  tailrec fun possibleArrangements(s: String, ps: Int, condition: Int?, conditions: List<Int>, pc: Int): Long {
    if (ps >= s.length) {
      if ((condition == 0 || condition == null) && pc >= conditions.size) {
        return 1
      } else {
        return 0
      }
    }
    when (s[ps]) {
      '.' -> {
        if (condition == 0 || condition == null) {
          return possibleArrangements(s, ps + 1, null, conditions, pc)
        } else {
          return 0
        }
      }

      '#' -> {
        if (condition == 0) {
          return 0
        }
        if (condition == null) {
          if (pc < conditions.size) {
            return possibleArrangements(s, ps + 1, conditions[pc] - 1, conditions, pc + 1)
          } else {
            return 0
          }
        } else {
          return possibleArrangements(s, ps + 1, condition - 1, conditions, pc)
        }
      }

      '?' -> {
        if (condition == null) {
          if (pc < conditions.size) {
            val cacheKey = Tuple5(s, ps, condition, conditions, pc)
            val cached = cache[cacheKey]
            if (cached != null) {
              return cached
            }
            val res1 = possibleArrangements(s, ps + 1, null, conditions, pc)
            val res2 = possibleArrangements(s, ps + 1, conditions[pc] - 1, conditions, pc + 1)
            cache[cacheKey] = res1 + res2
            return res1 + res2
          } else {
            return possibleArrangements(s, ps + 1, 0, conditions, pc)
          }
        } else {
          if (condition > 0) {
            return possibleArrangements(s, ps + 1, condition - 1, conditions, pc)
          } else {
            return possibleArrangements(s, ps + 1, null, conditions, pc)
          }
        }
      }

      else -> throw IllegalArgumentException("Bad character")
    }
  }

  override fun part1(input: List<String>): Long {
    return input.map {
      it.split(' ')
        .let { split -> split[0] to split[1].split(',').map { it.toInt() } }
    }
      .sumOf {
        val r = possibleArrangements(it.first, it.second)
        r
      }
  }

  override fun part2(input: List<String>): Long {
    return input.map {
      it.split(' ')
        .let { split -> split[0] to split[1].split(',').map { it.toInt() } }
    }
      .sumOf {
        val expandedString = (1..5).map { _ -> it.first }.joinToString("?")
        val expandedConditions = (1..5).flatMap { _ -> it.second }
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
