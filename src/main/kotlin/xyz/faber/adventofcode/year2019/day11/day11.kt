package xyz.faber.adventofcode.year2019.day11

import adventofcode.year2019.intcode.Machine
import xyz.faber.adventofcode.util.*

class Day11 {
    val input = getInputLongsFromCsv(2019, 11)

    fun part1(): Int {
        val robot = Robot()
        val machine = Machine(input)

        machine.receiveInput = { robot.currentColor() }
        machine.sendOutput = { robot.receiveInput(it) }

        machine.run()

        return robot.colorByPos.size
    }


    fun part2() {
        val robot = Robot()
        val machine = Machine(input)

        robot.colorByPos[Pos(0, 0)] = 1

        machine.receiveInput = { robot.currentColor() }
        machine.sendOutput = { robot.receiveInput(it) }

        machine.run()

        robot.colorByPos.toXYMap().printBlockIf { it == 1L }

        println(robot.colorByPos.toXYMap().detectText { it == 1L })
    }

    class Robot {
        var dir = Direction.N
        var pos = Pos(0, 0)
        val colorByPos = mutableMapOf<Pos, Long>()
        var lastSentColor: Long? = null
        var animation: Animation? = null

        fun currentColor() = colorByPos[pos] ?: 0L

        fun receiveInput(value: Long) {
            if (lastSentColor == null) {
                lastSentColor = value
            } else {
                colorByPos[pos] = lastSentColor!!
                if (value == 0L) {
                    dir = dir.turnLeft()
                } else {
                    dir = dir.turnRight()
                }
                pos = pos.move(dir)
                lastSentColor = null
                if (animation != null) {
                    val posWithDelta = pos + colorByPos.getDelta()
                    animation!!.frame(colorByPos.toXYMap(), posWithDelta)
                }
            }
        }
    }

}

fun main(args: Array<String>) {
    val d = Day11()
    println(d.part1())
    d.part2()
}
