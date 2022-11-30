package xyz.faber.adventofcode.year2019.day3

import xyz.faber.adventofcode.util.*

fun part1(line1: List<String>, line2: List<String>): Int {
    val path1 = line1.map { it.toStep() }.toPath()
    val path2 = line2.map { it.toStep() }.toPath()

    val crossings = path1.toSet().intersect(path2.toSet())
            .filter { it != ORIGIN }

    val closestCrossing = crossings.minByOrNull { manhattanDistance(ORIGIN, it) }!!

    return manhattanDistance(Pos(0, 0), closestCrossing)
}

fun part2(line1: List<String>, line2: List<String>): Int {
    val path1 = line1.map { it.toStep() }.toPath()
    val path2 = line2.map { it.toStep() }.toPath()

    val crossings = path1.toSet().intersect(path2.toSet())
            .filter { it != ORIGIN }

    val closestCrossing = crossings.minByOrNull { path1.indexOf(it) + path2.indexOf(it) }!!

    return path1.indexOf(closestCrossing) + path2.indexOf(closestCrossing)
}

data class Step(val direction: Direction, val amount: Int)

fun String.toStep(): Step = Step(this.substring(0..0).toDirection(), this.substring(1).toInt())

fun List<Step>.toPath(): List<Pos> {
    var pos = ORIGIN
    return listOf(pos).plus(
            this.flatMap { (direction, amount) ->
                (1..amount)
                        .map {
                            pos += direction
                            pos
                        }
            }
    )
}

fun main(args: Array<String>) {
    val input = getInputFromLines(2019, 3)
    val line1 = input.get(0).split(",")
    val line2 = input.get(1).split(",")

    println(part1(line1, line2))
    println(part2(line1, line2))
}
