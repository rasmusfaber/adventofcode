package xyz.faber.adventofcode.year2023.day16

import xyz.faber.adventofcode.util.*

data class Ray(val pos: Pos, val dir: Direction)

class Day16 : AdventSolutionWithTransform<Int, CharXYMap>() {
  override fun transformAll(input: String): CharXYMap = input.toXYMap()

  fun nextDir(ray: Ray, map: CharXYMap): List<Direction> {
    return when (map[ray.pos]) {
      '/' -> when (ray.dir) {
        Direction.N -> listOf(Direction.E)
        Direction.E -> listOf(Direction.N)
        Direction.S -> listOf(Direction.W)
        Direction.W -> listOf(Direction.S)
      }

      '\\' -> when (ray.dir) {
        Direction.N -> listOf(Direction.W)
        Direction.W -> listOf(Direction.N)
        Direction.S -> listOf(Direction.E)
        Direction.E -> listOf(Direction.S)
      }

      '|' -> when (ray.dir) {
        Direction.W, Direction.E -> listOf(Direction.S, Direction.N)
        else -> listOf(ray.dir)
      }

      '-' -> when (ray.dir) {
        Direction.N, Direction.S -> listOf(Direction.E, Direction.W)
        else -> listOf(ray.dir)
      }

      else -> listOf(ray.dir)
    }
  }

  fun next(ray: Ray, map: CharXYMap): List<Ray> {
    val nextDirs = nextDir(ray, map)
    return nextDirs.map { Ray(ray.pos + it, it) }
  }

  private fun energizedTiles(initialRay: Ray, map: CharXYMap): Int {
    var rays = listOf(initialRay)
    val visited = mutableSetOf<Ray>()
    while (rays.isNotEmpty()) {
      visited.addAll(rays)
      rays = rays.flatMap { next(it, map) }.filter { it !in visited }.filter { map.isInBounds(it.pos) }
    }
    val visitedPos = visited.map { it.pos }.distinct()
    return visitedPos.size
  }

  override fun part1(input: CharXYMap): Int {
    return energizedTiles(Ray(Pos(0, 0), Direction.E), input)
  }

  override fun part2(input: CharXYMap): Int {
    val initialRays = input.xrange.map { Ray(Pos(it, 0), Direction.S) } +
      input.xrange.map { Ray(Pos(it, input.maxy), Direction.N) } +
      input.yrange.map { Ray(Pos(0, it), Direction.E) } +
      input.yrange.map { Ray(Pos(input.maxx, it), Direction.W) }
    return initialRays.maxOf { energizedTiles(it, input) }
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 16, Day16()).run()

}
