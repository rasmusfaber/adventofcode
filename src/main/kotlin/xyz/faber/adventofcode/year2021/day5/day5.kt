package xyz.faber.adventofcode.year2021.day5

import xyz.faber.adventofcode.util.Pos
import xyz.faber.adventofcode.util.XYMap
import xyz.faber.adventofcode.util.getInputFromLines
import xyz.faber.adventofcode.util.parseInputFromLines
import kotlin.math.sign

class Day5 {
    val input1 = getInputFromLines(2021, 5)

    val input = parseInputFromLines(2021, 5, "(\\d*),(\\d*) -> (\\d*),(\\d*)")
        .map { (x1, y1, x2, y2) -> Pos(x1.toInt(), y1.toInt()) to Pos(x2.toInt(), y2.toInt()) }

    fun part1a(input: List<Pair<Pos, Pos>>): Int {
        val map = XYMap<Int>(1000, 1000, 0)
        input
            .filter { (from, to) -> from.x == to.x }
            .map { (from, to) -> if (from.y < to.y) from to to else to to from }
            .forEach { (from, to) ->
                (from.y..to.y).forEach { y -> map[from.x, y]++ }
            }
        input
            .filter { (from, to) -> from.y == to.y }
            .map { (from, to) -> if (from.x < to.x) from to to else to to from }
            .forEach { (from, to) ->
                (from.x..to.x).forEach { x -> map[x, from.y]++ }
            }
        return map.count { it.value >= 2 }
    }

    fun part2a(input: List<Pair<Pos, Pos>>): Int {
        val map = XYMap<Int>(1000, 1000, 0)
        input
            .filter { (from, to) -> from.x == to.x }
            .map { (from, to) -> if (from.y < to.y) from to to else to to from }
            .forEach { (from, to) ->
                (from.y..to.y).forEach { y -> map[from.x, y]++ }
            }
        input
            .filter { (from, to) -> from.y == to.y }
            .map { (from, to) -> if (from.x < to.x) from to to else to to from }
            .forEach { (from, to) ->
                (from.x..to.x).forEach { x -> map[x, from.y]++ }
            }
        input
            .filter { (from, to) -> from.x != to.x && from.y != to.y }
            .map { (from, to) -> if (from.x < to.x) from to to else to to from }
            .filter { (from, to) -> from.y < to.y }
            .forEach { (from, to) ->
                (from.x..to.x).forEach { x -> map[x, from.y + x - from.x]++ }
            }
        input
            .filter { (from, to) -> from.x != to.x && from.y != to.y }
            .map { (from, to) -> if (from.x < to.x) from to to else to to from }
            .filter { (from, to) -> from.y > to.y }
            .forEach { (from, to) ->
                (from.x..to.x).forEach { x -> map[x, from.y - x + from.x]++ }
            }
        return map.count { it.value >= 2 }
    }

    fun linePoints(from: Pos, to: Pos): List<Pos> {
        val dx = (to.x - from.x).sign
        val dy = (to.y - from.y).sign
        return generateSequence(from) { Pos(it.x + dx, it.y + dy) }
            .takeWhile { it != to }.plus(to).toList()
    }

    fun part1(input: List<String>): Int {
        val regex = "(\\d*),(\\d*) -> (\\d*),(\\d*)".toRegex()
        return input.map { regex.matchEntire(it)!!.destructured }
            .map { (x1, y1, x2, y2) -> Pos(x1.toInt(), y1.toInt()) to Pos(x2.toInt(), y2.toInt()) }
            .filter { (from, to) -> from.x == to.x || from.y == to.y }
            .map { (from, to) -> linePoints(from, to) }
            .flatten()
            .groupingBy { it }
            .eachCount()
            .count { (_, v) -> v >= 2 }
    }

    fun part2(input: List<String>): Int {
        val regex = "(\\d*),(\\d*) -> (\\d*),(\\d*)".toRegex()
        return input.map { regex.matchEntire(it)!!.destructured }
            .map { (x1, y1, x2, y2) -> Pos(x1.toInt(), y1.toInt()) to Pos(x2.toInt(), y2.toInt()) }
            .map { (from, to) -> linePoints(from, to) }
            .flatten()
            .groupingBy { it }
            .eachCount()
            .count { (_, v) -> v >= 2 }
    }

}

fun main(args: Array<String>) {
    val d = Day5()
    println(d.part1a(d.input))
    println(d.part2a(d.input))
    println(d.part1(d.input1))
    println(d.part2(d.input1))
}
