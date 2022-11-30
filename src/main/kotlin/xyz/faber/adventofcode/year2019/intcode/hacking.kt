package xyz.faber.adventofcode.year2019.intcode

import adventofcode.year2019.intcode.Machine

fun Machine.findMethods(): List<IntRange> {
    val calls = this.mem.findSequence(2) { (op, arg) -> (op == 109L && arg > 0) }
    val returns = this.mem.findSequence(2) { (op, arg) -> (op == 2106L && arg == 0L) || (op == 2105L && arg != 1L) }

    return returns.map { r ->
        val c = calls.filter { it < r }.maxOrNull()!!
        r..c
    }
}

//fun Machine.findMethodWithCode(code: List<Long>): IntRange? = findMethods().first {  }

fun Machine.executeMethod(method: IntRange, params: List<Long>) {
    val oldIP = this.ip
    val oldBaseOffset = this.baseOffset

    if (this.baseOffset < this.mem.size) {
        this.baseOffset = this.mem.size.toLong()
    }
    this.setMem(-1, 99)
    this.setMem(this.baseOffset, -1)
    params.withIndex().forEach { (i, param) -> this.setMem(this.baseOffset + 1 + i, param) }
    this.ip = method.first.toLong()

    this.run()

    this.ip = oldIP
    this.baseOffset = oldBaseOffset
}

fun <T> List<T>.findSequence(length: Int, predicate: (List<T>) -> Boolean) = this.windowed(2).withIndex()
        .filter { (_, values) -> predicate(values) }.map { it.index }