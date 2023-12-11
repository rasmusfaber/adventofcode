package xyz.faber.adventofcode.year2023.day11

import xyz.faber.adventofcode.util.*
import kotlin.math.max
import kotlin.math.min

class Day11 : AdventSolutionWithTransform<Long, CharXYMap>() {
  override fun transformAll(input: String) = input.toXYMap()

  fun solve(input: CharXYMap, expansion: Long): Long {
    val emptyColumns = input.xrange.filter { x -> input.yrange.all { y -> input[x, y] == '.' } }
    val emptyRows = input.yrange.filter { y -> input.xrange.all { x -> input[x, y] == '.' } }
    val galaxies = input.positions().filter { input[it] == '#' }
    val pairs = galaxies.combinations(2)

    return pairs.sumOf {
      distance(it.first().x, it.last().x, emptyColumns, expansion) +
        distance(it.first().y, it.last().y, emptyRows, expansion)
    }
  }

  private fun distance(a: Int, b: Int, empty: Collection<Int>, expansion: Long): Long {
    val min = min(a, b)
    val max = max(a, b)
    return max - min + (expansion - 1) * empty.count { it in min..max }
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
