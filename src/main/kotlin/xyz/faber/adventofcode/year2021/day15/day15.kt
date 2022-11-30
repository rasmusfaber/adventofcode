package xyz.faber.adventofcode.year2021.day15

import xyz.faber.adventofcode.util.*

class Day15{
    val input = getInputFromLines(2021, 15)

    private fun solve(map: XYMap<Int>): PathSolution<Pos>? {
        val builder = DirectedWeightedGraphBuilder<Pos>()
        map.forEach { (pos, _) ->
            pos.adjacentNonDiagonal()
                .filter { p2 -> map.isInBounds(p2) }
                .forEach { p2 ->
                    builder.add(pos, p2, map[p2])
                }
        }
        val graph = builder.build()

        val start = Pos(0, 0)
        val end = Pos(map.maxx, map.maxy)
        return dijkstra(graph, start, end)
    }

    fun part1(input: List<String>): Int {
        val map = input.toIntXYMap()
        val path = solve(map)
        return path!!.totalCost
    }


    fun part2(input: List<String>):Int {
        val map = input.toIntXYMap()
        val fullmap = XYMap<Int>(map.dimx*5, map.dimy*5, 0)
        for (x in 0 until fullmap.dimx) {
            for (y in 0 until fullmap.dimy) {
                fullmap[x, y] = ((map[x%(map.dimx), y%(map.dimy)]+(x/map.dimx)+(y/map.dimy))-1)%9+1
            }
        }
        val path = solve(fullmap)
        return path!!.totalCost
    }
}

fun main(args: Array<String>) {
    val d = Day15()

    println(d.part1(d.input))
    println(d.part2(d.input))
}
