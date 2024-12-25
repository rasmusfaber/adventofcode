package xyz.faber.adventofcode.year2024.day22

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Day22 : AdventSolution<Long>() {
    fun nextNumber(n: Long): Long {
        val r1 = n * 64
        val r2 = (n xor r1).mod(16777216L)
        val r3 = r2 / 32
        val r4 = (r2 xor r3).mod(16777216L)
        val r5 = r4 * 2048
        val r6 = (r4 xor r5).mod(16777216L)
        return r6
    }

    override fun part1(input: List<String>): Long {
        val initialNumbers = input.map{it.toLong()}
        val n2000 = initialNumbers.map{
            (1..2000).fold(it){acc, _ -> nextNumber(acc)}
        }
        return n2000.sum()
    }

    override fun part2(input: List<String>): Long {
        val initialNumbers = input.map{it.toLong()}
        val sequences = initialNumbers.map{
            (1..2000).scan(it) { acc, _ -> nextNumber(acc) }.map{it%10}.windowed(5).map{listOf(it[1]-it[0], it[2]-it[1], it[3]-it[2], it[4]-it[3]) to it[4]}
                .distinctBy { it.first }
                .toMap()
        }
        val results = sequences.flatMap { it.entries }
            .groupBy({it.key}, {it.value})
        val sums = results.mapValues { (_, values) -> values.sum() }

        val max = sums.maxBy { it.value }
        return max.value // 2343 too low
    }
}

fun main(args: Array<String>) {
    println(Day22().nextNumber(123L))
    AdventRunner(2024, 22, Day22()).run()
}