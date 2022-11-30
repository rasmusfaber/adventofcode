package xyz.faber.adventofcode.year2021.day23

import xyz.faber.adventofcode.util.*

data class State(val map: CharArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        other as State

        return map.contentEquals(other.map)
    }

    override fun hashCode(): Int {
        return map.contentHashCode()
    }
}

class Day23 {
    val input = getInputFromLines(2021, 23)
        .map { it.padEnd(13, ' ') }

    private class SearchStrategy(val initialMap: XYMap<Char>, val depth: Int) {
        private val positions = (1..4).flatMap{ column-> (depth+1 downTo 2).map{y-> Pos(column*2+1,y)}}+(1..11).filter{it !in setOf(3,5,7,9)}.map{Pos(it,1)}
        private val reverseTranslate = positions.mapIndexed { i, pos -> pos to i }.toMap()
        private val topology = initialMap.toGraph { it != '#' && it != ' ' }
        private val distances = positions.flatMap { p1 -> topology.getDistances(p1, positions, true).map { (reverseTranslate[p1]!! to reverseTranslate[it.key]!!) to it.value } }.toMap()
        private val routes = positions.flatMap { p1 ->
            topology.getRoutes(p1, positions, true).map { (reverseTranslate[p1]!! to reverseTranslate[it.key]!!) to it.value.map { it2 -> reverseTranslate[it2] }.filterNotNull() }
        }.toMap()

        private fun costMult(c: Char): Int {
            return when (c) {
                'A' -> 1
                'B' -> 10
                'C' -> 100
                'D' -> 1000
                else -> 0
            }
        }

        private fun getDests(c: Char): IntRange {
            return when (c) {
                'A' -> 0..depth - 1
                'B' -> depth..2 * depth - 1
                'C' -> 2 * depth..3 * depth - 1
                'D' -> 3 * depth..4 * depth - 1
                else -> -1..-1
            }
        }

        private fun heuristic(state: State): Int {
            var res = 0
            val filled = IntArray(4)
            for (i in positions.indices) {
                val c = state.map[i]
                if (c == '.') continue
                val dests = getDests(c)
                val dest = dests.first + filled[c - 'A']
                if (i != dest) {
                    res += distances[i to dest]!! * costMult(c)
                }
                filled[c - 'A']++
            }
            return res
        }

        private fun getNeighbours(state: State): Collection<Edge<State>> {
            val res = mutableListOf<Edge<State>>()
            val m = state.map
            for (i in positions.indices) {
                val c = state.map[i]
                if (c == '.') continue
                val l = mutableListOf<Int>()
                if (i <= 4*depth-1) {
                    for (j in 4*depth..4*depth+6) {
                        val r = routes[i to j]!!
                        if (r.all { m[it] == '.' }) {
                            l.add(j)
                        }
                    }
                } else {
                    val dests = getDests(c)
                    if (dests.all { m[it] == '.' || m[it] == c }) {
                        for (dest in dests) {
                            val r1 = routes[i to dest]!!
                            if (r1.all { m[it] == '.' }) {
                                l.add(dest)
                            }
                        }
                    }
                }

                l.forEach { j ->
                    res.add(Edge(state, newState(state, i, j), distances[i to j]!! * costMult(c)))
                }
            }
            return res
        }

        private fun newState(cur: State, i: Int, j: Int): State {
            val newmap = CharArray(cur.map.size)
            for (p in cur.map.indices) {
                newmap[p] = when (p) {
                    i -> '.'
                    j -> cur.map[i]
                    else -> cur.map[p]
                }
            }
            return State(newmap)
        }

        private fun isGoal(state: State): Boolean {
            return ('A'..'D').all { c -> getDests(c).all { state.map[it] == c } }
        }

        fun search(): PathSolution<State>? {
            val initialstatemap = positions.map { initialMap[it] }.toList().toCharArray()
            val initialstate = State(initialstatemap)

            return aStarPredicate(::getNeighbours, ::heuristic, initialstate, ::isGoal)
        }

        fun print(path: List<Edge<State>>) {
            path.forEach {
                print(initialMap, it.to, reverseTranslate)
                println(it.cost)
            }
        }

        private fun print(initialMap: XYMap<Char>, state: State, translate: Map<Pos, Int>) {
            initialMap.print { p ->
                val rp = translate[p]
                if (rp == null) {
                    initialMap[p].toString()
                } else {
                    state.map[rp].toString()
                }
            }
        }
    }


    fun part1() {
        val initialmap = input.toXYMap()
        val strategy = SearchStrategy(initialmap, 2)

        val res = strategy.search()
        strategy.print(res!!.path)
        println(res!!.totalCost)
    }

    fun part2() {
        val initialmap = input.subList(0, 3)
            .plus("  #D#C#B#A#  ")
            .plus("  #D#B#A#C#  ")
            .plus(input.subList(3, 5)).toXYMap()
        val strategy = SearchStrategy(initialmap, 4)
        val res = strategy.search()
        strategy.print(res!!.path)
        println(res!!.totalCost)
    }
}

fun main(args: Array<String>) {
    val d = Day23()

    d.part1()
    d.part2()
}
