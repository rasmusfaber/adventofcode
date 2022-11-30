package xyz.faber.adventofcode.year2019.day2

import adventofcode.year2019.intcode.Machine
import xyz.faber.adventofcode.util.getProgram

fun part1(input: List<Long>): Long {
    val program = input.toMutableList()
    program[1] = 12
    program[2] = 2
    val machine = Machine(program)
    machine.run()
    return machine.mem[0]
}

fun part2(input: List<Long>): Long {
    val target = 19690720L

    for (verb in 0L..99L) {
        for (noun in 0L..99L) {
            val copy = input.toMutableList()
            copy[1] = noun
            copy[2] = verb
            val machine = Machine(copy)
            machine.run()
            if (machine.mem[0] == target) {
                return noun * 100 + verb
            }
        }
    }
    throw RuntimeException("Target not found")
}


fun main(args: Array<String>) {
    val input = getProgram(2019, 2)
    println(part1(input))
    println(part2(input))
}
