package xyz.faber.adventofcode.year2023.day25

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Day25 : AdventSolution<Int>() {
  private fun bfs(
    residualGraph: Map<String, Set<String>>,
    source: String,
    sink: String,
    parent: MutableMap<String, String?>
  ): Boolean {
    val visited = mutableSetOf<String>()
    val queue = ArrayDeque<String>()

    queue.add(source)
    visited.add(source)
    parent[source] = null

    while (queue.isNotEmpty()) {
      val u = queue.removeFirst()
      residualGraph[u]?.forEach { v ->
        if (v !in visited) {
          queue.add(v)
          parent[v] = u
          visited.add(v)
        }
      }
    }

    return sink in visited
  }

  fun minCut(
    source: String,
    sink: String,
    graph: Map<String, Set<String>>
  ): Pair<Set<Pair<String, String>>, MutableSet<String>> {
    // Initialize residual graph
    val residualGraph = graph.mapValues { it.value.toMutableSet() }

    // Augment the flow while there is a path from source to sink
    val parent = mutableMapOf<String, String?>()
    while (bfs(residualGraph, source, sink, parent)) {
      var v = sink
      while (v != source) {
        val u = parent[v]!!
        residualGraph[u]!! -= v
        residualGraph[v]!! += v
        v = u
      }
    }

    // Find vertices reachable from source
    val reachable = mutableSetOf<String>()
    val queue = ArrayDeque<String>()
    queue.add(source)

    while (queue.isNotEmpty()) {
      val u = queue.removeFirst()
      if (u !in reachable) {
        reachable.add(u)
        residualGraph[u]?.forEach { v ->
          queue.add(v)
        }
      }
    }

    // Edges from reachable to non-reachable vertices are the min cut
    return graph.filterKeys { it in reachable }
      .flatMap { (u, neighbors) -> neighbors.mapNotNull { v -> if (v !in reachable) u to v else null } }
      .toSet() to reachable
  }


  override fun part1(input: List<String>): Int {
    val connections =
      input.map { it.split(": ") }.flatMap { (a, l) -> l.split(' ').flatMap { b -> listOf(a to b, b to a) } }
    val nodes = connections.flatMap { (a, b) -> listOf(a, b) }.toSet()
    val graph = nodes.map { it to mutableSetOf<String>() }.toMap().toMutableMap()
    for ((a, b) in connections) {
      graph[a]!!.add(b)
      graph[b]!!.add(a)
    }
    do {
      val source = nodes.random()
      val sink = nodes.random()
      val (minCut, reachable) = minCut(source, sink, graph)
      if (minCut.size == 3) return reachable.size * (nodes.size - reachable.size)
    } while (true)
  }

  override fun part2(input: List<String>): Int {
    throw NotImplementedError()
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 25, Day25()).run()

}
