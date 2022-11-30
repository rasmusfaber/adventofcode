package xyz.faber.adventofcode.year2019.day24

import xyz.faber.adventofcode.util.Pos
import xyz.faber.adventofcode.util.XYMap
import xyz.faber.adventofcode.util.getInput
import xyz.faber.adventofcode.util.toXYMap

class Day24 {
    val input = getInput(2019, 24)
    val input2 = "....#\n" +
            "#..#.\n" +
            "#.?##\n" +
            "..#..\n" +
            "#...."

    fun part1() {
        var map = input.toXYMap()
        map.default = '.'
        val seen = mutableSetOf(map.map)
        while (true) {
            val next = XYMap(map.dimx, map.dimy) { x, y ->
                val p = Pos(x, y)
                val ac = p.adjacentNonDiagonal().count { map[it] == '#' }
                when (map[p]) {
                    '.' -> if (ac == 1 || ac == 2) '#' else '.'
                    '#' -> if (ac == 1) '#' else '.'
                    else -> throw RuntimeException("${map[p]}")
                }
            }
            next.default = '.'
            map = next
            //map.print()
            if (!seen.add(map.map)) {
                var v = 1
                var res = 0
                for (p in map.positions()) {
                    if (map[p] == '#') {
                        res += v
                    }
                    v *= 2
                }
                // 33542143
                println(res)
                return
            }
        }
    }


    fun part2() {
        var map = input.toXYMap()
        map.default = '.'
        var maps = mapOf(0 to map)
        for (i in 1..200) {
            maps = next(maps)
            //print(maps)
            //println("---")
        }

        val res = maps.values.sumBy { it.count { (_, c) -> c == '#' } }
        // not 2016
        println(res)
    }

    private fun print(maps: Map<Int, XYMap<Char>>) {
        for (i in maps.keys.minOrNull()!!..maps.keys.maxOrNull()!!) {
            if (maps[i]!!.map.all { it == '.' }) {
                continue
            }
            println(i)
            maps[i]!!.print()
        }
    }

    private fun next(maps: Map<Int, XYMap<Char>>): Map<Int, XYMap<Char>> {
        val max = maps.keys.maxOrNull()!!
        val dim = maps[0]!!.dimx
        var next = mutableMapOf<Int, XYMap<Char>>()
        for (i in (-(max + 1)..(max + 1))) {
            val same = maps[i] ?: XYMap(dim, dim, '.')
            val below = maps[i + 1] ?: XYMap(dim, dim, '.')
            val above = maps[i - 1] ?: XYMap(dim, dim, '.')
            val level = XYMap(dim, dim) { x, y ->
                if (x == 2 && y == 2) '.'
                else {
                    val p = Pos(x, y)
                    var ac = p.adjacentNonDiagonal().count { same[it] == '#' }
                    if (x == 0) {
                        if (above[1, 2] == '#') {
                            ac++
                        }
                    }
                    if (x == 4) {
                        if (above[3, 2] == '#') {
                            ac++
                        }
                    }
                    if (y == 0) {
                        if (above[2, 1] == '#') {
                            ac++
                        }
                    }
                    if (y == 4) {
                        if (above[2, 3] == '#') {
                            ac++
                        }
                    }
                    if (x == 1 && y == 2) {
                        for (y2 in 0..4) {
                            if (below[0, y2] == '#') {
                                ac++
                            }
                        }
                    }
                    if (x == 3 && y == 2) {
                        for (y2 in 0..4) {
                            if (below[4, y2] == '#') {
                                ac++
                            }
                        }
                    }
                    if (x == 2 && y == 1) {
                        for (x2 in 0..4) {
                            if (below[x2, 0] == '#') {
                                ac++
                            }
                        }
                    }
                    if (x == 2 && y == 3) {
                        for (x2 in 0..4) {
                            if (below[x2, 4] == '#') {
                                ac++
                            }
                        }
                    }
                    when (same[p]) {
                        '.' -> if (ac == 1 || ac == 2) '#' else '.'
                        '#' -> if (ac == 1) '#' else '.'
                        else -> throw RuntimeException("${same[p]}")
                    }
                }
            }
            level.default = '.'
            next[i] = level
        }

        return next
    }

}

fun main(args: Array<String>) {
    val d = Day24()
    d.part1()
    d.part2()
}
