package xyz.faber.adventofcode.year2020.day3

import xyz.faber.adventofcode.util.Pos
import xyz.faber.adventofcode.util.getInput
import xyz.faber.adventofcode.util.toXYMap

class Day3 {
    val input = getInput(2020, 3)
    /*val input = "..##.......\n" +
            "#...#...#..\n" +
            ".#....#..#.\n" +
            "..#.#...#.#\n" +
            ".#...##..#.\n" +
            "..#.##.....\n" +
            ".#.#.#....#\n" +
            ".#........#\n" +
            "#.##...#...\n" +
            "#...##....#\n" +
            ".#..#...#.#\n"*/
    val map = input.toXYMap()

    fun part1() {
        var count = calcSlope(Pos(3, 1))
        println(count)
    }

    private fun calcSlope(slope: Pos): Long {
        var pos = Pos(0, 0)
        var count = 0
        while (pos.y < map.dimy) {
            if (map[pos] == '#') {
                count++
            }
            pos += slope
            if (pos.x >= map.dimx) {
                pos -= Pos(map.dimx, 0)
            }
        }
        return count.toLong()
    }


    fun part2() {
        val res1 = calcSlope(Pos(1, 1))
        val res2 = calcSlope(Pos(3, 1))
        val res3 = calcSlope(Pos(5, 1))
        val res4 = calcSlope(Pos(7, 1))
        val res5 = calcSlope(Pos(1, 2))
        val res = res1 * res2 * res3 * res4 * res5
        println(res)
    }

}

fun main(args: Array<String>) {
    val d = Day3()
    d.part1()
    d.part2()
}
