package xyz.faber.adventofcode.year2022.day17

import xyz.faber.adventofcode.util.*
import kotlin.math.min

class Day17 : AdventSolution<Long>() {
    val dashShape = setOf(Pos(0, 0), Pos(1, 0), Pos(2, 0), Pos(3, 0))
    val crossShape = setOf(Pos(1, 0), Pos(0, -1), Pos(1, -1), Pos(2, -1), Pos(1, -2))
    val lShape = setOf(Pos(0, 0), Pos(1, 0), Pos(2, 0), Pos(2, -1), Pos(2, -2))
    val pipeShape = setOf(Pos(0, 0), Pos(0, -1), Pos(0, -2), Pos(0, -3))
    val cubeShape = setOf(Pos(0, 0), Pos(0, -1), Pos(1, 0), Pos(1, -1))

    val shapes = listOf(dashShape, crossShape, lShape, pipeShape, cubeShape)

    private fun heightOfTower(directions: String, iterations: Int): Long {
        val map = mutableSetOf<Pos>()
        var top = 0
        var shapeIndex = 0
        var j = 0
        for (i in 0 until iterations) {
            val shape = shapes[shapeIndex]
            shapeIndex = (shapeIndex+1)%shapes.size
            var p = Pos(2, top - 4)

            while (true) {
                val c = directions[j]
                j = (j + 1) % directions.length
                val p3 = p + if (c == '<') Pos(-1, 0) else Pos(1, 0)
                if (!contact(shape, p3, map)) {
                    p = p3
                }

                val p2 = p + Pos(0, 1)
                if (contact(shape, p2, map)) {
                    shape.forEach { map += (it + p) }
                    top = min(shape.map { it+p }.minOf { it.y }, top)
                    break
                }
                p = p2
            }

        }

        return -top.toLong()
    }
    override fun part1(input: String): Long {
        val directions = input.trim()
        return heightOfTower(directions, 2022)
    }



    private fun contact(shape: Set<Pos>, p: Pos, map: Set<Pos>) = shape.map { it + p }.any { it.x < 0 || it.x > 6 || it.y>=0 || it in map }


    override fun part2(input: String): Long {
        val directions = input.trim()
        val x = 1730
        val y = 1740
        val a = heightOfTower(directions, x)
        println(a)
        val b = heightOfTower(directions, x + y)
        println(b)
        val c = heightOfTower(directions, x + 2 * y)
        println(c)
        val d = heightOfTower(directions, x + 3 * y)
        println(d)
        val e = heightOfTower(directions, x + 4 * y)
        println(e)
        val f = heightOfTower(directions, x + 5 * y)
        println(f)
        val g = heightOfTower(directions, x + 6 * y)
        println(g)
        println(b-a)
        println(c-b)
        println(d-c)
        println(e-d)
        println(f-e)
        println(g-f)


        val target = 1000000000000L
        val remainder = ((target-x)%y).toInt()
        val b2 = heightOfTower(directions, x + remainder)
        val res = a + (target-x)/y * (b-a) + b2-a

        return res
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 17, Day17()).run()

}
