package xyz.faber.adventofcode.util

import kotlin.math.abs
import kotlin.math.sign

data class XYZMap<T>(val dimx: Int, val dimy: Int, val dimz: Int, val empty: () -> T) {
    val minx = 0
    val maxx = dimx - 1
    val miny = 0
    val maxy = dimy - 1
    val minz = 0
    val maxz = dimz - 1
    val map = MutableList<T>(dimx * dimy * dimz) { empty() }

    operator fun get(x: Int, y: Int, z: Int): T {
        if (x < minx || x > maxx || y < miny || y > maxy || z < minz || z > maxz) return empty()
        return map[(x - minx) + (y - miny) * dimx + (z - minz) * dimx * dimy]
    }


    operator fun get(p: Pos3D): T {
        return this[p.x, p.y, p.z]
    }

    operator fun set(x: Int, y: Int, z: Int, value: T) {
        if (x < minx || x > maxx || y < miny || y > maxy || z < minz || z > maxz) throw ArrayIndexOutOfBoundsException()
        map[(x - minx) + (y - miny) * dimx + (z - minz) * dimx * dimy] = value
    }

    operator fun set(p: Pos3D, value: T) {
        this[p.x, p.y, p.z] = value
    }

    fun print() {
        this.print { it.toString() }
    }

    fun print(p: (T) -> String) {
        for (z in minz..maxz) {
            for (y in miny..maxy) {
                for (x in minx..maxx) {
                    print(p(this[x, y, z]))
                }
                println()
            }
            println("------------")
        }
    }
}


data class Pos3D(val x: Int, val y: Int, val z: Int) {
    operator fun plus(p: Pos3D): Pos3D {
        return Pos3D(x + p.x, y + p.y, z + p.z)
    }

    operator fun minus(p: Pos3D): Pos3D {
        return Pos3D(x - p.x, y - p.y, z - p.z)
    }

    val sign: Pos3D get()=Pos3D(x.sign, y.sign, z.sign)

    override fun toString(): String {
        return "($x, $y, $z)"
    }

    fun transform(t: Transform3D): Pos3D {
        return t.funcs.fold (this) { acc, f -> f(acc) }
    }
}

class Transform3D(val funcs: List<(Pos3D)->Pos3D>){
    constructor(func: (Pos3D)->Pos3D): this(listOf(func))
    constructor(vararg funcs: (Pos3D)->Pos3D): this(funcs.toList())

    operator fun plus(t: Transform3D): Transform3D {
        return Transform3D(funcs + t.funcs)
    }
}

// https://www.euclideanspace.com/maths/algebra/matrix/transforms/examples/index.htm
val rotations3D = listOf<(Pos3D)->Pos3D>(
    {Pos3D(it.x, it.y, it.z)},
    {Pos3D(it.x, -it.z, it.y)},
    {Pos3D(it.x, -it.y, -it.z)},
    {Pos3D(it.x, it.z, -it.y)},

    {Pos3D(-it.y, it.x, it.z)},
    {Pos3D(it.z, it.x, it.y)},
    {Pos3D(it.y, it.x, -it.z)},
    {Pos3D(-it.z, it.x, -it.y)},

    {Pos3D(-it.x, -it.y, it.z)},
    {Pos3D(-it.x, -it.z, -it.y)},
    {Pos3D(-it.x, it.y, -it.z)},
    {Pos3D(-it.x, it.z, it.y)},

    {Pos3D(it.y, -it.x, it.z)},
    {Pos3D(it.z, -it.x, -it.y)},
    {Pos3D(-it.y, -it.x, -it.z)},
    {Pos3D(-it.z, -it.x, it.y)},

    {Pos3D(-it.z, it.y, it.x)},
    {Pos3D(it.y, it.z, it.x)},
    {Pos3D(it.z, -it.y, it.x)},
    {Pos3D(-it.y, -it.z, it.x)},

    {Pos3D(-it.z, -it.y, -it.x)},
    {Pos3D(-it.y, it.z, -it.x)},
    {Pos3D(it.z, it.y, -it.x)},
    {Pos3D(it.y, -it.z, -it.x)}
).map{Transform3D(it)}


fun manhattanDistance(pos1: Pos3D, pos2: Pos3D): Int {
    return abs(pos1.x - pos2.x) + abs(pos1.y - pos2.y) + abs(pos1.z - pos2.z)
}