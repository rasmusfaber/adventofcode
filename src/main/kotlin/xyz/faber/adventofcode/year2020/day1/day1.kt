package xyz.faber.adventofcode.year2020.day1

import xyz.faber.adventofcode.util.getInputFromLines

class Day1 {
    val input = getInputFromLines(2020, 1).map{it.toInt()}

    fun part1() {
        val set = input.toSet()
        for (i in input) {
            if(set.contains(2020-i)){
                println(i*(2020-i))
                return
            }
        }
    }


    fun part2() {
        val set = input.toSet()
        val l = input.size
        for (i in 0..l - 1) {
            for (j in i + 1..l - 1) {
                if (set.contains(2020-input[i] - input[j])) {
                    println(input[i])
                    println(input[j])
                    println(2020-input[i]-input[j])
                    println(input[i] * input[j]*(2020-input[i]-input[j]))
                    return
                }
            }
        }
    }

}

fun main(args: Array<String>) {
    val d = Day1()
    d.part1()
    d.part2()
}
