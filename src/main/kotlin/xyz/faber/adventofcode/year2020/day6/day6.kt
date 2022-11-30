package xyz.faber.adventofcode.year2020.day6

import xyz.faber.adventofcode.util.getInput

class Day6 {
    val input = getInput(2020, 6)
    val input2 = "abc\n" +
            "\n" +
            "a\n" +
            "b\n" +
            "c\n" +
            "\n" +
            "ab\n" +
            "ac\n" +
            "\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "\n" +
            "b"

    fun part1() {
        val res = input.split("\n\n").map { it.toCharArray().filter { it in 'a'..'z' }.distinct().count() }.sum()
        println(res)
    }


    fun part2() {
        val groups = input.split("\n\n")
        val res = groups.map {
            val n = it.count { it == '\n' } + if (it.last() != '\n') {
                1
            } else {
                0
            }
            it.toCharArray().filter { it in 'a'..'z' }.groupingBy { it }.eachCount().filter { it.value == n }.count()
        }.sum()
        println(res)
    }

    fun part2x() {
        val counts = input.split("\n\n").map {
            val l = it.split("\n").filterNot { it.isBlank() }.map { it.toCharArray().toSet() }
            ('a'..'z').count { l.all { line -> it in line } }
        }
        //counts.forEach { println(it) }
        println(counts.sum())
    }
}

fun main(args: Array<String>) {
    val d = Day6()
    d.part1()
    d.part2()
    d.part2x()
}
