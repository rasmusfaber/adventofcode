package xyz.faber.adventofcode.year2021.day9

import com.marcinmoskala.math.product
import xyz.faber.adventofcode.util.Pos
import xyz.faber.adventofcode.util.XYMap
import xyz.faber.adventofcode.util.getInputFromLines
import xyz.faber.adventofcode.util.toIntXYMap

class Day9 {
    val input = getInputFromLines(2021, 9)

    fun part1(input: List<String>): Int {
        val map = input.toIntXYMap(default = 99)
        val res = map.filter {
            it.pos.adjacentNonDiagonal()
                .all { p2 -> map[p2] > it.value }
        }
            .sumBy { it.value + 1 }
        return res
    }


    fun part2(input: List<String>): Long {
        val map = input.toIntXYMap(default = 99)
        val lowPoints = map.filter {
            it.pos.adjacentNonDiagonal()
                .all { p2 -> map[p2] > it.value }
        }
            .map { it.pos }.toSet()

        val basinIds = mutableMapOf<Pos, Int>()
        for ((i, p) in lowPoints.withIndex()) {
            fill(map, p, basinIds, i)
        }

        val basins = basinIds.entries.groupBy { it.value }
            .map { it.value.size }
            .sortedDescending()

        return basins.take(3).product()
    }

    private fun fill(map: XYMap<Int>, p: Pos, basinIds: MutableMap<Pos, Int>, i: Int) {
        if (p in basinIds) return
        if (map[p] == 9) return
        if (!map.isInBounds(p)) return
        basinIds[p] = i
        p.adjacentNonDiagonal().forEach {
            fill(map, it, basinIds, i)
        }
    }
}

fun main(args: Array<String>) {
    val d = Day9()

    println(d.part1(d.input))
    println(d.part2(d.input))
}
