package xyz.faber.adventofcode.year2019.day16

import xyz.faber.adventofcode.util.getInput
import kotlin.math.abs
import kotlin.system.measureTimeMillis

class Day16 {
    val inputText = getInput(2019, 16)
    //val inputText = "80871224585914546619083218645595"
    val start = inputText.toCharArray().map { it.toString() }.filter { !it.isBlank() }.map { it.toInt() }.toList()

    fun part1() {
        //println(start)
        var input = start
        var output = input.toMutableList()
        for (time in 1..100) {
            for (i in 1..input.size) {
                val pattern = pattern(i)
                var res = 0
                val pit = pattern.iterator()
                pit.next()
                for (j in 1..input.size) {
                    res += input[j - 1] * pit.next()
                }
                res = abs(res) % 10

                output[i - 1] = res % 10
            }
            input = output
        }
        println(output.subList(0, 8).joinToString(""))
    }

    fun pattern(i: Int): Sequence<Int> = sequence {

        while (true) {
            for (i in 1..i) {
                yield(0)
            }
            for (i in 1..i) {
                yield(1)
            }
            for (i in 1..i) {
                yield(0)
            }
            for (i in 1..i) {
                yield(-1)
            }
        }
    }

    fun part2() {
        val startSize = start.size
        val fullSize = start.size * 10000
        //println("start: ${start.findCycle()}")
        val offset = inputText.substring(0, 7).toInt()
        var input = (offset until fullSize).map { start[it % startSize] }
        var output = input.toMutableList()
        for (iteration in 1..100) {
            //println(iteration)
            var total = 0
            for (i in output.size - 1 downTo 0) {
                total += input[i]
                output[i] = abs(total) % 10
            }
            input = output
            output = input.toMutableList()
            //println("$iteration : ${output.findCycle()}")
                //println(output.subList(0, min(startSize * 10, output.size)).map { it.toString() }.joinToString(""))
                //println(output.map { it.toString() }.joinToString(""))
                //println("start: ${start.findCycle()}")
        }
        // 80722126
        println(output.map { it.toString() }.joinToString(""))
        println(output.subList(0, 8).map { it.toString() }.joinToString(""))
    }
}

fun main(args: Array<String>) {
    val d = Day16()
    val part1Millis = measureTimeMillis {
        d.part1()
    }
    println("Part 1 duration ${part1Millis}ms")
    val part2Millis = measureTimeMillis {
        d.part2()
    }
    println("Part 2 duration ${part2Millis}ms")
}
