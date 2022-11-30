package xyz.faber.adventofcode.year2019.day6

import xyz.faber.adventofcode.util.getInputFromLines

class Day6 {
    val input = getInputFromLines(2019, 6)
    val regex = "(.*)\\)(.*)".toRegex()
    val map = input.filter { it.isNotBlank() }.map { regex.find(it)!!.destructured!!.let { (a, b) -> b to a } }.toMap()

    fun part1(): Int {
        return map.keys.sumBy { length(it) }
    }

    fun length(a: String): Int {
        val b = map[a]
        if (b == null) {
            return 0
        } else {
            return 1 + length(b)
        }
    }

    fun part2(): Int {
        val sanAnc = ancestors("SAN")
        val sanAncSet = sanAnc.toSet()
        val youAnc = ancestors("YOU")
        val commonAnc = youAnc.first { it in sanAncSet }!!

        return sanAnc.indexOf(commonAnc) + youAnc.indexOf(commonAnc)
    }

    fun ancestors(a: String): List<String> {
        val b = map[a]
        if (b == null) {
            return listOf()
        } else {
            return listOf(b) + ancestors(b)
        }
    }
}

fun main(args: Array<String>) {
    val d = Day6()
    println(d.part1())
    println(d.part2())
}
