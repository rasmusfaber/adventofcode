package xyz.faber.adventofcode.year2020.day2

import xyz.faber.adventofcode.util.parseInputFromLines

class Day2 {
    val input = parseInputFromLines(2020, 2, "(\\d*)-(\\d*) (.): (.*)")
            .map { (from, to, c, s) -> Rule(from.toInt(), to.toInt(), c[0], s) }

    data class Rule(val from: Int, val to: Int, val c: Char, val s: String)

    fun part1() {
        val res = input.filter { (from, to, c, s) -> (s.toCharArray().count { it == c }) in from..to }.count()
        println(res)
    }


    fun part2() {
        val res = input.filter { (from, to, c, s) -> (s[from - 1] == c) xor (s[to - 1] == c) }.count()
        println(res)
    }

}

fun main(args: Array<String>) {
    val d = Day2()
    d.part1()
    d.part2()
}
