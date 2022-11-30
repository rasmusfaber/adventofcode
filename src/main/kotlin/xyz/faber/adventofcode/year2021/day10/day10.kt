package xyz.faber.adventofcode.year2021.day10

import arrow.core.Either
import arrow.core.orNull
import xyz.faber.adventofcode.util.getInputFromLines

class Day10 {
    val input = getInputFromLines(2021, 10)

    fun List<Int>.median(): Int =
        if (size % 2 == 0) {
            (this.sorted()[size / 2] + this.sorted()[size / 2 + 1]) / 2
        } else {
            this.sorted()[size / 2]
        }

    fun List<Long>.median(): Long =
        if (size % 2 == 0) {
            (this.sorted()[size / 2] + this.sorted()[size / 2 + 1]) / 2
        } else {
            this.sorted()[size / 2]
        }

    fun parse(line: String): Either<Char, List<Char>> {
        val stack = mutableListOf<Char>()
        line.forEach {
            if (it == '(') {
                stack.add(')')
            } else if (it == '[') {
                stack.add(']')
            } else if (it == '{') {
                stack.add('}')
            } else if (it == '<') {
                stack.add('>')
            } else {
                if (stack.isEmpty() || stack.last() != it) {
                    return Either.left(it)
                } else {
                    stack.removeAt(stack.lastIndex)
                }
            }
        }
        return Either.right(stack.reversed())
    }

    fun part1(input: List<String>) = input.map { parse(it) }
        .mapNotNull { it.swap().orNull() }
        .sumOf {
            when (it) {
                ')' -> 3
                ']' -> 57
                '}' -> 1197
                '>' -> 25137
                else -> 0
            } as Int
        }


    fun part2(input: List<String>) = input.map { parse(it) }
        .mapNotNull { it.orNull() }
        .map {
            it.fold<Char, Long>(0L) { acc, c ->
                acc * 5 + when (c) {
                    ')' -> 1
                    ']' -> 2
                    '}' -> 3
                    '>' -> 4
                    else -> 0
                }
            }
        }.median()
}

fun main(args: Array<String>) {
    val d = Day10()

    println(d.part1(d.input))
    println(d.part2(d.input))
}
