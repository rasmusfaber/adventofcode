package xyz.faber.adventofcode.year2024.day10

import xyz.faber.adventofcode.util.*

class Day10 : AdventSolution<Int>() {
    override fun part1(input: List<String>): Int {
        val map = input.toIntXYMap()
        val graph = object : DirectedGraph<Pos> {
            override fun getNeighbours(pos: Pos): Collection<Pos> {
                return pos.adjacentNonDiagonal().filter { map[it] == map[pos] + 1 }
            }
        }
        val trailheads = map.positions().filter { map[it] == 0 }.toSet()
        val goals = map.positions().filter { map[it] == 9 }
        val counts = trailheads.map { bfs(graph, it).count { it in goals } }
        return counts.sum()
    }

    override fun part2(input: List<String>): Int {
        val map = input.toIntXYMap()
        val inversegraph = object : DirectedGraph<Pos> {
            override fun getNeighbours(pos: Pos): Collection<Pos> {
                return pos.adjacentNonDiagonal().filter { map[it] == map[pos] - 1 }
            }
        }
        val trailheads = map.positions().filter { map[it] == 0 }.toSet()
        val goals = map.positions().filter { map[it] == 9 }
        val pathsFromGoals = goals.map{pathCount(it, inversegraph, trailheads)}
        return pathsFromGoals.sum()
    }

    private fun pathCount(start: Pos, inversegraph: DirectedGraph<Pos>, trailheads: Set<Pos>): Int {
        val mem = mutableMapOf<Pos, Int>()
        return pathCount(start, inversegraph, trailheads, mem)
    }

    private fun pathCount(
        start: Pos,
        inversegraph: DirectedGraph<Pos>,
        trailheads: Set<Pos>,
        mem: MutableMap<Pos, Int>
    ): Int {
        if(start in mem){
            return mem[start]!!
        }
        if(start in trailheads){
            return 1
        }
        val count = inversegraph.getNeighbours(start).map { pathCount(it, inversegraph, trailheads, mem) }.sum()
        mem[start] = count
        return count
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 10, Day10()).run()

}
