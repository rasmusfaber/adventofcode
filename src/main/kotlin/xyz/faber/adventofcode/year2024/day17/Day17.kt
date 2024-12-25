package xyz.faber.adventofcode.year2024.day17

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.extractNumbers

class Device(val ins: List<Int>, var rega: Long, var regb: Long, var regc: Long) {
    var pc = 0
    val output = mutableListOf<Int>()
    var jumping = false
    var debug = false

    fun combo(operand: Long): Long {
        return when (operand) {
            in 0L..3L -> operand
            4L -> return rega
            5L -> return regb
            6L -> return regc
            else -> throw IllegalArgumentException("Invalid combo operand")
        }
    }

    fun adv(operand: Long) {
        val num = rega
        val den = 1 shl combo(operand).toInt()
        rega = num / den
    }

    fun bxl(operand: Long) {
        regb = regb xor operand
    }

    fun bst(operand: Long) {
        regb = combo(operand) and 7
    }

    fun jnz(operand: Long) {
        if (rega != 0L) {
            pc = operand.toInt()
            jumping = true
        }
    }

    fun bxc(operand: Long) {
        regb = regb xor regc
    }

    fun out(operand: Long) {
        output += combo(operand).toInt() and 7
    }

    fun bdv(operand: Long) {
        val num = rega
        val den = 1 shl combo(operand).toInt()
        regb = num / den
    }

    fun cdv(operand: Long) {
        val num = rega
        val den = 1 shl combo(operand).toInt()
        regc = num / den
    }

    fun step() {
        val opcode = ins[pc]
        val operand = ins[pc + 1].toLong()
        if (debug) {
            println("pc: $pc, opcode: $opcode, operand: $operand")
            println("  before: rega: $rega, regb: $regb, regc: $regc")
        }
        when (opcode) {
            0 -> adv(operand)
            1 -> bxl(operand)
            2 -> bst(operand)
            3 -> jnz(operand)
            4 -> bxc(operand)
            5 -> out(operand)
            6 -> bdv(operand)
            7 -> cdv(operand)
            else -> throw IllegalArgumentException("Invalid opcode")
        }
        if (jumping) {
            jumping = false
        } else {
            pc += 2
        }
        if (debug) {
            println("  after:  rega: $rega, regb: $regb, regc: $regc")
        }

    }

    fun run() {
        while (!done) {
            step()
        }
    }

    val done get() = pc !in 0 until ins.size
}


class Day17 : AdventSolution<String>() {
    override fun part1(input: List<String>): String {
        val a = input[0].extractNumbers()[0].toLong()
        val b = input[1].extractNumbers()[0].toLong()
        val c = input[2].extractNumbers()[0].toLong()
        val program = input[4].split(": ").last().split(",").map { it.toInt() }

        val device = Device(program, a, b, c)
        device.debug = false
        device.run()
        return device.output.joinToString(",")
    }

    override fun part2(input: List<String>): String {
        val program = input[4].split(": ").last().split(",").map { it.toInt() }
        if(program.size!=16){
            return ""
        }
        var a = 0L
        for (i in program.reversed()) {
            a = a shl 3
            var startb = 0
            while (true) {
                var b = startb and 7
                b = b xor 5
                var c = (((a+startb) shr b) and 7).toInt()
                b = b xor 6
                b = b xor c
                if ((b and 7) == i) {
                    break
                }
                startb++
            }
            a += startb
        }

        return a.toString()
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 17, Day17()).run()
}