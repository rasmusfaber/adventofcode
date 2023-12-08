package xyz.faber.adventofcode.year2023.day7

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Day7 : AdventSolution<Long>() {
  enum class Type {
    FIVE,
    FOUR,
    FULL,
    THREE,
    TWO,
    ONE,
    HIGH
  }

  fun getType(s: String, jokersActive: Boolean): Type {
    val jokers = if (jokersActive) {
      s.count { it == 'J' }
    } else {
      0
    }
    val counts =
      s.filter { !jokersActive || it != 'J' }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }
        .toList()
    val most = counts.getOrNull(0)?.value ?: 0
    val secondMost = counts.getOrNull(1)?.value ?: 0
    if (most + jokers == 5) {
      return Type.FIVE
    } else if (most + jokers == 4) {
      return Type.FOUR
    } else if (most + jokers == 3 && secondMost == 2) {
      return Type.FULL
    } else if (most + jokers == 3) {
      return Type.THREE
    } else if (most + jokers == 2 && secondMost == 2) {
      return Type.TWO
    } else if (most + jokers == 2) {
      return Type.ONE
    } else {
      return Type.HIGH
    }
  }

  fun translateToHex(s: String, jokersActive: Boolean): String {
    return s.map { translateToHex(it, jokersActive) }.joinToString("")
  }

  fun translateToHex(c: Char, jokersActive: Boolean): Char {
    return when (c) {
      in '2'..'9' -> c
      'T' -> 'a'
      'J' -> if (jokersActive) '1' else 'b'
      'Q' -> 'c'
      'K' -> 'd'
      'A' -> 'e'
      else -> throw IllegalArgumentException()
    }
  }

  fun score(input: List<String>, jokersActive: Boolean): Long {
    val splits = input.map { it.split(' ') }
    val sorted = splits.sortedWith(
      compareByDescending<List<String>> { getType(it[0], jokersActive) }
        .thenBy { translateToHex(it[0], jokersActive) }
    )
    return sorted.withIndex().sumOf { (it.index + 1) * it.value[1].toLong() }
  }

  override fun part1(input: List<String>): Long {
    return score(input, false)
  }

  override fun part2(input: List<String>): Long {
    return score(input, true)
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 7, Day7()).run()

}
