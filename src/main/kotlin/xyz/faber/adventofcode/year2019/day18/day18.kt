package xyz.faber.adventofcode.year2019.day18

import xyz.faber.adventofcode.util.*

class Day18 {
    val input = getInput(2019, 18)
    val map = input.toXYMap()

    fun part1() {
        val start = map.positions().filter { map[it] == '@' }.single()
        val keys = map.positions().filter { map[it] in 'a'..'z' }
        val doors = map.positions().filter { map[it] in 'A'..'Z' }
        val innerdwg = map.toDirectedWeightedGraphByContent(keys + doors + start) { it != '#' }
        val startState = State(listOf('@'), charSetOf())
        val dwg = ExtendedDWG(innerdwg)
        val solution = aStarPredicate(dwg, {keys.size - it.keys.size}, startState, { it.keys.size == keys.size })!!
        solution.print()
        println(solution.totalCost)
    }

    fun part2(){
        val oldStart = map.positions().filter { map[it] == '@' }.single()
        val startPositions = oldStart.adjacentDiagonal()
        startPositions.withIndex().forEach { (i, pos) -> map[pos] = ('0' + i) }
        map[oldStart] = '#'
        map[oldStart + Pos(0, -1)] = '#'
        map[oldStart + Pos(0, 1)] = '#'
        map[oldStart + Pos(1, 0)] = '#'
        map[oldStart + Pos(-1, 0)] = '#'
        //map.print()

        val keys = map.positions().filter { map[it] in 'a'..'z' }
        val doors = map.positions().filter { map[it] in 'A'..'Z' }
        val innerdwg = map.toDirectedWeightedGraphByContent(keys + doors + startPositions) { it != '#' }
        val startState = State(listOf('0','1','2','3'), charSetOf())
        val dwg = ExtendedDWG(innerdwg)
        //val solution = dijkstraPredicate(dwg, startState, { it.keys.size == 26 })
        val solution = aStarPredicate(dwg, {(keys.size - it.keys.size)*3}, startState, {it.keys.size==keys.size})!!
        solution.print()
        println(solution.totalCost)

    }

    data class State(val pos: List<Char>, val keys: CharSet) {
        override fun toString(): String = "$pos ${keys.sorted()}"
    }

    class ExtendedDWG(var inner: DirectedWeightedGraph<Char>) : DirectedWeightedGraph<State> {
        override fun getNeighbours(state: State): Collection<Edge<State>> {
            val res = mutableListOf<Edge<State>>()
            for (i in 0 until state.pos.size) {
                for (e in inner.getNeighbours(state.pos[i])) {
                    when (e.to) {
                        '@', in '0'..'3' -> res.add(Edge(state, State(state.pos.setAt(i, e.to), state.keys), e.cost))
                        in 'a'..'z' -> res.add(Edge(state, State(state.pos.setAt(i, e.to), state.keys + e.to), e.cost))
                        in 'A'..'Z' -> if (e.to.lowercaseChar() in state.keys) {
                            res.add(Edge(state, State(state.pos.setAt(i, e.to), state.keys), e.cost))
                        }
                    }
                }
            }
            return res
        }
    }
}

fun main(args: Array<String>) {
    val d = Day18()
    d.part1()
    d.part2()
}

