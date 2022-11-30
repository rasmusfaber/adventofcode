package xyz.faber.adventofcode.year2019.day21

import adventofcode.year2019.intcode.convertToString
import adventofcode.year2019.intcode.runMachine
import adventofcode.year2019.intcode.runMachineOnFileContinuously
import xyz.faber.adventofcode.util.getInputLongsFromCsv
import java.io.File

class Day21 {
    val input = getInputLongsFromCsv(2019, 21)

    fun part1() {
        val program2 = listOf(
                "NOT A T",
                "NOT B T",
                "AND T J",
                "NOT C T",
                "AND T J",
                "AND D J",
                "NOT C T",
                "OR T J",
                /*"NOT B T",
                "NOT T T",
                "AND T J",
                */
                "WALK")

        // ..@..............
        // #####...#########
        // #####.@.#########
        // #####.@.#########
        val program3 = listOf(
                "NOT A J",
                "WALK")
        // #####..#@########
        val program4 = listOf(
                "NOT A J",
                "NOT B T",
                "OR T J",
                "AND D J",
                "WALK")
        // #####@#..########
        val program5 = listOf(
                "NOT A J",
                "NOT B T",
                "OR T J",
                "WALK")
        // #####..@#########
        val program = listOf(
                "NOT A J",
                "NOT B T",
                "OR T J",
                "NOT C T",
                "OR T J",
                "AND D J",
                "WALK")
        //
        val res = runMachine(input, program)
        if (res.size == 1) {
            println(res[0])
        } else {
            println(res.convertToString())
            println(res[res.size - 1])
        }
    }


    fun part2() {
        val program2 = listOf(
                "NOT A J",
                "NOT B T",
                "OR T J",
                "NOT C T",
                "OR T J",
                "AND D J",
                "RUN")
        // #####.#@##..#.###
        val program3 = listOf(
                "NOT A J",
                "NOT B T",
                "OR T J",
                "NOT C T",
                "OR T J",
                "AND D J",
                "AND E J",
                "RUN")
        // #####@.#.########
        val program = listOf(
                "NOT A J",
                "NOT B T",
                "OR T J",
                "NOT C T",
                "OR T J",
                "AND D J",
                "NOT J T",
                "AND J T",
                "OR E T",
                "OR H T",
                "AND T J",
                "RUN")
        // #####.#@##..#.###

        val res = runMachine(input, program)
        println(res.convertToString())
        println(res[res.size - 1])

    }

    fun partx(){
        runMachineOnFileContinuously(input, File("src/main/resources/adventofcode/2019/day21/input.txt"))
    }
}

fun main(args: Array<String>) {
    val d = Day21()
    //d.part1()
    //d.part2()
    d.partx()
}
