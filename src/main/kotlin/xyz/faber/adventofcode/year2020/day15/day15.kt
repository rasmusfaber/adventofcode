package xyz.faber.adventofcode.year2020.day15

import xyz.faber.adventofcode.util.getInputIntsFromCsv

class Day15 {
    val input = getInputIntsFromCsv(2020, 15)
    //val input = listOf(0, 3, 6)
    fun part1() {
        val lastSpoken = mutableMapOf<Int, Int>()
        for (n in input.withIndex()) {
            lastSpoken[n.value] = n.index + 1
        }
        var last = 0
        for (t in input.size + 2..2020) {
            val n = lastSpoken[last]?.let { t-1 - it } ?: 0
            lastSpoken[last] = t-1
            last = n
            //println(last)
        }
        println(last)
    }


    fun part2() {
        //val data = mutableListOf<Int>()
        val lastSpoken = mutableMapOf<Int, Int>()
        for (n in input.withIndex()) {
            lastSpoken[n.value] = n.index + 1
       //     data.add(n.value)
        }
        var last = 0
      //  data.add(0)
        for (t in input.size + 2..30000000) {
            val n = lastSpoken[last]?.let { t-1 - it } ?: 0
            lastSpoken[last] = t-1
            last = n
       //     data.add(last)
            //println(last)
        }
        //val cycle = data.subList(100000,1000000).findCycle()
        println(last)
    }

}

fun main(args: Array<String>) {
    val d = Day15()
    d.part1()
    d.part2()
}
