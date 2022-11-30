package xyz.faber.adventofcode.year2019.day4

import aballano.kotlinmemoization.memoize
import xyz.faber.adventofcode.util.getInput

fun part1Alternate(from: String, to: String): Int {
    return possibleNumbers(from, to, true)
}

val possibleNumbers = ::possibleNumbersImpl.memoize()

fun possibleNumbersImpl(from: String, to: String, withRepeat: Boolean): Int {
    if (from[0] > to[0]) {
        return 0
    }
    val length = from.length
    if (length == 1) {
        if (withRepeat) {
            return 0
        } else {
            return to.toInt() - from.toInt() + 1
        }
    }
    if (withRepeat) {
        var res = 0
        if (from[0] >= from[1]) {
            res += possibleNumbers(from[0].toString().repeat(length - 1), from[0] + "9".repeat(length - 2), false)
            if (from[0] != '9') {
                if ((from[0] + 1) >= from[1]) {
                    res += possibleNumbers((from[0] + 1).toString().repeat(length - 1), "9".repeat(length - 1), true)
                } else {
                    res += possibleNumbers(from.substring(1), "9".repeat(length - 1), true)
                }
            }
        } else {
            res += possibleNumbers(from.substring(1), "9".repeat(length - 1), true)
        }
        for (first in from[0] + 1 until to[0]) {
            res += possibleNumbers(first.toString().repeat(length - 1), first + "9".repeat(length - 2), false)
            if (first != '9') {
                res += possibleNumbers((first + 1).toString().repeat(length - 1), "9".repeat(length - 1), true)
            }
        }
        if (from[0] != to[0]) {
            res += possibleNumbers(to[0].toString().repeat(length - 1), to.substring(1), false)
            if (to[0] != '9') {
                res += possibleNumbers((to[0] + 1).toString().repeat(length - 1), to.substring(1), true)
            }
        }
        return res
    } else {
        var res = 0
        for (first in from[0] until to[0]) {
            res += possibleNumbers(first.toString().repeat(length - 1), "9".repeat(length - 1), false)
        }
        res += possibleNumbers(to[0].toString().repeat(length - 1), to.substring(1), false)
        return res
    }
}

fun main(args: Array<String>) {
    val input = getInput(2019, 4).split("-").map { it.trim() }

    println(part1Alternate(input[0], input[1]))
    println(part2(input[0], input[1]))
}
