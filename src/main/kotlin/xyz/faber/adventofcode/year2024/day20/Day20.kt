package xyz.faber.adventofcode.year2024.day20

import xyz.faber.adventofcode.util.*

class Day20 : AdventSolution<Int>() {
    override fun part1(input: List<String>): Int {
        val limit = if(testdata) 1 else 100
        val map = input.toXYMap()
        val start = map.filter { it.value == 'S' }.first().pos
        val end = map.filter { it.value == 'E' }.first().pos
        val noncheatingSolution =
            aStar(map.toDirectedGraph { it != '#' }, { a, b -> manhattanDistance(a, b) }, start, end)
        val noncheatingDistance = noncheatingSolution!!.totalCost
        val positions = map.positions().filter { map[it]!='#' }.toSet()
        val dg = map.toDirectedGraph { it != '#' }
        val distancesFromStart = dijkstraDistances(dg.toDirectedWeightedGraph(), start)
        val distancesToEnd = dijkstraDistances(dg.toDirectedWeightedGraph().reversed(positions), end)

        return positions.sumOf { a ->
            a.atMostManhattanDistanceAway(2).filter{it in positions}.count { b ->
                distancesFromStart[a]!! + distancesToEnd[b]!! + manhattanDistance(
                    a,
                    b
                ) <= noncheatingDistance - limit
            }
        }
    }

    fun part1old(input: List<String>): Int {
        return 0
        val map = input.toXYMap()
        val start = map.filter { it.value == 'S' }.first().pos
        val end = map.filter { it.value == 'E' }.first().pos
        val noncheatingSolution =
            aStar(map.toDirectedGraph { it != '#' }, { a, b -> manhattanDistance(a, b) }, start, end)
        val cheats = mutableSetOf<Pair<Pos, Pos>>()
        while (true) {
            val dg = object : DirectedGraph<Pair<Pos, Boolean>> {
                override fun getNeighbours(pos: Pair<Pos, Boolean>): Collection<Pair<Pos, Boolean>> {
                    val cheated = pos.second
                    if (cheated) {
                        return pos.first.adjacentNonDiagonal().filter { map.isInBounds(it) && map[it] != '#' }
                            .map { it to true }
                    }
                    val noncheating =
                        pos.first.adjacentNonDiagonal().filter { map.isInBounds(it) && map[it] != '#' }
                            .map { it to false }
                    val cheating =
                        pos.first.adjacentNonDiagonal().filter { map.isInBounds(it) && map[it] == '#' }
                            .filter { pos.first to it !in cheats }
                            .map { it to true }
                    return noncheating + cheating
                }
            }
            val solution = aStar(
                dg,
                { a, b -> manhattanDistance(a.first, a.first) },
                start to false,
                end to true
            )
            val saved = noncheatingSolution!!.totalCost - solution!!.totalCost
            if (saved >= 100) {
                val cheat =
                    solution.path.filter { it.from.second != it.to.second }.map { it.from.first to it.to.first }
                        .single()
                cheats += cheat
            } else {
                break
            }
        }
        return cheats.size
    }

    override fun part2(input: List<String>): Int {
        val limit = if(testdata) 1 else 100
        val map = input.toXYMap()
        val start = map.filter { it.value == 'S' }.first().pos
        val end = map.filter { it.value == 'E' }.first().pos
        val noncheatingSolution =
            aStar(map.toDirectedGraph { it != '#' }, { a, b -> manhattanDistance(a, b) }, start, end)
        val noncheatingDistance = noncheatingSolution!!.totalCost
        val positions = map.positions().filter { map[it]!='#' }.toSet()
        val dg = map.toDirectedGraph { it != '#' }
        val distancesFromStart = dijkstraDistances(dg.toDirectedWeightedGraph(), start)
        val distancesToEnd = dijkstraDistances(dg.toDirectedWeightedGraph().reversed(positions), end)

        return positions.sumOf { a ->
            a.atMostManhattanDistanceAway(20).filter{it in positions}.count { b ->
                distancesFromStart[a]!! + distancesToEnd[b]!! + manhattanDistance(
                    a,
                    b
                ) <= noncheatingDistance - limit
            }
        }
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 20, Day20()).run()
}