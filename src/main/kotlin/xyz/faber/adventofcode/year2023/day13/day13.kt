package xyz.faber.adventofcode.year2023.day13

import xyz.faber.adventofcode.util.*
import kotlin.math.max
import kotlin.math.min

class Day13 : AdventSolutionWithTransform<Int, List<CharXYMap>>() {
  override fun transformAll(input: List<String>): List<CharXYMap> {
    return input.split(listOf("")).map {
      it.toXYMap()
    }
  }

  private fun findVerticalMirror(m: CharXYMap, wrong: Int = 0): Int? {
    return m.xrange.drop(1).firstOrNull { mirrorx: Int ->
      m.yrange.count { y ->
        val width = min(mirrorx, m.dimx - mirrorx)
        (0 until width).all { dx -> m[mirrorx + dx, y] == m[mirrorx - 1 - dx, y] }
      } == m.dimy - wrong
    }
  }

  private fun findHorizontalMirror(m: CharXYMap, wrong: Int = 0) = findVerticalMirror(m.transpose(), wrong)

  override fun part1(input: List<CharXYMap>): Int {
    return input.sumOf {
      findVerticalMirror(it) ?: (findHorizontalMirror(it)!! * 100)
    }
  }

  override fun part2(input: List<CharXYMap>): Int {
    return input.sumOf {
      findVerticalMirror(it, wrong = 1) ?: (findHorizontalMirror(it, wrong = 1)!! * 100)
    }
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 13, Day13()).run()

}
