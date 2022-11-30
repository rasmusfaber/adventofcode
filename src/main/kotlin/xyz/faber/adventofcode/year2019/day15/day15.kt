package xyz.faber.adventofcode.year2019.day15

import adventofcode.year2019.intcode.Machine
import adventofcode.year2019.intcode.decompile
import xyz.faber.adventofcode.util.*
import java.util.*

class Day15 {
    val input = getInputLongsFromCsv(2019, 15)
    var animation: Animation? = null

    fun part1() {
        //animation = Animation()
        val map = getMap()
        animation?.loop(10L)
        map.printChars {
            when (map[it]) {
                0 -> ' '
                1 -> '#'
                2 -> 'O'
                else -> '?'
            }
        }
        val graph = map.toGraph(setOf(-1, 1))
        val start = Pos(0, 0)
        val goal = map.positions().first { map[it] == 2 }
        val path = aStar(graph, ::manhattanDistance, start, goal)!!.toDirections()
        println(path.size)
    }

    private fun getMap(): XYMap<Int> {
        val droid = Machine(input)
        droid.runAsync()
        val map = XYMap<Int>(-1)
        var pos = Pos(0, 0)
        map[pos] = 0
        val topology = map.toGraph(setOf(-1, 1))
        val queue = LinkedList<Pos>()
        queue.addFirst(Pos(0, 0))
        //var nextPos = unknownPos(map)
        while (!queue.isEmpty()) {
            val nextPos = queue.pop()
            val path = aStar(topology, ::manhattanDistance, pos, nextPos)!!.toDirections()
            for (direction in path) {
                droid.sendMoveCommand(direction)
                val res = droid.receive()
                if (res == 0L) {
                    throw RuntimeException("Unexpected wall")
                }
                pos += direction
                frame(map, pos)
            }
            pos = nextPos
            for (dir in Direction.values()) {
                if (map[pos + dir] == -1) {
                    val res = droid.testMove(dir)
                    when (res) {
                        0L -> map[pos + dir] = 1
                        1L -> {
                            map[pos + dir] = 0; queue.addFirst(pos + dir)
                        }
                        2L -> map[pos + dir] = 2
                        else -> throw java.lang.RuntimeException()
                    }
                    frame(map, pos)
                }
            }
            //map.printColors()
            //println("---")
            //nextPos = unknownPos(map)

        }
        return map
    }

    private fun Machine.sendMoveCommand(direction: Direction) {
        when (direction) {
            Direction.N -> this.send(1)
            Direction.E -> this.send(4)
            Direction.S -> this.send(2)
            Direction.W -> this.send(3)
        }
    }

    private fun frame(map: XYMap<Int>, pos: Pos) {
        animation?.frame(map, pos)
    }

    fun Machine.testMove(direction: Direction): Long {
        this.sendMoveCommand(direction)
        val res = this.receive()
        if (res != 0L) {
            this.sendMoveCommand(direction.opposite())
            val res2 = this.receive()
            if (res2 == 0L) throw RuntimeException("Received $res2")
        }
        return res
    }

    fun unknownPos(map: XYMap<Int>): Pos? {
        return map.positions().filter { map[it] == 0 }
                .filter { p ->
                    Direction.values().any { map[p + it] == -1 }
                }
                .firstOrNull()
    }


    fun part2() {
        val map = getMap()
        val topology = map.toGraph(setOf(-1, 1))
        var oxygen = map.positions().filter { map[it] == 2 }.single()
        val res = bfsWithPath(topology, oxygen).maxByOrNull { it.size }!!.size - 1
        println(res)
    }

}

fun main(args: Array<String>) {
    val d = Day15()
    d.part1()
    d.part2()
    decompile(d.input)
}
