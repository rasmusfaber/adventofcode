package xyz.faber.adventofcode.year2023.day6

import com.marcinmoskala.math.product
import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import kotlin.math.floor
import kotlin.math.sqrt

class Day6 : AdventSolution<Long>() {
  fun roundStrictDown(d: Double): Long {
    val floor = floor(d)
    return if (floor == d) {
      (floor - 1).toLong()
    } else {
      floor.toLong()
    }
  }

  fun margin(time: Long, distance: Long): Long {
    val discriminant = time * time - 4 * distance
    if (time % 2 == 0L) {
      return roundStrictDown(sqrt(discriminant.toDouble()) / 2) * 2 + 1
    } else {
      return roundStrictDown((sqrt(discriminant.toDouble()) + 1) / 2) * 2
    }
  }

  override fun part1(input: List<String>): Long {
    val times = input[0].split(" ").filter { it.isNotBlank() }.drop(1).map { it.toLong() }
    val distances = input[1].split(" ").filter { it.isNotBlank() }.drop(1).map { it.toLong() }
    val margins = times.zip(distances).map { (time, distance) -> margin(time, distance) }
    return margins.product()
  }

  override fun part2(input: List<String>): Long {
    val time = input[0].split(" ").filter { it.isNotBlank() }.drop(1).joinToString("").toLong()
    val distance = input[1].split(" ").filter { it.isNotBlank() }.drop(1).joinToString("").toLong()
    val margin = margin(time, distance)
    return margin
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 6, Day6()).run()

}
