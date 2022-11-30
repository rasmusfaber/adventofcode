package xyz.faber.adventofcode.year2021.day22

import xyz.faber.adventofcode.util.IntCounter
import xyz.faber.adventofcode.util.getInputFromLines
import xyz.faber.adventofcode.util.parse
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day22 {
    val input = getInputFromLines(2021, 22)
        .parse("(.*) x=(.*)\\.\\.(.*),y=(.*)\\.\\.(.*),z=(.*)\\.\\.(.*)")
        .map { (on, x1, x2, y1, y2, z1, z2) -> Cube(x1.toInt(), x2.toInt(), y1.toInt(), y2.toInt(), z1.toInt(), z2.toInt()) to (on == "on") }

    data class Cube(val x1: Int, val x2: Int, val y1: Int, val y2: Int, val z1: Int, val z2: Int) {
        fun intersect(other: Cube): Cube? {
            val x1 = max(this.x1, other.x1)
            val x2 = min(this.x2, other.x2)
            val y1 = max(this.y1, other.y1)
            val y2 = min(this.y2, other.y2)
            val z1 = max(this.z1, other.z1)
            val z2 = min(this.z2, other.z2)
            if (x1 > x2 || y1 > y2 || z1 > z2) {
                return null
            }
            return Cube(x1, x2, y1, y2, z1, z2)
        }

        fun volume(): Long {
            return 1L * (x2 - x1 + 1) * (y2 - y1 + 1) * (z2 - z1 + 1)
        }
    }

    fun part1(input: List<Pair<Cube, Boolean>>): Int {
        val grid = mutableSetOf<Triple<Int, Int, Int>>()
        input
            .filter { (c, _) -> abs(c.x1) <= 50 && abs(c.x2) <= 50 && abs(c.y1) <= 50 && abs(c.y2) <= 50 && abs(c.z1) <= 50 && abs(c.z2) <= 50 }
            .forEach { (c, on) ->
                for (x in c.x1..c.x2) {
                    for (y in c.y1..c.y2) {
                        for (z in c.z1..c.z2) {
                            if (on) {
                                grid += Triple(x, y, z)
                            } else {
                                grid -= Triple(x, y, z)
                            }
                        }
                    }
                }
                //println(grid.size)
            }
        return grid.size
    }

    fun part2(input: List<Pair<Cube, Boolean>>): Long {
        val xs = input.flatMap { (c, _) -> listOf(c.x1, c.x2 + 1) }.toSortedSet().toList()
        val xindex = xs.withIndex().associate { it.value to it.index }
        val ys = input.flatMap { (c, _) -> listOf(c.y1, c.y2 + 1) }.toSortedSet().toList()
        val yindex = ys.withIndex().associate { it.value to it.index }
        val zs = input.flatMap { (c, _) -> listOf(c.z1, c.z2 + 1) }.toSortedSet().toList()
        val zindex = zs.withIndex().associate { it.value to it.index }

        println(xs.size * ys.size * zs.size)
        println(input.size)
        val grid = UByteArray(xs.size * ys.size * zs.size)
        var i = 0
        input
            .forEach { (c, on) ->
                val x1i = xindex[c.x1]!!
                val x2i = xindex[c.x2 + 1]!!
                val y1i = yindex[c.y1]!!
                val y2i = yindex[c.y2 + 1]!!
                val z1i = zindex[c.z1]!!
                val z2i = zindex[c.z2 + 1]!!

                for (x in x1i until x2i) {
                    for (y in y1i until y2i) {
                        for (z in z1i until z2i) {
                            if (on) {
                                grid[x * ys.size * zs.size + y * zs.size + z] = 1u
                            } else {
                                grid[x * ys.size * zs.size + y * zs.size + z] = 0u
                            }
                        }
                    }
                }
                i++
            }
        var res = 0L
        for (xi in xs.indices) {
            for (yi in ys.indices) {
                for (zi in zs.indices) {
                    if (grid[xi * ys.size * zs.size + yi * zs.size + zi] == 1u.toUByte()) {
                        res += 1L * (xs[xi + 1] - xs[xi]) * (ys[yi + 1] - ys[yi]) * (zs[zi + 1] - zs[zi])
                    }
                }
            }
        }
        return res
    }

    fun part2b(input: List<Pair<Cube, Boolean>>): Long {
        val cubes = IntCounter<Cube>()
        input
            .forEach { (c, on) ->
                val update = IntCounter<Cube>()
                cubes.forEach { (c2, i) ->
                    val intersection = c.intersect(c2)
                    if (intersection != null) {
                        update[intersection] -= i
                    }
                }
                if (on) {
                    update[c] += 1
                }
                cubes.update(update)
            }

        return cubes.entries.sumOf { (c, i) -> c.volume() * i }
    }
}

fun main(args: Array<String>) {
    val d = Day22()

    println(d.part1(d.input))
    println(d.part2b(d.input))
}
