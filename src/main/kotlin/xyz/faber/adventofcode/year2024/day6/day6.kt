package xyz.faber.adventofcode.year2024.day6

import xyz.faber.adventofcode.util.*

class Day6 : AdventSolution<Int>() {
    override fun part1(input: List<String>): Int {
        val map = input.toXYMap(' ', false)
        val guardStart = map.filter { it.value == '^' }.first().pos
        var currentPos = guardStart
        var currentDir = Direction.N
        val visited = mutableSetOf<Pos>()
        while (map.isInBounds(currentPos)) {
            visited += currentPos
            if (map[currentPos + currentDir] == '#') {
                currentDir = currentDir.turnRight()
            } else {
                currentPos = currentPos + currentDir
            }
        }
        return visited.size
    }

    fun loops(pos: Pos, dir: Direction, map: CharXYMap, extraPosition: Pos): Boolean {
        var currentPos = pos
        var currentDir = dir
        val visited = mutableSetOf<Pair<Pos, Direction>>()
        while (map.isInBounds(currentPos + currentDir)) {
            visited += currentPos to currentDir
            if (map[currentPos + currentDir] == '#' || currentPos + currentDir == extraPosition) {
                currentDir = currentDir.turnRight()
            } else {
                currentPos = currentPos + currentDir
            }
            if ((currentPos to currentDir) in visited) {
                return true
            }
        }
        return false
    }

    override fun part2(input: List<String>): Int {
        val map = input.toXYMap()
        val guardStart = map.filter { it.value == '^' }.first().pos
        var currentPos = guardStart
        var currentDir = Direction.N
        var loopCount = 0
        val visited = mutableSetOf<Pair<Pos, Direction>>()
        val triedBlock = mutableSetOf<Pos>()
        while (map.isInBounds(currentPos + currentDir)) {
            if (map[currentPos + currentDir] == '#') {
                currentDir = currentDir.turnRight()
            } else {
                if (currentPos + currentDir != guardStart && currentPos + currentDir !in triedBlock) {
                    if (loops(currentPos, currentDir, map, currentPos + currentDir)) {
                        loopCount++
                    }
                    triedBlock += currentPos + currentDir
                }
                currentPos = currentPos + currentDir
            }
            visited += currentPos to currentDir
        }
        return loopCount // 2097 too high
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 6, Day6()).run()

}
