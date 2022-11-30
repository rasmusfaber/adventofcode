package xyz.faber.adventofcode.year2019.day9

import adventofcode.year2019.intcode.Machine
import xyz.faber.adventofcode.util.getProgram

class Day9 {
    val input = getProgram(2019, 9)
    //val input = listOf(204,0,109,1,1001,100,1,100,1008,100,16,101,1006,101,0,99).map{it.toLong()}

    fun part1() {
        val machine = Machine(input)
        machine.printOutput = true
        machine.run(1)
    }


    fun part2() {
        val machine = Machine(input)
        machine.printOutput = true
        machine.run(2)
    }

}

fun main(args: Array<String>) {
    val d = Day9()
    d.part1()
    d.part2()
    //decompile(d.input)
}
