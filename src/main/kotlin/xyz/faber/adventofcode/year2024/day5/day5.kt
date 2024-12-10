package xyz.faber.adventofcode.year2024.day5

import xyz.faber.adventofcode.util.*

class Day5 : AdventSolution<Int>() {
    override fun part1(input: List<String>): Int {
        var split = input.split(listOf(""))
        var rules = split[0].map { it.split("|") }.map { it[0].toInt() to it[1].toInt() }
        var updates = split[1].map { it.split(",").map { it.toInt() } }
        val okUpdates = updates.filter { rulesOkay(it, rules) }
        val middleNumbers = okUpdates.map { it[(it.size - 1) / 2] }
        return middleNumbers.sum()
    }

    private fun rulesOkay(update: List<Int>, rules: List<Pair<Int, Int>>): Boolean {
        val indexedUpdate = update.withIndex().map { (i, v) -> v to i }.toMap()
        return rules.all { ruleOkay(indexedUpdate, it) }
    }

    private fun ruleOkay(indexedUpdate: Map<Int, Int>, rule: Pair<Int, Int>): Boolean {
        val indexa = indexedUpdate[rule.first] ?: return true
        val indexb = indexedUpdate[rule.second] ?: return true
        return indexa < indexb
    }

    override fun part2(input: List<String>): Int {
        var split = input.split(listOf(""))
        var rules = split[0].map { it.split("|") }.map { it[0].toInt() to it[1].toInt() }
        var updates = split[1].map { it.split(",").map { it.toInt() } }
        val badUpdates = updates.filter { !rulesOkay(it, rules) }
        val rulesSet = rules.toSet()
        val sortedBadUpdates = badUpdates.map {
            it.sortedWith { a, b ->
                if (rulesSet.contains(a to b)) -1 else if (rulesSet.contains(b to a)) 1 else 0
            }
        }
        return sortedBadUpdates.map { it[(it.size - 1) / 2] }.sum()
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 5, Day5()).run()

}
