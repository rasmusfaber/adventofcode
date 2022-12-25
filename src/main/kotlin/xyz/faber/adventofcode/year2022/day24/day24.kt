package xyz.faber.adventofcode.year2022.day24

import xyz.faber.adventofcode.util.*

class Day24 : AdventSolution<Int>() {
    override fun part1(input: List<String>): Int {
        val map = input.subList(1, input.size - 1).map { it.substring(1, it.length - 1) }.toXYMap()
        val validpositions = map.positions() + Pos(0, -1) + Pos(map.maxx, map.maxy + 1)
        val blizzards = map.filter { it.value in setOf('<', '>', '^', 'v') }.groupBy({ it.value }, { it.pos }).mapValues { it.value.toSet() }

        val start = Pos(0, -1)
        val end = Pos(map.maxx, map.maxy + 1)
        val graph = { (pos, i): Pair<Pos, Int> -> (pos.adjacentNonDiagonal() + pos).filter { it in validpositions && !blocked(it, i + 1, blizzards, map) }.map { it to i + 1 } }.toDirectedGraph()
        val solution = aStarPredicate(graph.toDirectedWeightedGraph(), { manhattanDistance(it.first, end) }, start to 0, { it.first == end })
        return solution!!.totalCost
    }

    fun blocked(pos: Pos, time: Int, blizzards: Map<Char, Set<Pos>>, map: XYMap<Char>): Boolean {
        return pos.isInBoundsOf(map) && (
                (pos - Pos(-time, 0)).wrapAround(map) in blizzards['<'] ?: emptySet()
                        || (pos - Pos(time, 0)).wrapAround(map) in blizzards['>'] ?: emptySet()
                        || (pos - Pos(0, -time)).wrapAround(map) in blizzards['^'] ?: emptySet()
                        || (pos - Pos(0, time)).wrapAround(map) in blizzards['v'] ?: emptySet()
                )
    }

    override fun part2(input: List<String>): Int {
        val map = input.subList(1, input.size - 1).map { it.substring(1, it.length - 1) }.toXYMap()
        val validpositions = map.positions() + Pos(0, -1) + Pos(map.maxx, map.maxy + 1)
        val blizzards = map.filter { it.value in setOf('<', '>', '^', 'v') }.groupBy({ it.value }, { it.pos }).mapValues { it.value.toSet() }

        val start = Pos(0, -1)
        val end = Pos(map.maxx, map.maxy + 1)
        val graph = { (pos, i): Pair<Pos, Int> -> (pos.adjacentNonDiagonal() + pos).filter { it in validpositions && !blocked(it, i + 1, blizzards, map) }.map { it to i + 1 } }.toDirectedGraph()
        val solution1 = aStarPredicate(graph.toDirectedWeightedGraph(), { manhattanDistance(it.first, end) }, start to 0, { it.first == end })
        val time1 = solution1!!.totalCost
        val solution2 = aStarPredicate(graph.toDirectedWeightedGraph(), { manhattanDistance(it.first, start) }, end to time1, { it.first == start })
        val time2 = solution2!!.totalCost
        val solution3 = aStarPredicate(graph.toDirectedWeightedGraph(), { manhattanDistance(it.first, end) }, start to time1+time2, { it.first == end })
        val time3 = solution3!!.totalCost
        return time1+time2+time3

    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 24, Day24()).run()

}
