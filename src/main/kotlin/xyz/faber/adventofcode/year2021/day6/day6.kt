package xyz.faber.adventofcode.year2021.day6

import xyz.faber.adventofcode.util.getInputIntsFromCsv

class Day6 {
    val input = getInputIntsFromCsv(2021, 6)

    fun part1() {
        var cur = input.groupBy { it }.map { it.key to it.value.size }.toMap()
        for (i in 1..80) {
            val next = mutableMapOf<Int, Int>()
            for (i in 0..7) {
                next[i] = cur[i + 1] ?: 0
            }
            next[6] = (next[6] ?: 0) + (cur[0] ?: 0)
            next[8] = cur[0] ?: 0
            cur = next
        }
        println(cur.values.sum())
    }

    fun part2() {
        var cur = input.groupBy { it }.map { it.key to it.value.size.toLong() }.toMap()
        for (i in 1..256) {
            val next = mutableMapOf<Int, Long>()
            for (i in 0..7) {
                next[i] = cur[i + 1] ?: 0
            }
            next[6] = (next[6] ?: 0) + (cur[0] ?: 0)
            next[8] = cur[0] ?: 0
            cur = next
        }
        println(cur.values.sum())
    }

}

fun main(args: Array<String>) {
    val d = Day6()
    d.part1()
    d.part2()
}
