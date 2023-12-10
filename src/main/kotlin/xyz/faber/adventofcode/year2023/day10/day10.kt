package xyz.faber.adventofcode.year2023.day10

import xyz.faber.adventofcode.util.*
import java.util.*
import java.util.function.Predicate

class Day10 : AdventSolutionWithTransform<Int, CharXYMap>() {
  override fun transformAll(input: String) = input.toXYMap()

  fun nextPos(input: CharXYMap, cur: Pos): List<Pos> {
    if (input[cur] == 'S') {
      val res = mutableListOf<Pos>()
      if (input[cur + Direction.N] in setOf('7', 'F', '|')) {
        res += (cur + Direction.N)
      }
      if (input[cur + Direction.E] in setOf('7', 'J', '-')) {
        res += (cur + Direction.E)
      }
      if (input[cur + Direction.S] in setOf('J', 'L', '|')) {
        res += (cur + Direction.S)
      }
      if (input[cur + Direction.W] in setOf('F', 'L', '-')) {
        res += (cur + Direction.W)
      }
      return res
    }
    return when (input[cur]) {
      '7' -> listOf(cur + Direction.W, cur + Direction.S)
      'J' -> listOf(cur + Direction.W, cur + Direction.N)
      'F' -> listOf(cur + Direction.E, cur + Direction.S)
      'L' -> listOf(cur + Direction.E, cur + Direction.N)
      '-' -> listOf(cur + Direction.W, cur + Direction.E)
      '|' -> listOf(cur + Direction.N, cur + Direction.S)
      else -> throw IllegalArgumentException("Bad ${input[cur]}")
    }

  }

  override fun part1(input: CharXYMap): Int {
    val startPos = input.single { it.value == 'S' }.pos
    val neighbours = nextPos(input, startPos)
    val path = walk(input, startPos, neighbours[0], mutableListOf<Pos>(startPos))
    return (path.size + 1) / 2
  }

  private tailrec fun walk(
    input: CharXYMap,
    from: Pos,
    pos: Pos,
    path: MutableList<Pos>
  ): List<Pos> {
    if (pos == path[0]) {
      return path
    }
    path += pos
    val nextPos = nextPos(input, pos).single { it != from }
    return walk(input, pos, nextPos, path)
  }

  private fun floodfill(minx: Int, maxx: Int, miny: Int, maxy: Int, blocked: Predicate<Pos>): Set<Pos> {
    val fill = mutableSetOf<Pos>()
    val stack = LinkedList<Pos>()
    stack.push(Pos(minx, miny))
    while (!stack.isEmpty()) {
      val pos = stack.pop()
      if (blocked.test(pos)) continue
      if (pos in fill) continue
      if (pos.x !in minx..maxx || pos.y !in miny..maxy) continue
      fill.add(pos)
      pos.adjacentNonDiagonal().forEach { stack.push(it) }
    }
    return fill
  }

  override fun part2(input: CharXYMap): Int {
    val startPos = input.single { it.value == 'S' }.pos
    val neighbours = nextPos(input, startPos)
    val path = walk(input, startPos, neighbours[0], mutableListOf<Pos>(startPos))
    val pathDoubled = (path + startPos).windowed(2).flatMap {
      listOf(
        Pos(it[0].x * 2, it[0].y * 2),
        Pos(it[0].x + it[1].x, it[0].y + it[1].y),
        Pos(it[1].x * 2, it[1].y * 2)
      )
    }
    val pathDoubledSet = pathDoubled.toSet()
    val outsideDoubled = floodfill(-1, input.maxx * 2 + 1, -1, input.maxy * 2 + 1) { Pos(it.x, it.y) in pathDoubledSet }
    val inside = input.positions().filter { it !in path && Pos(it.x * 2, it.y * 2) !in outsideDoubled }.toSet()
    return inside.size // 18648 too high // 3839 too high
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 10, Day10()).run()

}
