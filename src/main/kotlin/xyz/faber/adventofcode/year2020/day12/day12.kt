package xyz.faber.adventofcode.year2020.day12

import xyz.faber.adventofcode.util.Direction
import xyz.faber.adventofcode.util.Pos
import xyz.faber.adventofcode.util.getInputFromLines
import xyz.faber.adventofcode.util.manhattanDistance

class Day12 {
    val input = getInputFromLines(2020, 12).map { it[0] to it.substring(1).toInt() }
    /*val input = ("F10\n" +
            "N3\n" +
            "F7\n" +
            "R90\n" +
            "F11").lines().map{it[0] to it.substring(1).toInt()}*/

    fun part1() {
        var pos = Pos(0, 0)
        var dir = Direction.E
        for (l in input) {
            when (l.first) {
                'N' -> pos = pos + Pos(0, -l.second)
                'S' -> pos = pos + Pos(0, l.second)
                'W' -> pos = pos + Pos(-l.second, 0)
                'E' -> pos = pos + Pos(l.second, 0)
                'R' -> for (i in 1..l.second / 90) dir = dir.turnRight()
                'L' -> for (i in 1..l.second / 90) dir = dir.turnLeft()
                'F' -> pos = pos.move(dir, l.second)
            }
            println(l.toString() + " " + pos.toString() + " " + dir.toString())
        }
        println(manhattanDistance(Pos(0, 0), pos))
    }


    fun part2() {
        var pos = Pos(0, 0)
        var waypoint = Pos(10, -1)
        var dir = Direction.E
        for (l in input) {
            when (l.first) {
                'N' -> waypoint = waypoint + Pos(0, -l.second)
                'S' -> waypoint = waypoint + Pos(0, l.second)
                'W' -> waypoint = waypoint + Pos(-l.second, 0)
                'E' -> waypoint = waypoint + Pos(l.second, 0)
                'R' -> for (i in 1..l.second / 90) waypoint = Pos(-waypoint.y, waypoint.x)
                'L' -> for (i in 1..l.second / 90) waypoint = Pos(waypoint.y, -waypoint.x)
                'F' -> for (i in 1..l.second) pos = pos + waypoint
            }
            println(l.toString() + " " + pos.toString() + " " + dir.toString())
        }
        println(manhattanDistance(Pos(0, 0), pos)) // not 355
    }

}

fun main(args: Array<String>) {
    val d = Day12()
    d.part1()
    d.part2()
}
