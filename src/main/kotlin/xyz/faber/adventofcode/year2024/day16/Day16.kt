package xyz.faber.adventofcode.year2024.day16

import xyz.faber.adventofcode.util.*
import java.util.*
import kotlin.math.roundToInt

class Day16 : AdventSolution<Int>() {
    fun toDwg(map: CharXYMap): DirectedWeightedGraph<Pair<Pos, Direction>> {
        return object: DirectedWeightedGraph<Pair<Pos, Direction>>{
            override fun getNeighbours(pos: Pair<Pos, Direction>): Collection<Edge<Pair<Pos, Direction>>> {
                val (p, d) = pos
                return setOf(
                    Edge(pos, p to d.turnLeft(), 1000),
                    Edge(pos, p to d.turnRight(),1000),
                    Edge(pos, p + d to d, 1),
                ).filter { map.isInBounds(it.to.first) && map[it.to.first] != '#' }
            }
        }
    }

    override fun part1(input: List<String>): Int {
        val map = input.toXYMap()
        val start = map.filter { it.value == 'S' }.first().pos
        val end = map.filter { it.value == 'E' }.first().pos
        val dwg = toDwg(map)
        val solution = aStarPredicate(dwg, { manhattanDistance(it.first, end) }, start to Direction.E, { it.first == end })
        return solution!!.totalCost
    }

    private fun <P> dijkstraMulti(
        graph: DirectedWeightedGraph<P>,
        start: P
    ): Map<P, Set<Edge<P>>> {
        val distances = mutableMapOf(start to 0)
        val shortestEdges = mutableMapOf<P, MutableSet<Edge<P>>>()
        val queue = HeapPriorityQueue<P>()
        queue.add(start, 0)
        while (queue.isNotEmpty()) {
            val from = queue.remove()
            for (edge in graph.getNeighbours(from)) {
                if (distances[edge.to] ?: Int.MAX_VALUE > distances[from]!! + edge.cost) {
                    distances[edge.to] = distances[from]!! + edge.cost
                    shortestEdges[edge.to] = mutableSetOf(edge)
                    queue.addOrUpdate(edge.to, distances[from]!! + edge.cost)
                }else if (distances[edge.to] ?: Int.MAX_VALUE == distances[from]!! + edge.cost) {
                    shortestEdges[edge.to]!! += edge
                    queue.addOrUpdate(edge.to, distances[from]!! + edge.cost)
                }
            }
        }
        return shortestEdges
    }

    fun <P> dfsWithPathNoVisit(graph: DirectedGraph<P>, start: P): Sequence<List<P>> = sequence {
        val stack = LinkedList<List<P>>()
        stack.push(listOf(start))
        while (!stack.isEmpty()) {
            val current = stack.pop()
            yield(current)
            val neighbours = graph.getNeighbours(current.last())
            neighbours.map { current.plus(it) }.asReversed().forEach { stack.push(it) }
        }
    }

    override fun part2(input: List<String>): Int {
        val map = input.toXYMap()
        val start = map.filter { it.value == 'S' }.first().pos
        val end = map.filter { it.value == 'E' }.first().pos
        val dwg = toDwg(map)
        val shortest = aStarPredicate(dwg, { manhattanDistance(it.first, end) }, start to Direction.E, { it.first == end })
        val shortestLength = shortest!!.totalCost
        val shortestEdges = dijkstraMulti(dwg, start to Direction.E)
        val shortestEdgesReversed = shortestEdges.flatMap { it.value }.groupBy { it.from }
        val shortestEdgeGraph = object:DirectedGraph<Pair<Pos, Direction>>{
            override fun getNeighbours(pos: Pair<Pos, Direction>): Collection<Pair<Pos, Direction>> {
                return shortestEdgesReversed[pos]?.map { it.to } ?: emptyList()
            }
        }
        val shortestPaths = dfsWithPathNoVisit(shortestEdgeGraph, start to Direction.E)
            .filter { it.last().first == end }
            .filter { it.zipWithNext().sumOf { if(it.first.second!=it.second.second) 1000L else 1L } == shortestLength.toLong()}
            .toList()
        val lookpoints = shortestPaths.flatMap { it.map { it.first } }.toSet() + start
        return lookpoints.size
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 16, Day16()).run()
}