package xyz.faber.adventofcode.year2022.day15

import kotlinx.coroutines.yield
import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.Pos
import xyz.faber.adventofcode.util.manhattanDistance
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day15 : AdventSolution<Long>() {
    val regex = "Sensor at x=(.*), y=(.*): closest beacon is at x=(.*), y=(.*)".toRegex()
    fun parse(input: List<String>): List<Pair<Pos, Pos>> = input.map { regex.find(it)!!.destructured }
        .map { (x1, y1, x2, y2) -> Pos(x1.toInt(), y1.toInt()) to Pos(x2.toInt(), y2.toInt()) }

    override fun part1(input: List<String>): Long {
        val sensorsAndBeacons = parse(input)
        val beacons = sensorsAndBeacons.map { it.second }.toSet()
        val targetY = 2000000
        val coveredPositions = mutableSetOf<Int>()
        sensorsAndBeacons.forEach { (sensor, beacon) ->
            val maxDistance = manhattanDistance(sensor, beacon)
            val yDistance = abs(sensor.y - targetY)
            if (yDistance < maxDistance) {
                val maxXDistance = maxDistance - yDistance
                for (x in (sensor.x - maxXDistance)..(sensor.x + maxXDistance)) {
                    coveredPositions += x
                }
            }
        }
        return coveredPositions.size.toLong() - beacons.count { it.y == targetY }
    }

    override fun part2(input: List<String>): Long {
        val sensorsAndBeacons = parse(input)
        val maxDim = 4000000
        for (y in 0..maxDim) {
            val ranges = sensorsAndBeacons.map { (sensor, beacon) ->
                val maxDistance = manhattanDistance(sensor, beacon)
                val yDistance = abs(sensor.y - y)
                if (yDistance < maxDistance) {
                    val maxXDistance = maxDistance - yDistance
                    max(0, (sensor.x - maxXDistance))..min(maxDim, (sensor.x + maxXDistance))
                } else {
                    IntRange.EMPTY
                }
            }.combine()
            if (ranges.size > 1) {
                val x = ranges[0].last + 1
                return x * 4000000L + y
            }
        }
        return -1
    }
}

private fun Iterable<IntRange>.combine(): List<IntRange> {
    val sorted = this.sortedBy { it.start }
    val stack = mutableListOf<IntRange>()
    for (it in sorted) {
        if (stack.isEmpty() || stack.last().endInclusive + 1 < it.start) {
            stack += it
        } else if (stack.last().endInclusive < it.endInclusive) {
            val prev = stack.removeLast()
            stack += IntRange(prev.start, it.endInclusive)
        }
    }
    return stack
}

fun main(args: Array<String>) {
    AdventRunner(2022, 15, Day15()).run()

}
