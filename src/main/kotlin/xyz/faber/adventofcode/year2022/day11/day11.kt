package xyz.faber.adventofcode.year2022.day11

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.lcm
import xyz.faber.adventofcode.util.split

data class Monkey(val id: Int, var items: MutableList<Long>, val operation: (Long) -> Long, val divisibleBy: Long, val nextMonkey: (Long) -> Int) {
    var inspectCount = 0L
}

fun parseMonkey(input: List<String>): Monkey {
    val id = "Monkey (.*):".toRegex().find(input[0])!!.groupValues[1].toInt()
    val items = "  Starting items: (.*)".toRegex().find(input[1])!!.groupValues[1].split(", ").map { it.toLong() }
    val (operator, operandString) = "  Operation: new = old ([+*]) (.*)".toRegex().find(input[2])!!.destructured
    val operation = if (operator == "+") {
        if (operandString == "old") {
            { it: Long -> it + it }
        } else {
            { it: Long -> it + operandString.toLong() }
        }
    } else {
        if (operandString == "old") {
            { it: Long -> it * it }
        } else {
            { it: Long -> it * operandString.toLong() }
        }
    }
    val divisibleBy = "  Test: divisible by (.*)".toRegex().find(input[3])!!.groupValues[1].toLong()
    val ifTrue = "    If true: throw to monkey (.*)".toRegex().find(input[4])!!.groupValues[1].toInt()
    val ifFalse = "    If false: throw to monkey (.*)".toRegex().find(input[5])!!.groupValues[1].toInt()
    val nextMonkey = { it: Long -> if ((it % divisibleBy) == 0L) ifTrue else ifFalse }
    return Monkey(id, items.toMutableList(), operation, divisibleBy, nextMonkey)

}

class Day11 : AdventSolution<Long>() {
    override fun part1(input: List<String>): Long {
        val monkeys = input.split(listOf("")).map{ parseMonkey(it) }
        for(round in 1..20){
            for (monkey in monkeys) {
                monkey.inspectCount += monkey.items.size
                for (item in monkey.items) {
                    val newLevel = monkey.operation(item) / 3
                    val nextMonkeyIndex = monkey.nextMonkey(newLevel)
                    val nextMonkey = monkeys[nextMonkeyIndex]
                    nextMonkey.items += newLevel
                }
                monkey.items.clear()
            }
        }
        val topMonkeys = monkeys.sortedByDescending { it.inspectCount }.take(2)
        return topMonkeys[0].inspectCount*topMonkeys[1].inspectCount
    }

    override fun part2(input: List<String>): Long {
        val monkeys = input.split(listOf("")).map{ parseMonkey(it) }
        val lcm = monkeys.map{it.divisibleBy}.reduce { a, b -> lcm(a, b) }
        for(round in 1..10000){
            for (monkey in monkeys) {
                monkey.inspectCount += monkey.items.size
                for (item in monkey.items) {
                    val newLevel = monkey.operation(item) % lcm
                    val nextMonkeyIndex = monkey.nextMonkey(newLevel)
                    val nextMonkey = monkeys[nextMonkeyIndex]
                    nextMonkey.items += newLevel
                }
                monkey.items.clear()
            }
        }
        val topMonkeys = monkeys.sortedByDescending { it.inspectCount }.take(2)
        return topMonkeys[0].inspectCount*topMonkeys[1].inspectCount
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 11, Day11()).run()

}
