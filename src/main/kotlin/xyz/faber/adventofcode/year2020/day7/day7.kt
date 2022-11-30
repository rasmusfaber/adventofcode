package xyz.faber.adventofcode.year2020.day7

import xyz.faber.adventofcode.util.parseInputFromLines

class Day7 {
    private val input = parseInputFromLines(2020, 7, "(.*) bags contain (.*)")
            .map { (color, contains) -> Rule(color, parseContains(contains)) }
    private val rulesByParent = input.map { it.color to it }.toMap()
    private val parents = input.flatMap { it.contains.keys.map { v -> v to it } }.groupBy({ it.first }, { it.second.color })

    fun part1() {
        val visited = mutableSetOf<String>()
        val queue = mutableListOf("shiny gold")
        while (!queue.isEmpty()) {
            val bag = queue.removeAt(0)
            val parents = parents[bag] ?: emptyList()
            for (parent in parents) {
                if (parent !in visited) {
                    queue.add(parent)
                }
            }
            visited.add(bag)
        }
        println(visited.size - 1)
    }


    fun part2() {
        println(bagsIn("shiny gold") - 1)
    }

    fun bagsIn(s: String): Long {
        val rule = rulesByParent[s]!!
        return rule.contains.entries.map { it.value * bagsIn(it.key) }.sum() + 1
    }

    fun parseContains(s: String): Map<String, Int> {
        if (s == "no other bags.") {
            return emptyMap()
        }
        val regex2 = "(\\d*) (.*) bags?.?".toRegex()
        return s.split(", ").map { regex2.matchEntire(it)!!.destructured!! }.map { (n, c) -> c to n.toInt() }.toMap()
    }
}

private class Rule(val color: String, val contains: Map<String, Int>)

fun main(args: Array<String>) {
    val d = Day7()
    d.part1()
    d.part2()
}
