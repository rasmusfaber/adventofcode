package xyz.faber.adventofcode.year2022.day23

import xyz.faber.adventofcode.util.*

class Day23 : AdventSolution<Int>() {
    override fun part1(input: List<String>): Int {
        val map = input.toXYMap()
        val initialPos = map.filter { it.value == '#' }.map { it.pos }
        var pos = initialPos.toSet()
        for (i in 0..9) {
            pos = step(pos, i)
        }
        val minx = pos.minOf { it.x }
        val maxx = pos.maxOf { it.x }
        val miny = pos.minOf { it.y }
        val maxy = pos.maxOf { it.y }

        return (maxx - minx + 1) * (maxy - miny + 1) - pos.size
    }

    fun step(before: Set<Pos>, i: Int): Set<Pos> {
        val proposed = mutableListOf<Pair<Pos, Pos>>()
        val seen = mutableSetOf<Pos>()
        val duplicated = mutableSetOf<Pos>()
        for (pos in before) {
            val next = getProposed(pos, before, i)
            proposed += pos to next
            if (next in seen) {
                duplicated += next
            } else {
                seen += next
            }
        }
        return proposed.map {
            if (it.second in duplicated) {
                it.first
            } else {
                it.second
            }
        }.toSet()
    }

    private fun getProposed(
        pos: Pos,
        before: Set<Pos>,
        i: Int
    ): Pos {
        if (pos.adjacent().all { !before.contains(it) }) {
            return pos
        } else {
            for (j in 0..3) {
                when ((i + j) % 4) {
                    0 -> {
                        if (!before.contains(pos + Pos(0, -1))
                            && !before.contains(pos + Pos(-1, -1))
                            && !before.contains(pos + Pos(1, -1))
                        ) {
                            return pos + Pos(0, -1)
                        }
                    }

                    1 -> {
                        if (!before.contains(pos + Pos(0, 1))
                            && !before.contains(pos + Pos(-1, 1))
                            && !before.contains(pos + Pos(1, 1))
                        ) {
                            return pos + Pos(0, 1)
                        }
                    }

                    2 -> {
                        if (!before.contains(pos + Pos(-1, 0))
                            && !before.contains(pos + Pos(-1, -1))
                            && !before.contains(pos + Pos(-1, 1))
                        ) {
                            return pos + Pos(-1, 0)
                        }
                    }

                    3 -> {
                        if (!before.contains(pos + Pos(1, 0))
                            && !before.contains(pos + Pos(1, -1))
                            && !before.contains(pos + Pos(1, 1))
                        ) {
                            return pos + Pos(1, 0)
                        }
                    }
                }
            }
        }
        return pos
    }

    override fun part2(input: List<String>): Int {
        val map = input.toXYMap()
        val initialPos = map.filter { it.value == '#' }.map { it.pos }
        var pos = initialPos.toSet()
        var i = 0
        while(true) {
            val next = step(pos, i)
            if(next == pos){
                return i+1
            }
            pos = next
            i++
        }
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 23, Day23()).run()

}
