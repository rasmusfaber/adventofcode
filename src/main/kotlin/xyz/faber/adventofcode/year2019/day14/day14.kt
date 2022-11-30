package xyz.faber.adventofcode.year2019.day14

import xyz.faber.adventofcode.util.findMax
import xyz.faber.adventofcode.util.getInputFromLines

class Day14 {
    val input = getInputFromLines(2019, 14)
    //val input = ("10 ORE => 10 A\n1 ORE => 1 B\n7 A, 1 B => 1 C\n7 A, 1 C => 1 D\n7 A, 1 D => 1 E\n7 A, 1 E => 1 FUEL").lines()
    val regex = "(.*) => (.*)".toRegex()
    val reactions = input.map { regex.matchEntire(it)!!.destructured }
            .map { (from, to) ->
                val froms = from.split(", ").map {
                    val x = it.split(" ")
                    val amount = x[0].toLong()
                    val type = x[1]
                    Part(amount, type)
                }
                val t = to.split(" ").let {
                    val amount = it[0].toLong()
                    val type = it[1]
                    Part(amount, type)
                }
                Reaction(froms, t)
            }
    val byResult = reactions.map {
        it.output.element to it
    }.toMap()

    fun part1(): Long {
        return needed(1L)
    }

    private fun needed(fuelAmount: Long): Long {
        val required = mutableMapOf("FUEL" to fuelAmount)
        while (required.any { it.value > 0 && it.key != "ORE" }) {
            val need = required.entries.first { it.value > 0 && it.key != "ORE" }
            val from = byResult[need.key]!!
            val multiple = Math.ceil(1.0 * need.value / from.output.count).toLong()
            for (part in from.input) {
                val req = required[part.element] ?: 0
                required[part.element] = req + part.count * multiple
            }
            required[need.key] = required[need.key]!! - from.output.count * multiple
        }
        return required["ORE"]!!
    }

    data class Part(val count: Long, val element: String)
    data class Reaction(val input: List<Part>, val output: Part)

    fun part2(): Long {
        val target = 1000000000000L
        var guess = target / part1()

        return findMax({ needed(it) <= target }, guess)
    }

}

fun main(args: Array<String>) {
    val d = Day14()
    println(d.part1())
    println(d.part2())
}
