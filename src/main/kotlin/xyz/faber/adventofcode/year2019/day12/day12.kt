package xyz.faber.adventofcode.year2019.day12

import xyz.faber.adventofcode.util.Pos3D
import xyz.faber.adventofcode.util.commonPeriod
import xyz.faber.adventofcode.util.getInputFromLines
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sqrt

class Day12 {
    val regex = "<x=(-?\\d*), y=(-?\\d*), z=(-?\\d*)>".toRegex()
    //val input = ("<x=-1, y=0, z=2>\n<x=2, y=-10, z=-7>\n<x=4, y=-8, z=8>\n<x=3, y=5, z=-1>").lines()
    //val input = ("<x=-3, y=0, z=0>\n<x=-2, y=0, z=0>\n<x=2, y=0, z=0>\n<x=3, y=0, z=0>").lines()
    val input = getInputFromLines(2019, 12)
    val parsed = input
            .map {
                regex
                        .matchEntire(it)!!
                        .destructured
                        .let { (x, y, z) -> Pos3D(x.toInt(), y.toInt(), z.toInt()) }
            }

    fun part1(): Int {
        val moons = parsed.map { Moon(it) }
        //moons.forEach { println("${it.pos} ${it.vel}") }
        for (step in 1..1000) {
            moons.forEach { it.acc = calculateAcceleration(it, moons) }
            moons.forEach { it.step() }
            /*
                println(step)
                moons.forEach { println("${it.pos} ${it.vel} ${it.acc} ${it.energy()}") }
                println("---")
            */
        }
        return moons.sumBy { it.energy() }
    }

    fun calculateAcceleration(moon: Moon, moons: List<Moon>): Pos3D {
        return moons.map { (it.pos - moon.pos).sign }.reduce { x, y -> x + y }
    }

    fun part2(): Long {
        val moons = parsed.map { Moon(it) }
        val xhistory = mutableMapOf<List<Pair<Int, Int>>, Long>()
        val yhistory = mutableMapOf<List<Pair<Int, Int>>, Long>()
        val zhistory = mutableMapOf<List<Pair<Int, Int>>, Long>()
        var step = 0L
        xhistory[moons.map { it.pos.x to it.vel.x }] = step
        yhistory[moons.map { it.pos.y to it.vel.y }] = step
        zhistory[moons.map { it.pos.z to it.vel.z }] = step
        var xperiodfound = false
        var xperiod = -1L
        var xfirst = -1L
        var yperiodfound = false
        var yperiod = -1L
        var yfirst = -1L
        var zperiodfound = false
        var zperiod = -1L
        var zfirst = -1L
        while (!xperiodfound || !yperiodfound || !zperiodfound) {
            moons.forEach { it.acc = calculateAcceleration(it, moons) }
            moons.forEach { it.step() }
            step++
            val xseen = xhistory.put(moons.map { Pair(it.pos.x, it.vel.x) }, step)
            val yseen = yhistory.put(moons.map { Pair(it.pos.y, it.vel.y) }, step)
            val zseen = zhistory.put(moons.map { Pair(it.pos.z, it.vel.z) }, step)
            if (xseen != null && !xperiodfound) {
                xperiodfound = true
                xperiod = step - xseen
                xfirst = xseen
                println("xperiod $xperiod $xfirst")
            }
            if (yseen != null && !yperiodfound) {
                yperiodfound = true
                yperiod = step - yseen
                yfirst = yseen
                println("yperiod $yperiod $yfirst")
            }
            if (zseen != null && !zperiodfound) {
                zperiodfound = true
                zperiod = step - zseen
                zfirst = zseen
                println("zperiod $zperiod $zfirst")
            }
        }
        val commonPeriod = commonPeriod(xperiod to xfirst, yperiod to yfirst, zperiod to zfirst)
        return commonPeriod.first + commonPeriod.second
    }


    fun part2x(): Long {
        val moons = parsed.map { Moon(it) }
        val history = mutableSetOf<List<Pair<Pos3D, Pos3D>>>()
        history.add(moons.map { Pair(it.pos, it.vel) })
        var steps = 0L
        while (true) {
            moons.forEach { it.acc = calculateAcceleration(it, moons) }
            val step = getNextTimestep(moons)
            moons.forEach { it.step(step) }
            val energy = moons.sumBy { it.energy() }
            steps += step
            if (energy < 1000) {
                val seen = !history.add(moons.map { Pair(it.pos, it.vel) })
                if (seen) {
                    println("$steps $step")
                    moons.forEach { println("${it.pos} ${it.vel} ${it.acc}") }
                    // 461182102 too low
                    return steps
                }
            }
            if (step > 100) {
                println("$steps $step ${history.size}")
            }
            //moons.forEach { println("${it.pos} ${it.vel} ${it.acc}") }

        }
    }

    private fun getNextTimestep(moons: List<Moon>): Int {
        return moons.map { nextTimestep(it, moons) }.minOrNull()!!
    }

    private fun nextTimestep(moon: Moon, moons: List<Moon>): Int {
        return moons.filter { moon != it }.map { nextTimestep(moon, it) }.minOrNull()!!
    }

    private fun nextTimestep(moon: Moon, moon2: Moon): Int {
        val deltaPos = moon2.pos - moon.pos
        val deltaVel = moon2.vel - moon.vel
        val deltaAcc = moon2.acc - moon.acc

        val minOf = minOf(nextTimestep(deltaPos.x, deltaVel.x, deltaAcc.x),
                nextTimestep(deltaPos.y, deltaVel.y, deltaAcc.y),
                nextTimestep(deltaPos.z, deltaVel.z, deltaAcc.z))
        return minOf
    }

    fun nextTimestep(pos: Int, vel: Int, acc: Int): Int {
        if (abs(pos) <= 100 && abs(vel) <= 100 && abs(acc) <= 100) {
            val index = (pos + 100) + (vel + 100) * 201 + (acc + 100) * 201 * 201
            return table[index]
        } else {
            return nextTimestepImpl(pos, vel, acc)
        }
    }

    val table: IntArray = {
        val t = IntArray(201 * 201 * 201)
        for (p in -100..100) {
            for (v in -100..100) {
                for (a in -100..100) {
                    val index = (p + 100) + (v + 100) * 201 + (a + 100) * 201 * 201
                    t[index] = nextTimestepImpl(p, v, a)
                }
            }
        }
        t
    }()

    //val nextTimestepMemoed = ::nextTimestepImpl.memoize()

    private fun nextTimestepImpl(pos: Int, vel: Int, acc: Int): Int {
        if (pos == 0) {
            return 1
        }
        if (acc == 0) {
            val r = ceil(pos.toDouble() / vel).toInt()
            if (r <= 0) {
                return Int.MAX_VALUE
            }
            return r
        }
        val a = acc / 2.0
        val b = vel + acc / 2.0
        val c = pos
        val d = b * b - 4 * a * c
        if (d < 0) return Int.MAX_VALUE
        val r1 = if (b < 0.0) (-b + sqrt(d)) / (2 * a) else (-b - sqrt(d)) / (2 * a)
        val r2 = c / (a * r1)
        if (r1 <= 0 && r2 <= 0) {
            return Int.MAX_VALUE
        }
        if (r1 <= 0 && r2 > 0) {
            return ceil(r2).toInt()
        }
        if (r2 <= 0 && r1 > 0) {
            return ceil(r1).toInt()
        }
        if (r1 < r2) {
            return ceil(r1).toInt()
        }
        return ceil(r2).toInt()
    }

}

data class Moon(var pos: Pos3D) {
    var vel: Pos3D = Pos3D(0, 0, 0)
    var acc: Pos3D = Pos3D(0, 0, 0)

    fun step() {
        vel += acc
        pos += vel
    }

    fun step(step: Int) {
        pos += Pos3D(((vel.x + acc.x / 2.0) * step + acc.x / 2.0 * step * step).toInt(),
                ((vel.y + acc.y / 2.0) * step + acc.y / 2.0 * step * step).toInt(),
                ((vel.z + acc.z / 2.0) * step + acc.z / 2.0 * step * step).toInt())
        vel += Pos3D(acc.x * step, acc.y * step, acc.z * step)
    }


    fun energy(): Int = (abs(pos.x) + abs(pos.y) + abs(pos.z)) * (abs(vel.x) + abs(vel.y) + abs(vel.z))

}

fun main(args: Array<String>) {
    val d = Day12()
    println(d.part1())
    println("--------------")
    println(d.part2())
}
