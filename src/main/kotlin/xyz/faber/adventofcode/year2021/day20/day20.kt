package xyz.faber.adventofcode.year2021.day20

import xyz.faber.adventofcode.util.XYMap
import xyz.faber.adventofcode.util.getInputFromLines
import xyz.faber.adventofcode.util.toXYMap

class Day20 {
    val input = getInputFromLines(2021, 20)

    fun decode(map: XYMap<Char>, x: Int, y: Int): Int {
        var res = 0
        for (j in -1..1) {
            for (i in -1..1) {
                res *= 2
                if (map[x + i, y + j] == '#') {
                    res++
                }
            }
        }
        return res
    }

    fun enhance(map: XYMap<Char>, algo: String, default: Char): XYMap<Char> {
        val res = XYMap(map.dimx + 4, map.dimy + 4, default)
        for (x in 0..res.maxx) {
            for (y in 0..res.maxy) {
                val code = decode(map, x - 2, y - 2)
                res[x, y] = algo[code]
            }
        }
        return res
    }

    fun part1() {
        val algo = input[0]
        val map = input.drop(1).toXYMap()
        val map1 = enhance(map, algo, '#')
        val res = enhance(map1, algo, '.')
        println(res.count { it.value == '#' })
    }


    fun part2() {
        val algo = input[0]
        var map = input.drop(1).toXYMap()
        for(i in 1..50){
            map = enhance(map, algo, if(i%2==0) '.' else '#')
        }
        println(map.count { it.value == '#' })
    }
}

fun main(args: Array<String>) {
    val d = Day20()

    d.part1()
    d.part2()
}
