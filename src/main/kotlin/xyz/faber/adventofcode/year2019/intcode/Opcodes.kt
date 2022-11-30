package adventofcode.year2019.intcode

import kotlin.reflect.KClass

abstract sealed class Opcode(val code: Int, val name: String, val numInput: Int, val numOutput: Int) {
    open val isJump = false

    abstract suspend fun exec(machine: Machine, modes: Long)
}

abstract sealed class OpcodeBiFunc(code: Int, name: String) : Opcode(code, name, 2, 1) {
    abstract fun calc(v1: Long, v2: Long): Long
    override suspend fun exec(machine: Machine, modes: Long) {
        val v1 = machine.getValue(machine.next(), modes, 0)
        val v2 = machine.getValue(machine.next(), modes, 1)
        val res = calc(v1, v2)
        machine.setValue(machine.next(), modes, 2, res)
    }
}

abstract sealed class OpcodeJumpFunc(code: Int, name: String) : Opcode(code, name, 2, 0) {
    override val isJump = true

    abstract fun check(v1: Long): Boolean
    override suspend fun exec(machine: Machine, modes: Long) {
        val arg1 = machine.getValue(machine.next(), modes, 0)
        val arg2 = machine.getValue(machine.next(), modes, 1)
        if (check(arg1)) {
            machine.ip = arg2
        }
    }
}

class AddOpcode : OpcodeBiFunc(1, "ADD") {
    override fun calc(v1: Long, v2: Long): Long {
        return v1 + v2
    }
}

class MulOpcode : OpcodeBiFunc(2, "MUL") {
    override fun calc(v1: Long, v2: Long): Long {
        return v1 * v2
    }
}

class InputOpcode : Opcode(3, "INPUT", 0, 1) {
    override suspend fun exec(machine: Machine, modes: Long) {
        val input = machine.receiveInput()
        machine.setValue(machine.next(), modes, 0, input)
    }
}

class OutputOpcode : Opcode(4, "OUTPUT", 1, 0) {
    override suspend fun exec(machine: Machine, modes: Long) {
        val output = machine.getValue(machine.next(), modes, 0)
        machine.sendOutput(output)
        machine.lastOutput = output
        if (machine.printOutput) {
            println(output)
        }else if(machine.printOutputAsAscii){
            print(output.toChar())
        }
    }
}

class JnzOpcode : OpcodeJumpFunc(5, "JNZ") {
    override fun check(v1: Long): Boolean {
        return v1 != 0L
    }
}

class JzOpcode : OpcodeJumpFunc(6, "JZ") {
    override fun check(v1: Long): Boolean {
        return v1 == 0L
    }
}

class LtOpcode : OpcodeBiFunc(7, "LT") {
    override fun calc(v1: Long, v2: Long): Long {
        return if (v1 < v2) 1L else 0L
    }
}

class EqOpcode : OpcodeBiFunc(8, "EQ") {
    override fun calc(v1: Long, v2: Long): Long {
        return if (v1 == v2) 1 else 0
    }
}

class RelOffsetOpcode : Opcode(9, "OFFSET", 1, 0) {
    override suspend fun exec(machine: Machine, modes: Long) {
        val offset = machine.getValue(machine.next(), modes, 0)
        machine.baseOffset += offset
    }
}

class HaltOpcode : Opcode(99, "HALT", 0, 0) {
    override suspend fun exec(machine: Machine, modes: Long) {
        machine.done = true
    }
}

val opcodes = recursiveSubclasses(Opcode::class).flatMap { it.constructors }
        .filter { it.parameters.isEmpty() }
        .map { it.call() }
        .map { it.code to it }.toMap()

private fun <T : Any> recursiveSubclasses(c: KClass<T>): Collection<KClass<out T>> {
    val directSubclasses = c.sealedSubclasses
    return directSubclasses.plus(directSubclasses.flatMap { recursiveSubclasses(it) })
}

fun parseCodeVal(codeVal: Long): Pair<Opcode, Long> {
    val opcode = opcodes[(codeVal % 100).toInt()] ?: throw RuntimeException("Unknown opcode $codeVal")
    val modes = codeVal / 100
    return Pair(opcode, modes)
}
