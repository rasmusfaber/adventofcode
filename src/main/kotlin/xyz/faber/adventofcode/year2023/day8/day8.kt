package xyz.faber.adventofcode.year2023.day8

import arrow.core.Tuple3
import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.lcm
import xyz.faber.adventofcode.util.toMap

class Day8 : AdventSolution<Long>() {
  val regex = "(...) = \\((...), (...)\\)".toRegex()

  override fun part1(input: List<String>): Long {
    val path = input.first()
    val network = input.drop(2)
      .map { regex.matchEntire(it)!!.groupValues }
      .map { it[1] to (it[2] to it[3]) }
      .toMap()
    if ("ZZZ" !in network) {
      return -1
    }
    var c = 0
    var node = "AAA"
    while (node != "ZZZ") {
      when (path[c % path.length]) {
        'L' -> node = network[node]!!.first
        'R' -> node = network[node]!!.second
      }
      c++
    }
    return c.toLong()
  }

  override fun part2(input: List<String>): Long {
    val path = input.first()
    val network = input.drop(2)
      .map { regex.matchEntire(it)!!.groupValues }
      .map { it[1] to (it[2] to it[3]) }
      .toMap()
    var nodes = network.keys.filter { it[2] == 'A' }
    val cycles = nodes.map { cycleLength(path, network, it) }
    if (cycles.any { it.c.size != 1 }) {
      // Bad assumptions
      return -1
    }
    val cycles2 = cycles.map { it.a + it.c[0] to it.b }
    if (cycles2.any { it.first != it.second }) {
      // Bad assumptions
      return -1
    }

    return lcm(cycles2.map { it.first })
  }

  private fun cycleLength(
    path: String,
    network: Map<String, Pair<String, String>>,
    start: String
  ): Tuple3<Long, Long, List<Long>> {
    var node = start
    var visited = mutableMapOf<Pair<String, Int>, Long>()
    var c = 0L
    var p = 0
    var foundends = mutableListOf<Long>()
    while (node to p !in visited) {
      if (node[2] == 'Z') {
        foundends += c
      }
      visited[node to p] = c
      when (path[p]) {
        'L' -> node = network[node]!!.first
        'R' -> node = network[node]!!.second
      }
      c++
      p = (c % path.length).toInt()
    }
    val cyclestart = visited[node to p]!!
    return Tuple3(cyclestart, c - cyclestart, foundends.map { it - cyclestart })
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 8, Day8()).run()

}
