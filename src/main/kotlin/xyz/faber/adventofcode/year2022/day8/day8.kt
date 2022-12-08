package xyz.faber.adventofcode.year2022.day8

import xyz.faber.adventofcode.util.*

class Day8 : AdventSolution<Int>() {
    private fun visible(pos: Pos, map: XYMap<Int>): Boolean {
        val height = map[pos]
        return (0..(pos.x - 1)).all { map[Pos(it, pos.y)] < height }
                || ((pos.x + 1)..(map.maxx)).all { map[Pos(it, pos.y)] < height }
                || (0..(pos.y - 1)).all { map[Pos(pos.x, it)] < height }
                || ((pos.y + 1)..(map.maxy)).all { map[Pos(pos.x, it)] < height }
    }

    private fun score(pos: Pos, map: XYMap<Int>, dir: Direction): Int {
        val height = map[pos]
        var res = 0
        var p = pos + dir
        while (map.isInBounds(p)) {
            res++
            if (map[p] >= height) return res
            p = p.move(dir)
        }
        return res
    }

    private fun score(pos: Pos, map: XYMap<Int>) =
        score(pos, map, Direction.N) *
                score(pos, map, Direction.W) *
                score(pos, map, Direction.S) *
                score(pos, map, Direction.E)

    override fun part1(input: List<String>): Int {
        val map = input.toIntXYMap()
        return map.count { visible(it.pos, map) }
    }

    override fun part2(input: List<String>): Int {
        val map = input.toIntXYMap()
        return map.maxOf { score(it.pos, map) }
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 8, Day8()).run()
}
