package xyz.faber.adventofcode.year2024.day14

import xyz.faber.adventofcode.util.*

class Day14 : AdventSolution<Int>() {
    override fun part1(input: List<String>): Int {
        val robots = input.map { it.extractNumbers() }
        val dimx = if (input.size > 20) 101 else 11
        val dimy = if (input.size > 20) 103 else 7
        val time = 100
        val endPos = robots.map {
            val px = it[0]
            val py = it[1]
            val vx = it[2]
            val vy = it[3]
            Pos((px + vx * time).mod(dimx), (py + vy * time).mod(dimy))
        }
        return safetyFactor(endPos, dimx, dimy)
    }

    private fun safetyFactor(
        endPos: Collection<Pos>,
        dimx: Int,
        dimy: Int
    ): Int {
        val q1 = endPos.filter { (it.x < dimx / 2) && (it.y < dimy / 2) }
        val q2 = endPos.filter { (it.x > dimx / 2) && (it.y < dimy / 2) }
        val q3 = endPos.filter { (it.x < dimx / 2) && (it.y > dimy / 2) }
        val q4 = endPos.filter { (it.x > dimx / 2) && (it.y > dimy / 2) }
        return q1.size * q2.size * q3.size * q4.size
    }

    fun step(robots: List<List<Int>>, dimx: Int, dimy: Int): List<List<Int>> {
        return robots.map {
            val px = it[0]
            val py = it[1]
            val vx = it[2]
            val vy = it[3]
            listOf((px + vx).mod(dimx), (py + vy).mod(dimy), vx, vy)
        }
    }

    fun period(robots: List<List<Int>>, dimx: Int, dimy: Int): Int {
        val xperiods = robots.map { dimx / gcd(it[2], dimx) }
        val yperiods = robots.map { dimy / gcd(it[3], dimy) }
        val period = lcm(xperiods + yperiods)
        return period
    }

    override fun part2(input: List<String>): Int {
        val robots = input.map { it.extractNumbers() }
        if (input.size < 20) {
            return -1
        }
        val dimx = if (input.size > 20) 101 else 11
        val dimy = if (input.size > 20) 103 else 7
        val period = period(robots, dimx, dimy)
        var c = robots
        var t = 0
        var minEntropy = Double.MAX_VALUE
        var minEntropyTime = -1
        while (t <= period) {
            c = step(c, dimx, dimy)
            t++
            val pos = c.map { Pos(it[0], it[1]) }.toSet()
            val entropy = spacingEntropy(pos)
            //val entropy = safetyFactor(pos, dimx, dimy)
            if (entropy < minEntropy) {
                minEntropy = entropy
                minEntropyTime = t
                println("$t: $entropy")
            }
        }
        return minEntropyTime
    }

    fun part2b(input: List<String>): Int {
        val robots = input.map { it.extractNumbers() }
        val robotCount = robots.size
        if (input.size < 20) {
            return -1
        }
        val dimx = if (input.size > 20) 101 else 11
        val dimy = if (input.size > 20) 103 else 7
        val period = period(robots, dimx, dimy)
        println(period)
        for (axis in 0 until dimx) {
            var c = robots
            var t = 0
            while (t <= period) {
                c = step(c, dimx, dimy)
                t++
                val pos = c.map { it[0] to it[1] }.toSet()
                val symCount = pos.count { ((2 * axis - it.first) to it.second) in pos }
                if (symCount >= robotCount / 2) {
                    printRobots(pos, dimx, dimy)
                    return t
                }
            }
        }
        return -1
    }

    private fun printRobots(
        pos: Collection<Pair<Int, Int>>,
        dimx: Int,
        dimy: Int
    ) {
        for (y in 0 until dimy) {
            for (x in 0 until dimx) {
                if ((x to y) in pos) {
                    print("#")
                } else {
                    print(" ")
                }
            }
            println()
        }
        println("-------------")
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 14, Day14()).run()
}