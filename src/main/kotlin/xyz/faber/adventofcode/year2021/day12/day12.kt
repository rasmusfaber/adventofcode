package xyz.faber.adventofcode.year2021.day12

import xyz.faber.adventofcode.util.DirectedGraph
import xyz.faber.adventofcode.util.getInputFromLines
import xyz.faber.adventofcode.util.toDirectedGraph
import java.util.*

class Day12 {
    val input = getInputFromLines(2021, 12);

    fun parse(input: List<String>): DirectedGraph<String> = input
        .map { it.split("-") }
        .flatMap {
            if (it[0] == "start" || it[1] == "end")
                listOf(it[0] to it[1])
            else if (it[1] == "start" || it[0] == "end")
                listOf(it[1] to it[0])
            else listOf(it[0] to it[1], it[1] to it[0])
        }
        .groupBy({ it.first }, { it.second })
        .toDirectedGraph()

    fun bfsWithPath(graph: DirectedGraph<String>, start: String): Sequence<List<String>> = sequence {
        val queue = LinkedList<List<String>>()
        queue.addLast(listOf(start))
        while (!queue.isEmpty()) {
            val current = queue.pop()
            if (current.last() == "end") {
                yield(current)
            }
            val neighbours = graph.getNeighbours(current.last())
            val validNeighbours = neighbours.filter { it[0].isUpperCase() || !current.contains(it) }
            queue.addAll(validNeighbours.map { current.plus(it) })
        }
    }

    fun part1(input: List<String>): Int {
        val graph = parse(input)
        return bfsWithPath(graph, "start").count()
    }

    fun bfsWithPath2(graph: DirectedGraph<String>, start: String): Sequence<List<String>> = sequence {
        val queue = LinkedList<Pair<List<String>, Boolean>>()
        queue.addLast(listOf(start) to false)
        while (!queue.isEmpty()) {
            val current = queue.pop()
            if (current.first.last() == "end") {
                yield(current.first)
            }
            val neighbours = graph.getNeighbours(current.first.last())
            if (!current.second) {
                queue.addAll(neighbours.map { current.first.plus(it) to (it[0].isLowerCase() && current.first.contains(it)) })
            } else {
                val validNeighbours = neighbours.filter { it[0].isUpperCase() || !current.first.contains(it) }
                queue.addAll(validNeighbours.map { current.first.plus(it) to true })
            }
        }
    }

    fun part2(input: List<String>): Int {
        val graph = parse(input)
        return bfsWithPath2(graph, "start").count()
    }
}

fun main(args: Array<String>) {
    val d = Day12()

    println(d.part1(d.input))
    println(d.part2(d.input))
}
