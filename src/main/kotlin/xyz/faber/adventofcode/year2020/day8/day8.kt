package xyz.faber.adventofcode.year2020.day8

import xyz.faber.adventofcode.util.getInputFromLines
import xyz.faber.adventofcode.year2020.machine.Ins
import xyz.faber.adventofcode.year2020.machine.Machine
import xyz.faber.adventofcode.year2020.machine.Op
import xyz.faber.adventofcode.year2020.machine.parseProgram

class Day8 {
    val input = getInputFromLines(2020, 8)
    val program = parseProgram(input)

    fun part1() {
        val machine = Machine(program)
        machine.runUntilRepeatOrDone()
        println(machine.acc)
    }


    fun part2() {
        for (changeline in 0..program.size - 1) {
            val original = program[changeline]
            if (original.op == Op.ACC) {
                continue
            }
            val copy = program.toMutableList()
            if (original.op == Op.NOP) {
                copy[changeline] = Ins(Op.JMP, original.arg1)
            } else {
                copy[changeline] = Ins(Op.NOP, original.arg1)
            }
            val machine = Machine(copy)
            machine.runUntilRepeatOrDone()
            if (machine.done) {
                println(machine.acc)
                return
            }
        }
    }

}

fun main(args: Array<String>) {
    val d = Day8()
    d.part1()
    d.part2()
}
