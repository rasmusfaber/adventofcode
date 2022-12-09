package xyz.faber.adventofcode.year2022.day9

import xyz.faber.adventofcode.util.*
import kotlin.math.absoluteValue
import kotlin.math.sign

class Day9 : AdventSolution<Int>() {
    override fun part1(input: List<String>): Int {
        var head = Pos(0, 0)
        var tail = head
        val visited = mutableSetOf<Pos>()
        visited += tail
        for (line in input) {
            val (dir, moves) = line.split(" ")
            for (i in 1..moves.toInt()) {
                head = head.move(dir.toDirection())

                if ((head.x - tail.x).absoluteValue > 1 || (head.y - tail.y).absoluteValue > 1) {
                    tail = Pos(tail.x + (head.x - tail.x).sign, tail.y + (head.y - tail.y).sign)
                    visited += tail
                }
            }
        }
        return visited.size
    }

    override fun part2(input: List<String>): Int {
        var rope = (1..10).map { Pos(0, 0) }.toMutableList()
        val visited = mutableSetOf<Pos>()
        visited += rope.last()
        for (line in input) {
            val (dir, moves) = line.split(" ")
            for (i in 1..moves.toInt()) {
                rope[0] = rope[0].move(dir.toDirection())

                for (i in 1 until 10) {
                    if ((rope[i - 1].x - rope[i].x).absoluteValue > 1 || (rope[i - 1].y - rope[i].y).absoluteValue > 1) {
                        rope[i] = Pos(rope[i].x + (rope[i - 1].x - rope[i].x).sign, rope[i].y + (rope[i - 1].y - rope[i].y).sign)
                    }
                }
                visited += rope.last()
            }
        }
        return visited.size
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 9, Day9()).run()

}
