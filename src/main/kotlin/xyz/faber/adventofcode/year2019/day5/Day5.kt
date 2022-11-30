package xyz.faber.adventofcode.year2019.day5

import adventofcode.year2019.intcode.Machine
import adventofcode.year2019.intcode.decompile
import xyz.faber.adventofcode.util.getProgram

fun part1(input: List<Long>) {
    val machine = Machine(input)
    machine.run(1)
    println(machine.lastOutput)
}

fun part2(input: List<Long>) {
    val machine = Machine(input)
    machine.run(5)
    println(machine.lastOutput)
}


fun main(args: Array<String>) {
    val program = getProgram(2019, 5)
    part1(program)
    println("---")
    part2(program)
    println("---")
    decompile(program)
}
