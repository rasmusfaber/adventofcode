package xyz.faber.adventofcode.year2020.day16

import com.marcinmoskala.math.product
import xyz.faber.adventofcode.util.getInput

class Day16 {
    val input = getInput(2020, 16)

    /*val input = "class: 1-3 or 5-7\n" +
            "row: 6-11 or 33-44\n" +
            "seat: 13-40 or 45-50\n" +
            "\n" +
            "your ticket:\n" +
            "7,1,14\n" +
            "\n" +
            "nearby tickets:\n" +
            "7,3,47\n" +
            "40,4,50\n" +
            "55,2,20\n" +
            "38,6,12"*/
    /*val input = "class: 0-1 or 4-19\n" +
            "row: 0-5 or 8-19\n" +
            "seat: 0-13 or 16-19\n" +
            "\n" +
            "your ticket:\n" +
            "11,12,13\n" +
            "\n" +
            "nearby tickets:\n" +
            "3,9,18\n" +
            "15,1,5\n" +
            "5,14,9"*/
    val parts = input.split("\n\n")
    val regex = "(.*): (\\d*)-(\\d*) or (\\d*)-(\\d*)".toRegex()
    val rules = parts[0].lines().filter { it.isNotBlank() }.map { regex.matchEntire(it)!!.destructured }.map { (n, f1, t1, f2, t2) -> Rule(n, f1.toInt(), t1.toInt(), f2.toInt(), t2.toInt()) }
    val yourTicket = parts[1].lines().filter { it.isNotBlank() }[1].split(",").map { it.toInt() }
    val otherTickets = parts[2].lines().filter { it.isNotBlank() }.let { it.subList(1, it.size) }.map { it.split(",").map { it.toInt() } }

    fun part1() {
        val res = otherTickets.flatMap { it }.filter { rules.all { r -> !r.match(it) } }.sum()
        println(res) // not 108014
    }


    fun part2() {
        val validTickets = otherTickets.filter { ticket -> !ticket.any { rules.all { r -> !r.match(it) } } }
        val rulesByValid = rules.map { rule -> rule to yourTicket.indices.filter { i -> validTickets.all { rule.match(it[i]) } } }.sortedBy { it.second.size }
        val used = mutableSetOf<Int>()
        val indexAndRules = mutableMapOf<Int, Rule>()
        for ((rule, possibleIndices) in rulesByValid) {
            val index = possibleIndices.first { it !in used }
            used += index
            indexAndRules[index] = rule
        }
        val values = indexAndRules.entries.filter { it.value.name.startsWith("departure") }
                .map { yourTicket[it.key] }
        println(values)
        println(values.product()) // not 42180533641
    }

}

class Rule(val name: String, val from1: Int, val to1: Int, val from2: Int, val to2: Int) {
    fun match(i: Int): Boolean {
        return i in from1..to1 || i in from2..to2
    }
}

fun main(args: Array<String>) {
    val d = Day16()
    d.part1()
    d.part2()
}
