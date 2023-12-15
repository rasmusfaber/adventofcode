package xyz.faber.adventofcode.year2023.day14

import xyz.faber.adventofcode.util.*

class Day14 : AdventSolutionWithTransform<Int, CharXYMap>() {
  override fun transformAll(input: String): CharXYMap {
    return input.toXYMap()
  }

  override fun part1(input: CharXYMap): Int {
    val staticRocks = input.filter { it.value == '#' }.map { it.pos }.toSet()
    val movingRocks = input.filter { it.value == 'O' }.map { it.pos }.toSet()
    val movedRocks = tilt(input, movingRocks, staticRocks, Direction.N)

    return movedRocks.sumOf { input.dimy - it.y }
  }

  private fun tilt(map: CharXYMap, movingRocks: Set<Pos>, staticRocks: Set<Pos>, dir: Direction): Set<Pos> {
    val sortedMovingRocks = movingRocks.sortedBy {
      when (dir) {
        Direction.N -> it.y
        Direction.W -> it.x
        Direction.S -> -it.y
        Direction.E -> -it.x
      }
    }
    val movedRocks = sortedMovingRocks.fold(mutableSetOf<Pos>()) { cur, rock ->
      val newPos = furthestFreePos(map, rock, staticRocks, cur, dir)
      cur += newPos
      cur
    }
    return movedRocks
  }

  private fun furthestFreePos(
    map: CharXYMap,
    rock: Pos,
    staticRocks: Set<Pos>,
    movedRocks: Set<Pos>,
    dir: Direction
  ): Pos {
    return sequence {
      var p = rock
      while (map.isInBounds(p)) {
        yield(p)
        p += dir
      }
    }.first { p ->
      val p2 = p + dir
      p !in staticRocks && p !in movedRocks && (!map.isInBounds(p2) || p2 in staticRocks || p2 in movedRocks)
    }
  }

  private fun tiltCycle(map: CharXYMap, movingRocks: Set<Pos>, staticRocks: Set<Pos>): Set<Pos> {
    var res = tilt(map, movingRocks, staticRocks, Direction.N)
    res = tilt(map, res, staticRocks, Direction.W)
    res = tilt(map, res, staticRocks, Direction.S)
    return tilt(map, res, staticRocks, Direction.E)
  }

  override fun part2(input: CharXYMap): Int {
    val staticRocks = input.filter { it.value == '#' }.map { it.pos }.toSet()
    val movingRocks = input.filter { it.value == 'O' }.map { it.pos }.toSet()
    val (lam, mu) = findCycle(movingRocks, {tiltCycle(input, it, staticRocks)} , memo = true)
    val iterations = (1_000_000_000-mu)%lam + mu
    val final = (1..iterations).fold(movingRocks){acc, _ -> tiltCycle(input, acc, staticRocks)}
    return final.sumOf { input.dimy - it.y }
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 14, Day14()).run()

}
