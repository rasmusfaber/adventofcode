package xyz.faber.adventofcode.year2023.day18

import xyz.faber.adventofcode.util.*
import java.util.*
import java.util.function.Predicate
import kotlin.math.abs

class Day18 : AdventSolution<Long>() {
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

  override fun part1(input: List<String>): Long {
    val border = mutableSetOf<Pos>()
    var pos = Pos(0, 0)
    border += pos
    input.map { it.split(' ') }.forEach { (d, r, _) ->
      val dir = d.toDirection()
      val repeat = r.toInt()
      (1..repeat).forEach {
        pos += dir
        border += pos
      }
    }
    val minx = border.minOf { it.x }!! - 1
    val maxx = border.maxOf { it.x }!! + 1
    val miny = border.minOf { it.y }!! - 1
    val maxy = border.maxOf { it.y }!! + 1
    val outside = floodfill(minx, maxx, miny, maxy) { it in border }

    return ((maxx - minx + 1) * (maxy - miny + 1) - outside.size).toLong()
  }

  fun parseHexInstruction(i: String): Pair<Direction, Int> {
    val dir = when (i[7]) {
      '0' -> Direction.E
      '1' -> Direction.S
      '2' -> Direction.W
      '3' -> Direction.N
      else -> throw IllegalArgumentException("Bad direction")
    }
    val repeat = i.substring(2, i.length - 2).toInt(16)
    return dir to repeat
  }

  override fun part2(input: List<String>): Long {
    val borderPoints = mutableListOf<Pos>()
    var pos = Pos(0, 0)
    borderPoints += pos
    val instructions = input.map { it.split(' ') }
      .map { (_, _, i) -> parseHexInstruction(i) }
    var prevTurn = (instructions.last().first to instructions.first().first).toTurn()
    instructions.windowed(2).forEach { (ins1, ins2) ->
      val turn = (ins1.first to ins2.first).toTurn()
      val r = if (prevTurn == Turn.RIGHT && turn == Turn.RIGHT) ins1.second + 1
      else if (prevTurn == Turn.LEFT && turn == Turn.LEFT) ins1.second - 1
      else if (prevTurn == Turn.RIGHT && turn == Turn.LEFT) ins1.second
      else if (prevTurn == Turn.LEFT && turn == Turn.RIGHT) ins1.second
      else throw IllegalArgumentException("Not handled")
      pos = pos.move(ins1.first, r)
      borderPoints += pos
      prevTurn = turn
    }
    return abs(borderPoints.indices.sumOf { borderPoints[it].x.toLong() * borderPoints[(it + 1) % borderPoints.size].y.toLong() - borderPoints[(it + 1) % borderPoints.size].x.toLong() * borderPoints[it].y.toLong() }) / 2
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 18, Day18()).run()

}
