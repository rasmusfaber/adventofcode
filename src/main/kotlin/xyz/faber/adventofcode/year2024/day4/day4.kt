package xyz.faber.adventofcode.year2024.day4

import xyz.faber.adventofcode.util.*

class Day4 : AdventSolution<Int>() {
    val directions = listOf(
        Pos(0, 1),
        Pos(1, 1),
        Pos(1, 0),
        Pos(1, -1),
        Pos(0, -1),
        Pos(-1, -1),
        Pos(-1, 0),
        Pos(-1, 1)
    )

    fun check(map: CharXYMap, pos: Pos, dir: Pos, word: String): Boolean {
        var current = pos
        for (c in word) {
            if (map[current] != c) {
                return false
            }
            current = current.plus(dir)
        }
        return true
    }

    fun checkXMAS(map: CharXYMap, pos: Pos): Boolean {
        if (map[pos] != 'A') {
            return false
        }
        if (!(
                    ((map[pos + Pos(-1, -1)] == 'M' && map[pos + Pos(1, 1)] == 'S') ||
                            (map[pos + Pos(-1, -1)] == 'S' && map[pos + Pos(1, 1)] == 'M'))
                            &&
                            ((map[pos + Pos(1, -1)] == 'M' && map[pos + Pos(-1, 1)] == 'S') ||
                                    (map[pos + Pos(1, -1)] == 'S' && map[pos + Pos(-1, 1)] == 'M'))
                    )
        ) {
            return false
        }
        return true
    }

    override fun part1(input: List<String>): Int {
        val map = input.toXYMap(' ', false)
        return map.positions().flatMap { pos -> directions.map { pos to it } }
            .count { (pos, dir) -> check(map, pos, dir, "XMAS") }
    }

    override fun part2(input: List<String>): Int {
        val map = input.toXYMap(' ', false)
        return map.positions().count { pos-> checkXMAS(map, pos) }
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 4, Day4()).run()

}
