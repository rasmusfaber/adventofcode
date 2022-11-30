package xyz.faber.adventofcode.year2019.day20

import xyz.faber.adventofcode.util.*

class Day20 {
    val input = getInput(2019, 20)

    fun part1() {
        val map = input.toXYMap()
        val portals = map.getTaggedPositions('.')
        val portalsReversed = portals.reversed()
        val builder = DirectedWeightedGraphBuilder<Pos>()
        map.addToBuilder(builder, portals.keys) { it == '.' }
        portalsReversed.values.filter { it.size == 2 }.forEach {
            builder.add(it[0], it[1], 1)
            builder.add(it[1], it[0], 1)
        }
        val dwg = builder.build()
        val start = portalsReversed["AA"]!![0]
        val end = portalsReversed["ZZ"]!![0]
        val solution = dijkstra(dwg, start, end)!!

        //print(solution)
        println(solution.totalCost)

        //map.printPath(solution, "*")
    }

    fun part2() {
        val map = input.toXYMap()
        val portals = map.getTaggedPositions('.')
        val outerportals = portals.filter { (_, tag) -> tag != "AA" && tag != "ZZ" }.filter { (p, _) -> p.x == 2 || p.x == map.dimx - 3 || p.y == 2 || p.y == map.dimx - 3 }
        val innerportals = portals.filter { (_, tag) -> tag != "AA" && tag != "ZZ" } - outerportals.keys
        val portalsReversed = portals.reversed()

        val normalDwg = map.toDirectedWeightedGraph(portals.keys) { it == '.' }
        val extendedDWG = ExtendedDWG(normalDwg, outerportals, innerportals)

        val start = State(portalsReversed["AA"]!![0], 0)
        val end = State(portalsReversed["ZZ"]!![0], 0)

        val solution = dijkstra(extendedDWG, start, end)!!
        //print(solution, portals)
        println(solution.totalCost)
    }

    fun XYMap<Char>.getTaggedPositions(contents: Char): Map<Pos, String> {
        val res = mutableMapOf<Pos, String>()
        for (p in this.positions().filter { this[it] == contents }) {
            for (dir in Direction.values()) {
                if (this[p + dir] in ('A'..'Z')) {
                    val c1 = this[p + dir]
                    val c2 = this[p + dir + dir]
                    val tag =
                            when (dir) {
                                Direction.N -> "" + c2 + c1
                                Direction.E -> "" + c1 + c2
                                Direction.S -> "" + c1 + c2
                                Direction.W -> "" + c2 + c1
                            }
                    res[p] = tag
                }
            }
        }
        return res
    }

    data class State(val pos: Pos, val level: Int)

    class ExtendedDWG(var normal: DirectedWeightedGraph<Pos>,
                      var outerportals: Map<Pos, String>,
                      var innerportals: Map<Pos, String>) : DirectedWeightedGraph<State> {
        var outerportalsReversed = outerportals.reversedUnique()
        var innerportalsReversed = innerportals.reversedUnique()
        override fun getNeighbours(s: State): Collection<Edge<State>> {
            val samelevel = normal.getNeighbours(s.pos).map { (from, to, cost) -> Edge(s, State(to, s.level), cost) }
            if (outerportals.containsKey(s.pos) && s.level > 0) {
                val portalexit = innerportalsReversed[outerportals[s.pos]!!]!!
                return samelevel + Edge(s, State(portalexit, s.level - 1), 1)
            }
            if (innerportals.containsKey(s.pos)) {
                val portalexit = outerportalsReversed[innerportals[s.pos]!!]!!
                return samelevel + Edge(s, State(portalexit, s.level + 1), 1)
            }
            return samelevel
        }
    }

    private fun print(solution: PathSolution<State>, portals: Map<Pos, String>) {
        solution.path.forEach {
            val (from, to, cost) = it
            if (from.level == to.level) {
                println("Walk from ${portals[from.pos]} to ${portals[to.pos]} ($cost steps)")
            } else if (from.level < to.level) {
                println("Recurse into level ${to.level} through ${portals[from.pos]} (1 step)")
            } else {
                println("Return to level ${to.level} through ${portals[from.pos]} (1 step)")
            }
        }
    }
}

fun main(args: Array<String>) {
    val d = Day20()
    d.part1()
    println("---")
    //d.part2()
}
