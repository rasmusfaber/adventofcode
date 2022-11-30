package xyz.faber.adventofcode.year2020.day17

import xyz.faber.adventofcode.util.Pos3D
import xyz.faber.adventofcode.util.Pos4D
import xyz.faber.adventofcode.util.getInput
import xyz.faber.adventofcode.util.toXYMap

class Day17 {
    val input = getInput(2020, 17).toXYMap()

    fun part1() {
        val startPos = input.positions().filter { input[it] == '#' }.map { Pos3D(it.x, it.y, 0) }
        var active = startPos.toSet()
        for (i in 1..6) {
            val next = mutableSetOf<Pos3D>()
            val minx = active.minByOrNull { it.x }!!.x
            val miny = active.minByOrNull { it.y }!!.y
            val minz = active.minByOrNull { it.z }!!.z
            val maxx = active.maxByOrNull { it.x }!!.x
            val maxy = active.maxByOrNull { it.y }!!.y
            val maxz = active.maxByOrNull { it.z }!!.z

            for (x in (minx - 1)..(maxx + 1)) {
                for (y in (miny - 1)..(maxy + 1)) {
                    for (z in (minz - 1)..(maxz + 1)) {
                        val pos = Pos3D(x, y, z)
                        var nc = 0
                        for (nx in pos.x - 1..pos.x + 1) {
                            for (ny in pos.y - 1..pos.y + 1) {
                                for (nz in pos.z - 1..pos.z + 1) {
                                    if (nx == x && y == ny && z == nz) {
                                        continue
                                    }
                                    val npos = Pos3D(nx, ny, nz)
                                    if (npos in active) {
                                        nc++
                                    }
                                }
                            }

                        }
                        if (pos in active) {
                            if (nc in 2..3) {
                                next += pos
                            }
                        } else {
                            if (nc == 3) {
                                next += pos
                            }
                        }
                    }
                }
            }
            active = next
        }
        println(active.size)
    }


    fun part2() {
        val startPos = input.positions().filter { input[it] == '#' }.map { Pos4D(it.x, it.y, 0, 0) }
        var active = startPos.toSet()
        for (i in 1..6) {
            val next = mutableSetOf<Pos4D>()
            val minx = active.minByOrNull { it.x }!!.x
            val miny = active.minByOrNull { it.y }!!.y
            val minz = active.minByOrNull { it.z }!!.z
            val minw = active.minByOrNull { it.w }!!.w
            val maxx = active.maxByOrNull { it.x }!!.x
            val maxy = active.maxByOrNull { it.y }!!.y
            val maxz = active.maxByOrNull { it.z }!!.z
            val maxw = active.maxByOrNull { it.w }!!.w


            for (x in (minx - 1)..(maxx + 1)) {
                for (y in (miny - 1)..(maxy + 1)) {
                    for (z in (minz - 1)..(maxz + 1)) {
                        for (w in (minw - 1)..(maxw + 1)) {
                            val pos = Pos4D(x, y, z, w)
                            var nc = 0
                            for (nx in pos.x - 1..pos.x + 1) {
                                for (ny in pos.y - 1..pos.y + 1) {
                                    for (nz in pos.z - 1..pos.z + 1) {
                                        for (nw in pos.w - 1..pos.w + 1) {
                                            if (nx == x && y == ny && z == nz && w == nw) {
                                                continue
                                            }
                                            val npos = Pos4D(nx, ny, nz, nw)
                                            if (npos in active) {
                                                nc++
                                            }
                                        }
                                    }
                                }

                            }
                            if (pos in active) {
                                if (nc in 2..3) {
                                    next += pos
                                }
                            } else {
                                if (nc == 3) {
                                    next += pos
                                }
                            }
                        }
                    }
                }
            }
            active = next
        }
        println(active.size)
    }

}

fun main(args: Array<String>) {
    val d = Day17()
    d.part1()
    d.part2()
}
