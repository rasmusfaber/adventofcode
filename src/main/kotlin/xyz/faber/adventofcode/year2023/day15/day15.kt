package xyz.faber.adventofcode.year2023.day15

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.AdventSolutionWithTransform

class Day15 : AdventSolutionWithTransform<Int, List<String>>() {

  override fun transformAll(input: String): List<String> {
    return input.trim().split(',').map { it }
  }

  private fun hash(s: String): Int {
    return s.fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }
  }

  override fun part1(input: List<String>): Int {
    return input.sumOf { hash(it) }
  }

  override fun part2(input: List<String>): Int {
    val boxes = Array(256) { linkedMapOf<String, Int>() }
    val regex = "([a-z]*)([-=])([0-9])?".toRegex()
    input.map { regex.matchEntire(it)!!.groupValues.drop(1) }
      .forEach {
        val hash = hash(it[0])
        val box = boxes[hash]
        when (it[1]) {
          "=" -> box[it[0]] = it[2].toInt()
          "-" -> box.remove(it[0])
          else -> throw IllegalArgumentException("Bad "+it[1])
        }
      }
    return boxes.withIndex().sumOf { box ->
      box.value.values.withIndex().sumOf { lens ->
        (1 + box.index) * (1 + lens.index) * lens.value
      }
    }
  }

}

fun main(args: Array<String>) {
  AdventRunner(2023, 15, Day15()).run()

}
