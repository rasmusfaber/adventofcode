package xyz.faber.adventofcode.year2024.day21

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.Pos

interface Panel {
    fun shortestSequencesTo(currentPos: Char, target: Char): List<String>
}

class MainPanel : Panel {
    fun charToPos(char: Char): Pos {
        return when (char) {
            '7' -> Pos(0, 0)
            '8' -> Pos(1, 0)
            '9' -> Pos(2, 0)
            '4' -> Pos(0, 1)
            '5' -> Pos(1, 1)
            '6' -> Pos(2, 1)
            '1' -> Pos(0, 2)
            '2' -> Pos(1, 2)
            '3' -> Pos(2, 2)
            '0' -> Pos(1, 3)
            'A' -> Pos(2, 3)
            else -> throw IllegalArgumentException("Invalid char")
        }
    }

    override fun shortestSequencesTo(currentPos: Char, target: Char): List<String> {
        val currentPos = charToPos(currentPos)
        val targetPos = charToPos(target)
        val dx = targetPos.x - currentPos.x
        val dy = targetPos.y - currentPos.y
        val result = mutableListOf<String>()
        if (currentPos.x == 0 && targetPos.y == 3) {
            result.add(">".repeat(dx) + "v".repeat(dy))
        } else if (currentPos.y == 3 && targetPos.x == 0) {
            result.add("^".repeat(-dy) + "<".repeat(-dx))
        } else if (dx > 0 && dy > 0) {
            result.add("v".repeat(dy) + ">".repeat(dx))
            result.add(">".repeat(dx) + "v".repeat(dy))
        } else if (dx > 0 && dy < 0) {
            result.add("^".repeat(-dy) + ">".repeat(dx))
            result.add(">".repeat(dx) + "^".repeat(-dy))
        } else if (dx < 0 && dy > 0) {
            result.add("v".repeat(dy) + "<".repeat(-dx))
            result.add("<".repeat(-dx) + "v".repeat(dy))
        } else if (dx < 0 && dy < 0) {
            result.add("^".repeat(-dy) + "<".repeat(-dx))
            result.add("<".repeat(-dx) + "^".repeat(-dy))
        } else if (dx > 0) {
            result.add(">".repeat(dx))
        } else if (dx < 0) {
            result.add("<".repeat(-dx))
        } else if (dy > 0) {
            result.add("v".repeat(dy))
        } else if (dy < 0) {
            result.add("^".repeat(-dy))
        } else {
            result.add("")
        }

        return result
    }

}

class SecondaryPanel : Panel {
    fun charToPos(char: Char): Pos {
        return when (char) {
            '^' -> Pos(1, 0)
            'A' -> Pos(2, 0)
            '<' -> Pos(0, 1)
            'v' -> Pos(1, 1)
            '>' -> Pos(2, 1)
            else -> throw IllegalArgumentException("Invalid char")
        }
    }

    override fun shortestSequencesTo(currentPos: Char, target: Char): List<String> {
        val currentPos = charToPos(currentPos)
        val targetPos = charToPos(target)
        val dx = targetPos.x - currentPos.x
        val dy = targetPos.y - currentPos.y
        val result = mutableListOf<String>()
        if (currentPos.x == 0 && targetPos.y == 0) {
            result.add(">".repeat(dx) + "^".repeat(-dy))
        } else if (currentPos.y == 0 && targetPos.x == 0) {
            result.add("v".repeat(dy) + "<".repeat(-dx))
        } else if (dx > 0 && dy > 0) {
            result.add("v".repeat(dy) + ">".repeat(dx))
            result.add(">".repeat(dx) + "v".repeat(dy))
        } else if (dx > 0 && dy < 0) {
            result.add("^".repeat(-dy) + ">".repeat(dx))
            result.add(">".repeat(dx) + "^".repeat(-dy))
        } else if (dx < 0 && dy > 0) {
            result.add("v".repeat(dy) + "<".repeat(-dx))
            result.add("<".repeat(-dx) + "v".repeat(dy))
        } else if (dx < 0 && dy < 0) {
            result.add("^".repeat(-dy) + "<".repeat(-dx))
            result.add("<".repeat(-dx) + "^".repeat(-dy))
        } else if (dx > 0) {
            result.add(">".repeat(dx))
        } else if (dx < 0) {
            result.add("<".repeat(-dx))
        } else if (dy > 0) {
            result.add("v".repeat(dy))
        } else if (dy < 0) {
            result.add("^".repeat(-dy))
        } else {
            result.add("")
        }

        return result
    }

}

class Day21 : AdventSolution<Long>() {
    fun shortestSequenceLength(
        from: Char,
        to: Char,
        layer: Int,
        maxLayer: Int,
        mem: MutableMap<Triple<Char, Char, Int>, Long>
    ): Long {
        if (layer == maxLayer) {
            return 1L
        }
        val key = Triple(from, to, layer)
        if (mem.containsKey(key)) {
            return mem.getValue(key)
        }
        val panel = if (layer == 0) MainPanel() else SecondaryPanel()
        val sequences = panel.shortestSequencesTo(from, to)
        val res = sequences.minOf { shortestSequenceLength(it, layer + 1, maxLayer, mem) }
        mem[key] = res
        return res
    }

    fun shortestSequenceLength(input: String, layer: Int, maxLayer: Int, mem: MutableMap<Triple<Char, Char, Int>, Long>): Long {
        return ("A" + input + "A").windowedSequence(2).sumOf { shortestSequenceLength(it[0], it[1], layer, maxLayer, mem) }
    }

    override fun part1(input: List<String>): Long {
        val mem = mutableMapOf<Triple<Char, Char, Int>, Long>()
        val res = input.map { it.dropLast(1) }.map { it to shortestSequenceLength(it, 0, 3, mem) }
        return res.map { it.first.toInt() * it.second }.sum()
    }

    override fun part2(input: List<String>): Long {
        val mem = mutableMapOf<Triple<Char, Char, Int>, Long>()
        val res = input.map { it.dropLast(1) }.map { it to shortestSequenceLength(it, 0, 26, mem) }
        return res.map { it.first.toInt() * it.second }.sum()
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 21, Day21()).run()
}