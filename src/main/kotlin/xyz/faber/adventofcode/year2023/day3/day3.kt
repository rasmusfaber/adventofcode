package xyz.faber.adventofcode.year2023.dayN

import xyz.faber.adventofcode.util.*

class Day3 : AdventSolutionWithTransform<Int, CharXYMap>() {
  override fun transformAll(input: String): CharXYMap {
    return input.toXYMap()
  }

  override fun part1(input: CharXYMap): Int {
    val nonSymbols = ('0'..'9').toSet() + '.'
    return input.filter { it.value !in nonSymbols }
      .flatMap { attachedNumbers(input, it) }
      .sum()
  }

  private fun attachedNumbers(input: CharXYMap, symbol: MapEntry<Char>): List<Int> {
    val res = mutableListOf<Int>()
    val numbers = symbol.pos.adjacent().filter { input[it] in '0'..'9' }.toSet()
    val remainingNumbers = numbers.toMutableList()
    while (remainingNumbers.isNotEmpty()) {
      val number = remainingNumbers.removeFirst()
      var startx = number.x
      val y = number.y
      while (input[startx - 1, number.y] in '0'..'9') {
        startx--
      }
      var endx = number.x
      while (input[endx + 1, number.y] in '0'..'9') {
        endx++
      }
      var numberPositions = (startx..endx).map { Pos(it, y) }
      val value = numberPositions.map { input[it] }.joinToString("").toInt()
      res.add(value)
      remainingNumbers.removeAll(numberPositions)
    }
    return res
  }

  override fun part2(input: CharXYMap): Int {
    return input.filter { it.value == '*' }
      .map { attachedNumbers(input, it) }
      .filter { it.size == 2 }
      .map { it[0] * it[1] }
      .sum()
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 3, Day3()).run()

}
