package xyz.faber.adventofcode.year2021.day13

import xyz.faber.adventofcode.util.*

class Day13 {
    val input = getInputFromLines(2021, 13);

    private fun fold(points: Set<Pos>, fold: Pair<String, Int>) = points.map {
        if (fold.first == "x") {
            if (it.x < fold.second) {
                it
            } else {
                Pos(2 * fold.second - it.x, it.y)
            }
        } else {
            if (it.y < fold.second) {
                it
            } else {
                Pos(it.x, 2 * fold.second - it.y)
            }
        }
    }.toSet()

    fun part1(input: List<String>): Int {
        val points = input.parse("(.*),(.*)").map { (x, y) -> Pos(x.toInt(), y.toInt()) }.toSet()
        val folds = input.parse("fold along (.)=(.*)").map { (dir, c) -> Pair(dir, c.toInt()) }

        return fold(points, folds[0]).size
    }

    fun part2(input: List<String>): String {
        val points = input.parse("(.*),(.*)").map { (x, y) -> Pos(x.toInt(), y.toInt()) }.toSet()
        val folds = input.parse("fold along (.)=(.*)").map { (dir, c) -> Pair(dir, c.toInt()) }

        var res = folds.fold(points) { acc, fold -> fold(acc, fold) }
        return res.toXYMap().detectText()
    }
}

fun main(args: Array<String>) {
    val d = Day13()

    println(d.part1(d.input))
    println(d.part2(d.input))
}
