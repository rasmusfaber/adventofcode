package xyz.faber.adventofcode.year2020.day9

import xyz.faber.adventofcode.util.getInputFromLines

class Day9 {
    val input = getInputFromLines(2020, 9).map { it.toLong() }

    fun part1(): Long {
        for (i in 25..input.size - 1) {
            if (!test(i, input)) {
                println(input[i])
                return input[i]
            }
        }
        return -1
    }

    fun test(i: Int, input: List<Long>): Boolean {
        val v = input[i]

        for (j in i - 25..i - 1) {
            for (k in i - 25..i - 1) {
                if (j != k && input[j] + input[k] == v) {
                    return true
                }
            }
        }
        return false
    }


    fun part2(res1: Long) {
        for (i in 0..input.size) {
            var j = i
            while (j < input.size) {
                val sum = (i..j).sumOf { input[it] }
                if (sum == res1) {
                    val min = (i..j).minOf{ input[it] }
                    val max = (i..j).maxOf{ input[it] }
                    println(min+max) // 355555810
                    return
                }
                if (sum > res1) {
                    break
                }
                j++
            }
        }
    }

}

fun main(args: Array<String>) {
    val d = Day9()
    var res1 = d.part1()
    d.part2(res1)
}
