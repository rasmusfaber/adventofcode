package xyz.faber.adventofcode.year2022.day12

import xyz.faber.adventofcode.util.*

class Day12 : AdventSolution<Int>() {
    override fun part1(input: List<String>): Int {
        val map = input.map { it.replace("<em>", "").replace("</em>", "") }.toXYMap()
        val start = map.find { it.value == 'S' }!!.pos
        val end = map.find { it.value == 'E' }!!.pos
        map[start] = 'a'
        map[end] = 'z'
        val topology = object : DirectedGraph<Pos> {
            override fun getNeighbours(pos: Pos): Collection<Pos> = pos
                .adjacentNonDiagonal()
                .filter {
                    map[it] <= map[pos] + 1
                }
        }

        return dijkstra(topology.toDirectedWeightedGraph(), start, end)!!.totalCost
    }

    override fun part2(input: List<String>): Int {
        val map = input.map { it.replace("<em>", "").replace("</em>", "") }.toXYMap()
        val start = map.find { it.value == 'S' }!!.pos
        val end = map.find { it.value == 'E' }!!.pos
        map[start] = 'a'
        map[end] = 'z'
        val topology = object : DirectedGraph<Pos> {
            override fun getNeighbours(pos: Pos): Collection<Pos> = pos
                .adjacentNonDiagonal()
                .filter {
                    map[it] >= map[pos] - 1
                }
        }

        val starts = map.filter { it.value == 'a' }.map { it.pos }.toSet()
        return dijkstraPredicate(topology.toDirectedWeightedGraph(), end, { it in starts })!!.totalCost
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 12, Day12()).run()

}
