package xyz.faber.adventofcode.year2020.day13

import xyz.faber.adventofcode.util.gcdExtended
import xyz.faber.adventofcode.util.getInputFromLines
import java.math.BigInteger

class Day13 {
    val input = getInputFromLines(2020, 13)
    /*val input = ("939\n" +
            "7,13,x,x,59,x,31,19").lines()*/

    fun part1() {
        val earliest = input[0].toInt()
        val times = input[1].split(",").filter { it != "x" }.map { it.toInt() }

        val earliestDepartures = times.map { it to ((earliest - 1) / it) * it + it }
        val earliestBus = earliestDepartures.minByOrNull { it.second }!!
        println(earliestBus.first * (earliestBus.second - earliest))
    }

   fun part2() {
        val times = input[1].split(",").map { if (it != "x") it.toLong() else null }
        val constraints = times.withIndex().filter { it.value != null }

        var a = BigInteger.valueOf(-constraints[0].index.toLong())
        var n = BigInteger.valueOf(constraints[0].value!!.toLong())
        for (i in 1 until constraints.size) {
            val ax = BigInteger.valueOf(-constraints[i].index.toLong())
            val nx = BigInteger.valueOf(constraints[i].value!!.toLong())
            val (_, m, mx) = gcdExtended(n, nx)
            a = a * nx * mx + ax * n * m
            n = n * nx
            a = a % n
            a = (a+n) % n
        }
        println(a) // not 639554824907463
    }

}

fun main(args: Array<String>) {
    val d = Day13()
    d.part1()
    d.part2()
}
