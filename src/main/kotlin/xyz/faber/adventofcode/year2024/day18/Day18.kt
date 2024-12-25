package xyz.faber.adventofcode.year2024.day18

import xyz.faber.adventofcode.util.*

class Day18 : AdventSolution<String>() {
    override fun part1(input: List<String>): String {
        val dim = if (testdata) 7 else 71
        val limit = if(testdata) 12 else 1024
        val map = XYMap(dim, dim, false)
        input.take(limit).map { it.split(",").map { it.toInt() } }
            .map { Pos(it[0], it[1]) }
            .forEach { map[it] = true }
        val dg = map.toDirectedGraph { it == false }
        val start = Pos(0, 0)
        val end = Pos(dim - 1, dim - 1)
        val solution = aStar(dg, { a, b -> manhattanDistance(a, b) }, start, end)
        return solution!!.totalCost.toString()
    }

    fun isExitReachable(bytes: List<Pos>, time: Int): Boolean {
        val dim = if (testdata) 7 else 71
        val map = XYMap(dim, dim, false)
        bytes.take(time).forEach { map[it] = true }
        val dg = map.toDirectedGraph { it == false }
        val start = Pos(0, 0)
        val end = Pos(dim - 1, dim - 1)
        val solution = aStar(dg, { a, b -> manhattanDistance(a, b) }, start, end)
        return solution != null
    }

    override fun part2(input: List<String>): String {
        val bytes = input.map { it.split(",").map { it.toInt() } }
            .map { Pos(it[0], it[1]) }
        // Binary search
        var low = 0
        var high = bytes.size
        while(low < high) {
            val mid = (low + high) / 2
            if (!isExitReachable(bytes, mid)) {
                high = mid
            } else {
                low = mid + 1
            }
        }
        val res = bytes[low-1]
        return "${res.x},${res.y}" // Not 63,10
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 18, Day18()).run()
}