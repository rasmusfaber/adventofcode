package xyz.faber.adventofcode.year2022.day8

import xyz.faber.adventofcode.util.*

class Day8 : AdventSolution<Int>() {
    private fun Pos.limitedRay(direction: Direction, map: XYMap<Int>) =
        this.ray(direction).drop(1).takeWhile { it.isInBoundsOf(map) }

    private fun visible(pos: Pos, map: XYMap<Int>, direction: Direction) =
        pos.limitedRay(direction, map)
            .all { map[it] < map[pos] }

    private fun visible(pos: Pos, map: XYMap<Int>) =
        listOf(Direction.N, Direction.E, Direction.S, Direction.W)
            .any { visible(pos, map, it) }

    override fun part1(input: List<String>): Int {
        val map = input.toIntXYMap()
        return map.count { visible(it.pos, map) }
    }

    private fun score(pos: Pos, map: XYMap<Int>, direction: Direction) =
        pos.limitedRay(direction, map)
            .indexOfFirst { it.isOnBorderOf(map) || map[it] >= map[pos] } + 1

    private fun score(pos: Pos, map: XYMap<Int>): Int {
        return listOf(Direction.N, Direction.E, Direction.S, Direction.W)
            .map { score(pos, map, it) }.reduce(Int::times)
    }

    override fun part2(input: List<String>): Int {
        val map = input.toIntXYMap()
        return map.maxOf { score(it.pos, map) }
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 8, Day8()).run()
}
