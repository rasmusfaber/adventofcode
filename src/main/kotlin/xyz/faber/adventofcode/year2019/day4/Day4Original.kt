package xyz.faber.adventofcode.year2019.day4

import xyz.faber.adventofcode.util.getInput

fun part1Original(from: String, to: String): Int {
    return (from.toInt()..to.toInt()).count { okWithRepeat(it) }
}

fun part2Original(from: String, to: String): Int {
    return (from.toInt()..to.toInt()).count { okWithRepeatNoGroup(it) }
}

fun okWithRepeat(i: Int): Boolean {
    val digits = i.toString().toCharArray()
    var repeated = false
    for (i in 0..digits.size - 2) {
        if (digits[i] == digits[i + 1]) {
            repeated = true;
        }
        if (digits[i] > digits[i + 1]) {
            return false;
        }
    }
    return repeated;
}

fun okWithRepeatNoGroup(i: Int): Boolean {
    val digits = i.toString().toCharArray()
    var repeated = false
    for (i in 0..digits.size - 2) {
        if (digits[i] == digits[i + 1]
                && (i == 0 || digits[i] != digits[i - 1])
                && (i > digits.size - 3 || digits[i] != digits[i + 2])) {
            repeated = true
        }
        if (digits[i] > digits[i + 1]) {
            return false;
        }
    }
    return repeated;
}


fun main(args: Array<String>) {
    val input = getInput(2019, 4).split("-").map { it.trim() }

    println(part1Original(input[0], input[1]))
    println(part2Original(input[0], input[1]))
}
