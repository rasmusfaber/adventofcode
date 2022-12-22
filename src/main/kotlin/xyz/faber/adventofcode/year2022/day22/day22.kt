package xyz.faber.adventofcode.year2022.day22

import xyz.faber.adventofcode.util.*
import kotlin.random.Random

class Day22 : AdventSolution<Int>() {
    override fun part1(input: List<String>): Int {
        val (mapLines, pathLines) = input.split(listOf(""))
        val dimx = mapLines.maxOf { it.length }
        val map = mapLines.map { it.padEnd(dimx) }.toXYMap()
        val splitRegex = "(?<=[LR])|(?=[LR])".toRegex()
        val path = pathLines[0].split(splitRegex)

        val startPos = map.filter { it.value == '.' && it.pos.y == 0 }.minBy { it.pos.x }.pos
        var pos = startPos
        var direction = Direction.E

        for (pathSegment in path) {
            if (pathSegment == "L") {
                direction = direction.turnLeft()
            } else if (pathSegment == "R") {
                direction = direction.turnRight()
            } else {
                val moves = pathSegment.toInt()
                for (i in 1..moves) {
                    var next = pos
                    do {
                        next = (next + direction).wrapAround(map)
                    } while (map[next] == ' ')
                    if (map[next] == '#') {
                        break
                    }
                    pos = next
                }
            }
        }
        val password = 1000 * (pos.y + 1) + 4 * (pos.x + 1) + when (direction) {
            Direction.E -> 0
            Direction.S -> 1
            Direction.W -> 2
            Direction.N -> 3
        }

        return password
    }

    override fun part2(input: List<String>): Int {
        val (mapLines, pathLines) = input.split(listOf(""))
        val dimx = mapLines.maxOf { it.length }
        val dimy = mapLines.size
        val cubedim = if (dimx / 4 == dimy / 3) dimx / 4 else if (dimx / 3 == dimy / 4) dimx / 3 else throw IllegalArgumentException("Unhandled")
        var map = mapLines.map { it.padEnd(dimx) }.toXYMap()
        //map = mapLines.map { it.padEnd(dimx) }.map { it.replace('#', '.') }.toXYMap()
        val splitRegex = "(?<=[LR])|(?=[LR])".toRegex()
        var path = pathLines[0].split(splitRegex)

        val startPos = map.filter { it.value == '.' && it.pos.y == 0 }.minBy { it.pos.x }.pos
        val (pos, direction) = moveCube(startPos, Direction.E, path, map)

        val password = 1000 * (pos.y + 1) + 4 * (pos.x + 1) + when (direction) {
            Direction.E -> 0
            Direction.S -> 1
            Direction.W -> 2
            Direction.N -> 3
        }

        return password
    }

    fun moveCube(startPos: Pos, startDirection: Direction, path: List<String>, map: XYMap<Char>, print: Boolean = false): Pair<Pos, Direction> {
        var pos = startPos
        var direction = startDirection
        for (pathSegment in path) {
            val segmentPath = mutableMapOf<Pos, Char>()
            segmentPath[pos] = direction.toChar()
            if (pathSegment == "L") {
                direction = direction.turnLeft()
            } else if (pathSegment == "R") {
                direction = direction.turnRight()
            } else {
                val moves = pathSegment.toInt()
                for (i in 1..moves) {
                    var (next, nextdir) = (pos to direction).moveAroundCube(map)
                    if (map[next] == '#') {
                        break
                    }
                    pos = next
                    direction = nextdir
                    segmentPath[pos] = direction.toChar()
                }
            }
            if (print) {
                segmentPath[pos] = direction.toChar()
                println(pathSegment)
                map.printChars { if (it == startPos) '*' else segmentPath[it] ?: map[it] }
                println()
            }
        }
        return pos to direction
    }
}

private fun Pair<Pos, Direction>.moveAroundCube(map: XYMap<Char>): Pair<Pos, Direction> {
    if (map.dimx / 4 == map.dimy / 3) {
        //  1
        //234
        //  56
        val cubedim = map.dimx / 4
        // 2N* <-> 1N*
        if (this.first.x in 0 until cubedim && this.first.y == cubedim && this.second == Direction.N) {
            return Pos(3 * cubedim - 1 - this.first.x, 0) to Direction.S
        }
        if (this.first.x in 2 * cubedim until 3 * cubedim && this.first.y == 0 && this.second == Direction.N) {
            return Pos(3 * cubedim - 1 - this.first.x, cubedim) to Direction.S
        }

        // 3N* <-> *1W
        if (this.first.x in cubedim until 2 * cubedim && this.first.y == cubedim && this.second == Direction.N) {
            return Pos(2 * cubedim, this.first.x - cubedim) to Direction.E
        }
        if (this.first.x == 2 * cubedim && this.first.y in 0 until cubedim && this.second == Direction.W) {
            return Pos(cubedim + this.first.y, cubedim) to Direction.S
        }

        // 6N <-> 4E*
        if (this.first.x in 3 * cubedim until 4 * cubedim && this.first.y == 2 * cubedim && this.second == Direction.N) {
            return Pos(3 * cubedim - 1, 5 * cubedim - 1 - this.first.x) to Direction.W
        }
        if (this.first.x == 3 * cubedim - 1 && this.first.y in cubedim until 2 * cubedim && this.second == Direction.E) {
            return Pos(5 * cubedim - 1 - this.first.y, 2 * cubedim) to Direction.S
        }

        // 2W* <-> 6S*
        if (this.first.x == 0 && this.first.y in cubedim until 2 * cubedim && this.second == Direction.W) {
            return Pos(5 * cubedim - 1 - this.first.y, 3 * cubedim - 1) to Direction.N
        }
        if (this.first.x in 3 * cubedim until 4 * cubedim && this.first.y == 3 * cubedim - 1 && this.second == Direction.S) {
            return Pos(0, 5 * cubedim - 1 - this.first.x) to Direction.E
        }

        // 2S* <-> 5S*
        if (this.first.x in 0 until cubedim && this.first.y == 2 * cubedim - 1 && this.second == Direction.S) {
            return Pos(3 * cubedim - 1 - this.first.x, 3 * cubedim - 1) to Direction.N
        }
        if (this.first.x in 2 * cubedim until 3 * cubedim && this.first.y == 3 * cubedim - 1 && this.second == Direction.S) {
            return Pos(3 * cubedim - 1 - this.first.x, 2 * cubedim - 1) to Direction.N
        }

        // 3S* <-> 5W*
        if (this.first.x in cubedim until 2 * cubedim && this.first.y == 2 * cubedim - 1 && this.second == Direction.S) {
            return Pos(2 * cubedim, 4 * cubedim - 1 - this.first.x) to Direction.E
        }
        if (this.first.x == 2 * cubedim && this.first.y in 2 * cubedim until 3 * cubedim && this.second == Direction.W) {
            return Pos(4 * cubedim - 1 - this.first.y, 2 * cubedim - 1) to Direction.N
        }

        // 1E* <-> 6E*
        if (this.first.x == 3 * cubedim - 1 && this.first.y in 0 until cubedim && this.second == Direction.E) {
            return Pos(4 * cubedim - 1, 3 * cubedim - 1 - this.first.y) to Direction.W
        }
        if (this.first.x == 4 * cubedim - 1 && this.first.y in 2 * cubedim until 3 * cubedim && this.second == Direction.E) {
            return Pos(3 * cubedim - 1, 3 * cubedim - 1 - this.first.y) to Direction.W
        }
    } else if (map.dimx / 3 == map.dimy / 4) {
        // 12
        // 3
        //45
        //6
        val cubedim = map.dimx / 3
        // 1N <-> 6W
        if (this.first.x in cubedim until 2 * cubedim && this.first.y == 0 && this.second == Direction.N) {
            return Pos(0, this.first.x + 2 * cubedim) to Direction.E
        }
        if (this.first.x == 0 && this.first.y in 3 * cubedim until 4 * cubedim && this.second == Direction.W) {
            return Pos(this.first.y - 2 * cubedim, 0) to Direction.S
        }

        // 2N <-> 6S
        if (this.first.x in cubedim * 2 until cubedim * 3 && this.first.y == 0 && this.second == Direction.N) {
            return Pos(this.first.x - 2 * cubedim, 4 * cubedim - 1) to Direction.N
        }
        if (this.first.x in 0 until cubedim && this.first.y == 4 * cubedim - 1 && this.second == Direction.S) {
            return Pos(this.first.x + 2 * cubedim, 0) to Direction.S
        }

        // 1W <-> 4W
        if (this.first.x == cubedim && this.first.y in 0 until cubedim && this.second == Direction.W) {
            return Pos(0, 3 * cubedim - 1 - this.first.y) to Direction.E
        }
        if (this.first.x == 0 && this.first.y in 2 * cubedim until 3 * cubedim && this.second == Direction.W) {
            return Pos(cubedim, 3 * cubedim - 1 - this.first.y) to Direction.E
        }

        // 2E <-> 5E
        if (this.first.x == 3 * cubedim - 1 && this.first.y in 0 until cubedim && this.second == Direction.E) {
            return Pos(2 * cubedim - 1, 3 * cubedim - 1 - this.first.y) to Direction.W
        }
        if (this.first.x == 2 * cubedim - 1 && this.first.y in 2 * cubedim until 3 * cubedim && this.second == Direction.E) {
            return Pos(3 * cubedim - 1, 3 * cubedim - 1 - this.first.y) to Direction.W
        }

        // 2S <-> 3E
        if (this.first.x in 2 * cubedim until 3 * cubedim && this.first.y == cubedim - 1 && this.second == Direction.S) {
            return Pos(2 * cubedim - 1, this.first.x - cubedim) to Direction.W
        }
        if (this.first.x == 2 * cubedim - 1 && this.first.y in cubedim until 2 * cubedim && this.second == Direction.E) {
            return Pos(this.first.y + cubedim, cubedim - 1) to Direction.N
        }

        // 5S <-> 6E
        if (this.first.x in cubedim until 2 * cubedim && this.first.y == 3 * cubedim - 1 && this.second == Direction.S) {
            return Pos(cubedim - 1, this.first.x + 2 * cubedim) to Direction.W
        }
        if (this.first.x == cubedim - 1 && this.first.y in 3 * cubedim until 4 * cubedim && this.second == Direction.E) {
            return Pos(this.first.y - 2 * cubedim, 3 * cubedim - 1) to Direction.N
        }

        // 3W <-> 4N
        if (this.first.x == cubedim && this.first.y in cubedim until 2 * cubedim && this.second == Direction.W) {
            return Pos(this.first.y - cubedim, 2 * cubedim) to Direction.S
        }
        if (this.first.x in 0 until cubedim && this.first.y == 2 * cubedim && this.second == Direction.N) {
            return Pos(cubedim, this.first.x + cubedim) to Direction.E
        }
    } else throw IllegalArgumentException("Unhandled")
    return (this.first + this.second) to this.second
}

fun main(args: Array<String>) {
    AdventRunner(2022, 22, Day22()).run()

}
