package xyz.faber.adventofcode.year2024.day23

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Day23 : AdventSolution<String>() {
    override fun part1(input: List<String>): String {
        val connections = input.map { it.split("-") }.map { it -> it[0] to it[1] }
        val graph = mutableMapOf<String, Set<String>>()
        for (connection in connections) {
            val (a, b) = connection
            graph[a] = (graph[a] ?: emptySet()) + b
            graph[b] = (graph[b] ?: emptySet()) + a
        }
        val targets = graph.keys.filter { it.startsWith("t") }
        val threeSets = targets.flatMap { a ->
            graph[a]!!.flatMap { b ->
                graph[b]!!.filter { c -> a != c }.filter { c -> a in graph[c]!! }.map { c -> setOf(a, b, c) }
            }
        }.toSet()
        return threeSets.size.toString()
    }

    fun maximumClique(
        r: MutableSet<String>,
        p: MutableSet<String>,
        x: MutableSet<String>,
        graph: Map<String, Set<String>>
    ): Set<String> {
        if (p.isEmpty() && x.isEmpty()) {
            return r.toSet()
        }
        val pivot = (p + x).maxByOrNull { graph[it]!!.size }!!
        val pivotNeighbors = graph[pivot] ?: emptySet()
        val toExplore = p.filterNot { it in pivotNeighbors }
        var maximumClique = emptySet<String>()
        for (v in toExplore) {
            val neighbors = graph[v] ?: emptySet()

            // Save the original sets (because we will modify P, X, R in-place).
            val oldR = r.toSet()
            val oldP = p.toSet()
            val oldX = x.toSet()

            // Include v in the current clique
            r.add(v)
            // Restrict P and X to only neighbors of v
            p.retainAll(neighbors)
            x.retainAll(neighbors)

            // Recurse
            val clique = maximumClique(r, p, x, graph)
            if (clique.size > maximumClique.size) {
                maximumClique = clique.toSet()
            }


            // Revert R, P, and X to their original state
            r.clear(); r.addAll(oldR)
            p.clear(); p.addAll(oldP)
            x.clear(); x.addAll(oldX)

            // Move v from P to X (exclude v from future expansions)
            p.remove(v)
            x.add(v)
        }
        return maximumClique
    }

    fun maximumClique(graph: Map<String, Set<String>>): Set<String> {
        // Bron-Kerbosch
        val r = mutableSetOf<String>()
        val p = graph.keys.toMutableSet()
        val x = mutableSetOf<String>()

        return maximumClique(r, p, x, graph)
    }

    override fun part2(input: List<String>): String {
        val connections = input.map { it.split("-") }.map { it -> it[0] to it[1] }
        val graph = mutableMapOf<String, Set<String>>()
        for (connection in connections) {
            val (a, b) = connection
            graph[a] = (graph[a] ?: emptySet()) + b
            graph[b] = (graph[b] ?: emptySet()) + a
        }
        val maximumClique = maximumClique(graph)
        val res = maximumClique.sorted().joinToString(",")
        return res
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 23, Day23()).run()
}