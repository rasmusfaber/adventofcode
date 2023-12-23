package xyz.faber.adventofcode.year2023.day23

import xyz.faber.adventofcode.util.*
import java.util.*

class SlopeTopology<T>(val map: XYMap<T>) : DirectedGraph<Pos> {
  override fun getNeighbours(pos: Pos): Collection<Pos> =
    when (map[pos]) {
      '>' -> listOf(pos + Direction.E)
      'v' -> listOf(pos + Direction.S)
      '<' -> listOf(pos + Direction.W)
      '^' -> listOf(pos + Direction.N)
      '.' -> pos.adjacentNonDiagonal().filter { map.isInBounds(it) && map[it] != '#' }
      else -> throw IllegalArgumentException("Unexpected")
    }
}

class NoSlopeTopology<T>(val map: XYMap<T>) : DirectedGraph<Pos> {
  override fun getNeighbours(pos: Pos): Collection<Pos> = pos.adjacentNonDiagonal().filter { map.isInBounds(it) && map[it] != '#' }
}

class Day23 : AdventSolutionWithTransform<Int, CharXYMap>() {
  override fun transformAll(input: String) = input.toXYMap()

  override fun part1(input: CharXYMap): Int {
    val start = input.xrange.map { Pos(it, 0) }.first { input[it] == '.' }
    val end = input.xrange.map { Pos(it, input.maxy) }.first { input[it] == '.' }
    val topology = SlopeTopology(input)
    val positions = listOf(start, end) + input.filter {
      it.value == '.' && it.pos.adjacentNonDiagonal().count { input.isInBounds(it) && input[it] != '#' } > 2
    }.map { it.pos }
    val dwg = topology.toDirectedWeightedGraph(positions)
    val topologicalSort = topologicalSort(dwg.withoutWeights(), positions)
    val stack = LinkedList(topologicalSort)
    val distances = mutableMapOf<Pos, Int>()
    distances[start] = 0

    while (stack.isNotEmpty()) {
      val v = stack.pop()
      val dist = distances[v]
      if (dist != null) {
        for (edge in dwg.getNeighbours(v)) {
          val dist2 = distances[edge.to]
          if (dist2 == null || dist2 < dist + edge.cost) {
            distances[edge.to] = dist + edge.cost
          }
        }
      }
    }
    return distances[end]!!
  }

  fun getLongestPath(node: Pos, graph: DirectedWeightedGraph<Pos>, current: Int, distances: MutableMap<Pos, Int>, visited: MutableSet<Pos>){
    if(node in visited){
      return
    }
    visited += node
    if(distances[node]?:0<current){
      distances[node] = current
    }
    for (edge in graph.getNeighbours(node)) {
      getLongestPath(edge.to, graph, current + edge.cost, distances, visited)
    }
    visited -= node
  }

  override fun part2(input: CharXYMap): Int {
    val start = input.xrange.map { Pos(it, 0) }.first { input[it] == '.' }
    val end = input.xrange.map { Pos(it, input.maxy) }.first { input[it] == '.' }
    val topology = NoSlopeTopology(input)
    val positions = listOf(start, end) + input.filter {
      it.value == '.' && it.pos.adjacentNonDiagonal().count { input.isInBounds(it) && input[it] != '#' } > 2
    }.map { it.pos }
    val dwg = topology.toDirectedWeightedGraph(positions)
    val distances = mutableMapOf<Pos, Int>()
    val visited = mutableSetOf<Pos>()
    distances[start] = 0

    getLongestPath(start, dwg, 0, distances, visited)

    return distances[end]!!
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 23, Day23()).run()

}
