package xyz.faber.adventofcode.year2023.day11

import xyz.faber.adventofcode.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day11 : AdventSolutionWithTransform<Long, CharXYMap>() {
  override fun transformAll(input: String) = input.toXYMap()

  fun solve(input: CharXYMap, expansion: Long): Long {
    val emptyColumns = input.xrange.filter { x -> input.yrange.all { y -> input[x, y] == '.' } }
    val emptyRows = input.yrange.filter { y -> input.xrange.all { x -> input[x, y] == '.' } }
    val stars = input.positions().filter { input[it] == '#' }
    val pairs = stars.indices.flatMap { i -> ((i + 1) until (stars.size)).map { j -> stars[i] to stars[j] } }

    val xdistances =
      input.xrange.flatMap { x1 ->
        input.xrange.map { x2 ->
          (x1 to x2) to abs(x1 - x2) + (expansion-1) * emptyColumns.count {
            it in min(
              x1,
              x2
            )..max(x1, x2)
          }
        }
      }
        .toMap()
    val ydistances =
      input.yrange.flatMap { y1 ->
        input.yrange.map { y2 ->
          (y1 to y2) to abs(y1 - y2) + (expansion-1) * emptyRows.count {
            it in min(
              y1,
              y2
            )..max(y1, y2)
          }
        }
      }
        .toMap()

    return pairs.sumOf { xdistances[it.first.x to it.second.x]!! + ydistances[it.first.y to it.second.y]!! }
  }

  override fun part1(input: CharXYMap): Long {
    return solve(input, 2)
  }

  override fun part2(input: CharXYMap): Long {
    return solve(input, 1000000)
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 11, Day11()).run()

}
