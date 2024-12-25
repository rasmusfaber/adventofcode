package xyz.faber.adventofcode.year2024.day15

import org.hexworks.zircon.api.data.Position
import xyz.faber.adventofcode.util.*
import java.util.*

class Day15 : AdventSolution<Int>() {
    fun step(pos: Pos, boxes: Set<Pos>, walls: Set<Pos>, dir: Direction): Pair<Pos, Set<Pos>> {
        val newPos = pos + dir
        if (newPos in walls) {
            return pos to boxes
        }
        if (newPos !in boxes){
            return newPos to boxes
        }
        var c = newPos
        while (c in boxes) {
            c += dir
            if(c in walls) {
                return pos to boxes
            }
        }
        val newBoxes = boxes.toMutableSet()
        newBoxes -= newPos
        newBoxes += c
        return newPos to newBoxes
    }

    private fun print(
        c: Char,
        map: CharXYMap,
        pos: Pos,
        boxes: Set<Pos>,
        walls: Set<Pos>
    ) {
        println(c)
        map.print { p -> if (p == pos) "@" else if (p in boxes) "O" else if (p in walls) "#" else "." }
        println()
    }

    override fun part1(input: List<String>): Int {
        val split = input.split(listOf(""))
        val map = split[0].toXYMap()
        val instructions = split[1].joinToString("")
        val initialPos = map.filter { it.value == '@' }.first().pos
        val initialBoxes = map.filter { it.value == 'O' }.map { it.pos }
        val walls = map.filter { it.value == '#' }.map { it.pos }.toSet()
        var pos = initialPos
        var boxes = initialBoxes.toSet()
        for (c in instructions) {
            var r = step(pos, boxes, walls, c.toDirection())
            pos = r.first
            boxes = r.second
        }
        return boxes.sumOf{it.y * 100 + it.x}
    }

    fun step2(pos: Pos, boxes: Set<Pos>, walls: Set<Pos>, dir: Direction): Pair<Pos, Set<Pos>> {
        val newPos = pos + dir
        if (newPos in walls) {
            return pos to boxes
        }
        if (newPos !in boxes && newPos + Direction.W !in boxes){
            return newPos to boxes
        }
        val newBoxes = boxes.toMutableSet()
        val pushQueue = LinkedList<Pos>()
        if (newPos in boxes){
            pushQueue.add(newPos)
            newBoxes -= newPos
        }
        if (newPos + Direction.W in boxes){
            pushQueue.add(newPos + Direction.W)
            newBoxes -= newPos + Direction.W
        }
        while (pushQueue.isNotEmpty()){
            val c = pushQueue.pop()
            val next = c + dir
            if (next in walls || next + Direction.E in walls){
                return pos to boxes
            }
            if (next in newBoxes){
                pushQueue.add(next)
            }
            if (next + Direction.E in newBoxes){
                newBoxes-=next + Direction.E
                pushQueue.add(next + Direction.E)
            }
            if (next + Direction.W in newBoxes){
                newBoxes-=next + Direction.W
                pushQueue.add(next + Direction.W)
            }
            newBoxes+=next
        }
        return newPos to newBoxes
    }

    private fun print2(
        c: Char,
        map: CharXYMap,
        pos: Pos,
        boxes: Set<Pos>,
        walls: Set<Pos>
    ) {
        println(c)
        map.print { p -> if (p == pos) "@" else if (p in boxes) "[" else if (p + Direction.W in boxes) "]" else if (p in walls) "#" else "." }
        println()
    }

    override fun part2(input: List<String>): Int {
        val split = input.split(listOf(""))
        val baseMap = split[0]
        val expandedMap = baseMap.map{it.replace("#", "##").replace("O", "[]").replace(".", "..").replace("@", "@.")}
        val map = expandedMap.toXYMap()
        val instructions = split[1].joinToString("")
        val initialPos = map.filter { it.value == '@' }.first().pos
        val initialBoxes = map.filter { it.value == '[' }.map { it.pos }
        val walls = map.filter { it.value == '#' }.map { it.pos }.toSet()
        var pos = initialPos
        var boxes = initialBoxes.toSet()
        for (c in instructions) {
            var r = step2(pos, boxes, walls, c.toDirection())
            pos = r.first
            boxes = r.second
        }

        return boxes.sumOf{it.y * 100 + it.x}
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 15, Day15()).run()
}