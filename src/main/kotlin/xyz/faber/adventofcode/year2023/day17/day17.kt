package xyz.faber.adventofcode.year2023.day17

import xyz.faber.adventofcode.util.*

data class PosAndRepetitions(val pos: Pos, val dir: Direction, val rep: Int)

class Day17 : AdventSolutionWithTransform<Int, IntXYMap>() {
  override fun transformAll(input: String): IntXYMap = input.toIntXYMap()

  fun createEdge(pos: PosAndRepetitions, turn: String?, map: IntXYMap): Edge<PosAndRepetitions>? {
    val nextDir = if (turn != null) pos.dir.turn(turn) else pos.dir
    val nextPos = pos.pos + nextDir
    if (!map.isInBounds(nextPos)) {
      return null
    }
    val nextRep = if (turn == null) pos.rep + 1 else 1
    if (nextRep > 3) {
      return null
    }
    return Edge(pos, PosAndRepetitions(nextPos, nextDir, nextRep), map[nextPos])
  }

  override fun part1(input: IntXYMap): Int {
    val end = Pos(input.maxx, input.maxy)
    val path = aStarPredicate(object : DirectedWeightedGraph<PosAndRepetitions> {
      override fun getNeighbours(pos: PosAndRepetitions): Collection<Edge<PosAndRepetitions>> {
        return listOf(
          createEdge(pos, "L", input),
          createEdge(pos, null, input),
          createEdge(pos, "R", input)
        ).filterNotNull()
      }
    },
      { manhattanDistance(it.pos, end) },
      PosAndRepetitions(Pos(0, 0), Direction.E, 0),
      { it.pos == end }
    )!!
    return path.totalCost
  }

  fun createHeavyStraightEdge(pos: PosAndRepetitions, turn: String?, map: IntXYMap): Edge<PosAndRepetitions>? {
    val nextDir = pos.dir
    val nextPos = pos.pos + nextDir
    if (!map.isInBounds(nextPos)) {
      return null
    }
    val nextRep = pos.rep + 1
    if (nextRep > 10) {
      return null
    }
    return Edge(pos, PosAndRepetitions(nextPos, nextDir, nextRep), map[nextPos])
  }

  fun createHeavyTurnEdge(pos: PosAndRepetitions, turn: String, map: IntXYMap): Edge<PosAndRepetitions>? {
    if (pos.rep != 0 && pos.rep < 4) {
      return null
    }
    val nextDir = pos.dir.turn(turn)
    val nextPos = pos.pos + nextDir
    if (!map.isInBounds(nextPos)) {
      return null
    }
    val nextRep = 1
    return Edge(
      pos,
      PosAndRepetitions(nextPos, nextDir, nextRep),
      map[nextPos]
    )
  }

  override fun part2(input: IntXYMap): Int {
    val end = Pos(input.maxx, input.maxy)
    val path = aStarPredicate(object : DirectedWeightedGraph<PosAndRepetitions> {
      override fun getNeighbours(pos: PosAndRepetitions): Collection<Edge<PosAndRepetitions>> {
        return listOf(
          createHeavyTurnEdge(pos, "L", input),
          createHeavyStraightEdge(pos, null, input),
          createHeavyTurnEdge(pos, "R", input)
        ).filterNotNull()
      }
    },
      { manhattanDistance(it.pos, end) },
      PosAndRepetitions(Pos(0, 0), Direction.E, 0),
      { it.pos == end }
    )!!
    return path.totalCost
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 17, Day17()).run()

}
