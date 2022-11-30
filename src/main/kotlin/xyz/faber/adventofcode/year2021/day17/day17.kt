package xyz.faber.adventofcode.year2021.day17

import xyz.faber.adventofcode.util.getInput
import xyz.faber.adventofcode.util.parse
import kotlin.math.abs
import kotlin.math.sign

class Day17 {
    val input = getInput(2021, 17)

    fun testdx(x1: Int, x2: Int, startdx: Int): Boolean {
        var dx = startdx
        var x = 0
        var i = 0
        while (dx > 0) {
            if (x in x1..x2) {
                return true
            }
            x += dx
            dx -= dx.sign
        }
        return false
    }

    fun testdxdy(x1: Int, x2: Int, y1: Int, y2: Int, startdx: Int, startdy: Int): Boolean {
        var dx = startdx
        var dy = startdy
        var x = 0
        var y = 0
        while (dy>0 || y >= y1) {
            if (x in x1..x2 && y in y1..y2) {
                return true
            }
            x += dx
            y += dy
            dx -= dx.sign
            dy--
        }
        return false
    }

    fun count(x1: Int, x2: Int, y1: Int, y2: Int, possibledx: Collection<Int>, dyrange: IntRange): Int {
        return dyrange.flatMap { dy -> possibledx.filter { dx -> testdxdy(x1, x2, y1, y2, dx, dy) } }.count()
    }

    fun part1(x1: Int, x2: Int, y1: Int, y2: Int): Int = y1 * (y1 + 1) / 2

    fun part2(x1: Int, x2: Int, y1: Int, y2: Int): Int {
        val possibledx = (0..x2).filter { testdx(x1, x2, it) != null }

        return count(x1, x2, y1, y2, possibledx, -abs(y1)..abs(y1))!!
    }
}

fun main(args: Array<String>) {
    val d = Day17()
    val (x1, x2, y1, y2) = d.input.parse("target area: x=(-?\\d*)..(-?\\d*), y=(-?\\d*)..(-?\\d*)")
        .toList().map { it.toInt() }

    println(d.part1(x1, x2, y1, y2))
    println(d.part2(x1, x2, y1, y2))
}
