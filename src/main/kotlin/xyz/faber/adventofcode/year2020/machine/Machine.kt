package xyz.faber.adventofcode.year2020.machine

import java.util.*

class Machine(val program: List<Ins>) {
    var ip = 0
    var acc = 0
    var done = false

    fun step(){
        val ins = program[ip]
        when(ins.op){
            Op.ACC -> acc += ins.arg1
            Op.JMP -> ip += ins.arg1 -1
            Op.NOP -> {}
        }
        ip ++
        if(ip>=program.size){
            done = true
        }
    }

    fun runUntilRepeatOrDone(){
        val run = mutableSetOf<Int>()
        while (!done && ip !in run) {
            run += ip
            step()
        }
    }
}

class Ins(val op: Op, val arg1: Int)

enum class Op {
    ACC,
    JMP,
    NOP
}

fun parseProgram(lines: List<String>) = lines.map { parseLine(it) }

fun parseLine(line: String) = line.split(" ").let { Ins(Op.valueOf(it[0].uppercase(Locale.getDefault())), it[1].toInt()) }
