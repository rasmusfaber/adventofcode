package xyz.faber.adventofcode.year2019.day13

import adventofcode.year2019.intcode.Machine
import adventofcode.year2019.intcode.collectIntParams
import xyz.faber.adventofcode.util.IntXYMap
import xyz.faber.adventofcode.util.getInputLongsFromCsv
import xyz.faber.adventofcode.util.toMap
import kotlin.math.sign

class Day13 {
    val input = getInputLongsFromCsv(2019, 13)

    fun part1(): Int {
        val machine = Machine(input)
        val map = IntXYMap()
        machine.sendOutput = collectIntParams { x, y, tile ->
            map[x, y] = tile
        }
        machine.run()
        //map.printColors()
        return map.count { it.value == 2 }

    }


    fun part2(): Int {
        val machine = Machine(input)
        machine.mem[0] = 2
        val map = IntXYMap()
        var score = 0
        machine.sendOutput = collectIntParams { x, y, z ->
            if (x == -1 && y == 0) {
                score = z
            } else {
                map[x, y] = z
            }
        }
        machine.receiveInput = {
            val paddleX = map.toMap().filter { it.value == 3 }.keys.first().x
            val ballX = map.toMap().filter { it.value == 4 }.keys.first().x

            (ballX - paddleX).sign.toLong()
        }
        machine.run()
        return score

    }

    private fun map(tile: Int): Char {
        return when (tile) {
            0 -> ' '
            1 -> 'X'
            2 -> '='
            3 -> '-'
            4 -> 'o'
            else -> '?'
        }
    }

}

fun main(args: Array<String>) {
    val d = Day13()
    println(d.part1())
    println(d.part2())
    //decompile(d.input)
}
