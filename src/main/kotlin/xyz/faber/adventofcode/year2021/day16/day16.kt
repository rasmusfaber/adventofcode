package xyz.faber.adventofcode.year2021.day16

import xyz.faber.adventofcode.util.getInput

class Day16 {
    val input = getInput(2021, 16)

    fun toBinary(hex: String): String {
        return hex.filter { it.isLetterOrDigit() }.map { it.toString().toInt(16).toString(2).padStart(4, '0') }.joinToString("")
    }

    fun part1(input: String): Int {
        val b = toBinary(input)
        val reader = Reader(b)
        val packet = reader.readPacket()
        return packet.getVersionSum()

    }

    fun part2(input: String): Long {
        val b = toBinary(input)
        val reader = Reader(b)
        val packet = reader.readPacket()
        println(packet)
        return packet.getValue()// 812835898 too low
    }
}

abstract class Packet(val version: Int, val type: Int) {
    abstract fun getVersionSum(): Int
    abstract fun getValue(): Long
}

class LiteralPacket(version: Int, type: Int, val literal: Long) : Packet(version, type) {
    override fun getVersionSum() = version
    override fun getValue(): Long = literal
    override fun toString(): String {
        return literal.toString()
    }

}

class opPacket(version: Int, type: Int, val subPackets: List<Packet>) : Packet(version, type) {
    override fun getVersionSum() = version + subPackets.sumOf { it.getVersionSum() }
    override fun getValue(): Long = when (type) {
        0 -> subPackets.sumOf { it.getValue() }
        1 -> subPackets.productOf { it.getValue() }
        2 -> subPackets.minOfOrNull { it.getValue() }!!
        3 -> subPackets.maxOfOrNull { it.getValue() }!!
        5 -> if (subPackets[0].getValue() > subPackets[1].getValue()) 1 else 0
        6 -> if (subPackets[0].getValue() < subPackets[1].getValue()) 1 else 0
        7 -> if (subPackets[0].getValue() == subPackets[1].getValue()) 1 else 0
        else -> throw IllegalArgumentException("Unknown op type $type")
    }

    override fun toString(): String {
        return "[" + type.toString() + " (" + subPackets.map { it.toString() }.joinToString(", ") + ") =" + getValue().toString() + "]"
    }
}

inline fun <T> Iterable<T>.productOf(selector: (T) -> Long): Long {
    var sum = 1L
    for (element in this) {
        sum *= selector(element)
    }
    return sum
}

class Reader(val s: String) {
    var i = 0
    fun readChar(): Char {
        val c = s[i]
        i++
        return c
    }

    fun readString(len: Int): String {
        val sb = StringBuilder()
        for (i in 0 until len) {
            sb.append(readChar())
        }
        return sb.toString()
    }

    fun readOctet(): Int {
        return readString(3).toInt(2)
    }

    fun read11(): Int {
        return readString(11).toInt(2)
    }

    fun read15(): Int {
        return readString(15).toInt(2)
    }

    fun readLiteral(): Long {
        var res = 0L
        do {
            val done = readChar() == '0'
            res = res * 16 + readString(4).toInt(2)
        } while (!done)
        return res
    }

    fun readPacket(): Packet {
        val version = readOctet()
        val type = readOctet()
        if (type == 4) {
            val literal = readLiteral()
            return LiteralPacket(version, type, literal)
        } else {
            val lengthType = readChar()
            if (lengthType == '0') {
                val length = read15()
                val subPackets = mutableListOf<Packet>()
                val subStart = i
                while (i < subStart + length) {
                    val subPacket = readPacket()
                    subPackets.add(subPacket)
                }
                return opPacket(version, type, subPackets)
            } else {
                val length = read11()
                val subPackets = mutableListOf<Packet>()
                for (j in 0 until length) {
                    val subPacket = readPacket()
                    subPackets.add(subPacket)
                }
                return opPacket(version, type, subPackets)
            }
        }
    }
}

fun main(args: Array<String>) {
    val d = Day16()

    println(d.part1(d.input))
    println(d.part2(d.input))
}
