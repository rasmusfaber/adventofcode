package xyz.faber.adventofcode.util

import kotlin.math.*

var ORIGIN = Pos(0, 0)

data class Pos(val x: Int, val y: Int) {
    fun adjacent(): List<Pos> {
        return listOf(
            Pos(x - 1, y - 1), Pos(x, y - 1), Pos(x + 1, y - 1),
            Pos(x - 1, y), Pos(x + 1, y),
            Pos(x - 1, y + 1), Pos(x, y + 1), Pos(x + 1, y + 1)
        )

    }

    fun adjacentNonDiagonal(): List<Pos> {
        return listOf(
            Pos(x, y - 1),
            Pos(x - 1, y), Pos(x + 1, y),
            Pos(x, y + 1)
        )

    }

    fun adjacentDiagonal(): List<Pos> {
        return listOf(
            Pos(x - 1, y - 1),
            Pos(x - 1, y + 1), Pos(x + 1, y - 1),
            Pos(x + 1, y + 1)
        )

    }

    fun atMostManhattanDistanceAway(dist: Int): List<Pos> {
        val result = mutableListOf<Pos>()
        for (i in -dist..dist) {
            for (j in -dist + abs(i)..dist - abs(i)) {
                if (!(i == 0 && j == 0)) {
                    result.add(Pos(x + i, y + j))
                }
            }
        }
        return result

    }


    operator fun plus(direction: Direction): Pos = move(direction)

    operator fun plus(pos: Pos): Pos = Pos(this.x + pos.x, this.y + pos.y)

    operator fun minus(pos: Pos): Pos = Pos(this.x - pos.x, this.y - pos.y)

    fun sign(): Pos = Pos(this.x.sign, this.y.sign)

    fun move(direction: Direction): Pos {
        return move(direction, 1)
    }

    fun move(direction: Direction, amount: Int): Pos {
        return when (direction) {
            Direction.N -> Pos(x, y - amount)
            Direction.E -> Pos(x + amount, y)
            Direction.S -> Pos(x, y + amount)
            Direction.W -> Pos(x - amount, y)
        }
    }

    fun moveMod(direction: Direction, xMod: Int, yMod: Int): Pos {
        return when (direction) {
            Direction.N -> Pos(x, (y + yMod - 1) % yMod)
            Direction.E -> Pos((x + 1) % xMod, y)
            Direction.S -> Pos(x, (y + 1) % yMod)
            Direction.W -> Pos((x + xMod - 1) % xMod, y)
        }
    }


    fun restrict(minx: Int, maxx: Int, miny: Int, maxy: Int): Pos {
        return Pos(max(minx, min(maxx, x)), max(miny, min(maxy, y)))
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}

class MutablePos(var x: Int, var y: Int) {
    constructor(pos: Pos) : this(pos.x, pos.y)

    fun move(direction: Direction) {
        move(direction, 1)
    }

    fun move(direction: Direction, amount: Int) {
        when (direction) {
            Direction.N -> y -= amount
            Direction.E -> x += amount
            Direction.S -> y += amount
            Direction.W -> x -= amount
        }
    }

    operator fun plusAssign(direction: Direction) = move(direction)

    fun toPos(): Pos = Pos(x, y)
}

fun manhattanDistance(pos1: Pos, pos2: Pos): Int {
    return abs(pos1.x - pos2.x) + abs(pos1.y - pos2.y)
}

fun distanceSquared(pos1: Pos, pos2: Pos): Int {
    return (pos1.x - pos2.x) * (pos1.x - pos2.x) + (pos1.y - pos2.y) * (pos1.y - pos2.y)
}

fun Pos.normalize(): Pos {
    if (this == ORIGIN) return ORIGIN
    val gcd = gcd(this.x, this.y)
    return Pos(x / gcd, y / gcd)
}

// (1,0) is 0 (-1, -0.000.0001) is ~ -PI going clockwise to (-1,0) which is PI
fun Pos.angle(): Double = atan2(this.y.toDouble(), this.x.toDouble())

// Same direction is 0, going clockwise up to 2*PI
fun Pos.angleWith(pos: Pos): Double = (this.angle() - pos.angle()).let { if (it >= 0) it else it + 2 * PI }

fun Pos.ray(direction: Direction) = generateSequence(this) { it.move(direction) }

fun String.toPos(): Pos {
    val (x, y) = this.split(",").map { it.trim().toInt() }
    return Pos(x, y)
}