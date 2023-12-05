package xyz.faber.adventofcode.util

import xyz.faber.adventofcode.util.crayon.*
import java.lang.Integer.max
import java.lang.Integer.min

open class XYMap<T>(minx: Int, maxx: Int, miny: Int, maxy: Int, values: List<T>, var default: T?, val autoexpand: Boolean) : Iterable<MapEntry<T>> {
    constructor(dimx: Int, dimy: Int, values: List<T>, default: T?, autoexpand: Boolean) : this(0, dimx - 1, 0, dimy - 1, values, default, autoexpand)
    constructor(dimx: Int, dimy: Int, values: List<T>) : this(dimx, dimy, values, null, false) {
        if (dimx * dimy != values.size) {
            throw IllegalArgumentException("Dimensions and values does not match: $dimx * $dimy = ${dimx * dimy} != ${values.size}")
        }
    }

    constructor(dimx: Int, dimy: Int, values: (Pos) -> T) : this(dimx, dimy, (0 until dimy).flatMap { y -> (0 until dimx).toList().map { x -> values(Pos(x, y)) } })
    constructor(dimx: Int, dimy: Int, values: (Int, Int) -> T) : this(dimx, dimy, (0 until dimy).flatMap { y -> (0 until dimx).map { x -> values(x, y) } })
    constructor(dimx: Int, dimy: Int, default: T) : this(dimx, dimy, List(dimx * dimy) { default }, default, false)
    constructor(minx: Int, maxx: Int, miny: Int, maxy: Int, default: T) : this(minx, maxx, miny, maxy, List((maxx - minx + 1) * (maxy - miny + 1)) { default }, default, false)
    constructor(default: T) : this(-1, -1, listOf(default), default, true)

    var minx = minx
        private set
    var maxx = maxx
        private set
    var miny = miny
        private set
    var maxy = maxy
        private set
    var offsetx = minx
        private set
    var offsety = miny
        private set
    var map = values.toMutableList()

    val dimx: Int
        get() = maxx - minx + 1
    val dimy: Int
        get() = maxy - miny + 1

    operator fun get(x: Int, y: Int): T {
        if (!isInBounds(x, y)) {
            return default ?: throw ArrayIndexOutOfBoundsException()
        }
        return map[(x - offsetx) + (y - offsety) * dimx]
    }

    operator fun get(p: Pos): T {
        return get(p.x, p.y)
    }

    operator fun get(p: MapEntry<T>): T {
        return get(p.pos)
    }

    operator fun set(x: Int, y: Int, value: T) {
        if (!isInBounds(x, y)) {
            if (!autoexpand) {
                throw ArrayIndexOutOfBoundsException("Tried to set $x, $y. Valid dimensions are $minx..$maxx, $miny..$maxy")
            }
            expand(x, y)
        }
        map[(x - offsetx) + (y - offsety) * dimx] = value
    }

    operator fun set(p: Pos, value: T) {
        set(p.x, p.y, value)
    }

    fun isInBounds(x: Int, y: Int): Boolean = x in minx..maxx && y in miny..maxy

    fun isInBounds(p: Pos) = isInBounds(p.x, p.y)

    fun isOnBorder(x: Int, y: Int): Boolean = x == minx || x == maxx || y == miny || y == maxy

    fun isOnBorder(p: Pos) = isOnBorder(p.x, p.y)

    private fun expand(x: Int, y: Int) {
        if (dimx == -1 && dimy == -1) {
            minx = x
            maxx = x
            miny = y
            maxy = y
            offsetx = x
            offsety = y
            map = mutableListOf(default!!)
        }
        val newminx = min(minx, x)
        val newminy = min(miny, y)
        val newmaxx = max(maxx, x)
        val newmaxy = max(maxy, y)
        val newoffsetx = newminx
        val newoffsety = newminy

        val newmap = (newminy..newmaxy).flatMap { y -> (newminx..newmaxx).map { x -> this[x, y] } }

        this.minx = newminx
        this.miny = newminy
        this.maxx = newmaxx
        this.maxy = newmaxy
        this.offsetx = newoffsetx
        this.offsety = newoffsety
        this.map = newmap.toMutableList()
    }

    fun positions(): Collection<Pos> = (miny..maxy).flatMap { y -> (minx..maxx).map { x -> Pos(x, y) } }

    fun <S> map(transform: (T) -> S): XYMap<S> = XYMap(minx, maxx, miny, maxy, map.map(transform), if (default != null) transform(default!!) else null, autoexpand)

    override fun iterator(): Iterator<MapEntry<T>> = object : Iterator<MapEntry<T>> {
        private val inner = positions().iterator()

        override fun hasNext(): Boolean = inner.hasNext()

        override fun next(): MapEntry<T> = inner.next().let { p -> MapEntry(p, this@XYMap[p]) }
    }

    fun print() {
        print { this[it].toString() }
    }

    fun print(transform: (Pos) -> String) {
        for (y in miny..maxy) {
            for (x in minx..maxx) {
                print(transform(Pos(x, y)))
            }
            println()
        }
    }

    fun print1(transform: (T) -> Char) {
        for (y in miny..maxy) {
            for (x in minx..maxx) {
                print(transform(this[x, y]))
            }
            println()
        }
    }

    fun printChars(transform: (Pos) -> Char) = print { transform(it).toString() }

    fun printBlockIf(transform: (T) -> Boolean) {
        print { if (transform(this[it])) "\u2588".brightYellow() else "\u2591".black() }
    }

    fun printColors() {
        val distinct = map.distinct()
        val colorMapper = colorMapper(distinct, default)
        print {
            val v = this[it]
            colorMapper(
                v, if (v == default) {
                    " "
                } else {
                    "\u2588"
                }
            )
        }
    }

    fun rotateCCW() {
        val newMap = mutableListOf<T>()
        for (x in maxx downTo minx) {
            for (y in miny..maxy) {
                newMap.add(this[x, y])
            }
        }
        map = newMap

        var temp = minx
        minx = miny
        miny = temp

        temp = maxx
        maxx = maxy
        maxy = temp

        temp = offsetx
        offsetx = offsety
        offsety = temp
    }

    fun rotateCW() {
        val newMap = mutableListOf<T>()
        for (x in miny..maxy) {
            for (y in maxx downTo miny) {
                newMap.add(this[x, y])
            }
        }
        map = newMap

        var temp = minx
        minx = miny
        miny = temp

        temp = maxx
        maxx = maxy
        maxy = temp

        temp = offsetx
        offsetx = offsety
        offsety = temp
    }

    fun flipHorizontal() {
        val newMap = mutableListOf<T>()
        for (y in miny..maxy) {
            for (x in maxx downTo minx) {
                newMap.add(this[x, y])
            }
        }
        map = newMap

    }

    fun flipVertical() {
        val newMap = mutableListOf<T>()
        for (y in maxy downTo miny) {
            for (x in minx..maxx) {
                newMap.add(this[x, y])
            }
        }
        map = newMap
    }

    fun mapValues(transform: (T) -> T): XYMap<T> = XYMap(minx, maxx, miny, maxy, map.map { v -> transform(v) }, default, autoexpand)

    fun mapIndexed(transform: (Pos, T) -> T): XYMap<T> = XYMap(minx, maxx, miny, maxy, map.mapIndexed { i, v -> transform(Pos(i % dimx, i / dimx), v) }, default, autoexpand)

    fun replaceValues(transform: (T) -> T) = map.replaceAll(transform)
}

data class MapEntry<T>(val pos: Pos, val value: T)


fun <T> List<T>.toXYMap(dimx: Int, dimy: Int): XYMap<T> = XYMap(dimx, dimy, this)

fun List<String>.toXYMap(): CharXYMap {
    if (this.any { it.length != this[0].length }) {
        throw IllegalArgumentException("Lines are not same length")
    }
    return CharXYMap(this[0].length, this.size, this.joinToString("").toCharArray().toList())
}

fun <T> List<String>.toXYMap(transform: (Char) -> T, default: T): XYMap<T> {
    if (this.any { it.length != this[0].length }) {
        throw IllegalArgumentException("Lines are not same length")
    }
    return XYMap(this[0].length, this.size, this.joinToString("").toCharArray().toList().map(transform), default, false)
}

fun Collection<Pair<Int, Int>>.toXYMap(): XYMap<Boolean> {
    val maxx = this.maxByOrNull { it.first }!!.first
    val maxy = this.maxByOrNull { it.second }!!.second
    val res = XYMap(maxx + 1, maxy + 1, false)
    this.forEach { res[it.first, it.second] = true }
    return res
}

@JvmName("toXYMapPos")
fun Collection<Pos>.toXYMap(): XYMap<Boolean> {
    val minx = this.minByOrNull { it.x }!!.x
    val miny = this.minByOrNull { it.y }!!.y
    val maxx = this.maxByOrNull { it.x }!!.x
    val maxy = this.maxByOrNull { it.y }!!.y
    val res = XYMap(minx, maxx, miny, maxy, false)
    this.forEach { res[it.x, it.y] = true }
    return res
}

fun List<String>.toIntXYMap(default: Int = 0): IntXYMap {
    return toIntXYMap(default, { it.toString().toInt() })
}

fun List<String>.toIntXYMap(default: Int = 0, mapper: (Char) -> Int): IntXYMap {
    if (this.any { it.length != this[0].length }) {
        throw IllegalArgumentException("Lines are not same length")
    }
    return IntXYMap(this[0].length, this.size, this.joinToString("").toCharArray().toList().map(mapper), default, false)
}

fun String.toXYMap(): CharXYMap = this.lines().filter { it.isNotBlank() }.toXYMap()

fun <T> XYMap<T>.toMap(): Map<Pos, T> = this.positions().map { it to this[it] }.toMap()

fun <T> List<List<T>>.listsToXYMap(): XYMap<T> = this.flatten().toXYMap(this[0].size, this.size)

fun <T> Map<Pos, T>.toXYMap(default: T): XYMap<T> {
    val maxx = this.keys.maxByOrNull { it.x }!!.x
    val maxy = this.keys.maxByOrNull { it.y }!!.y

    val delta = this.getDelta()
    val res = XYMap(maxx + 1 + delta.x, maxy + 1 + delta.y, default)
    this.entries.forEach { res[it.key + delta] = it.value }
    return res
}

fun Map<Pos, *>.getDelta(): Pos {
    val minx = this.keys.maxByOrNull { it.x }!!.x
    val miny = this.keys.maxByOrNull { it.y }!!.y

    var delta: Pos
    if (minx < 0 || miny < 0 || minx > 10 || miny > 10) {
        //println("WARN: Minimums negative or large: applying delta (minx=$minx, miny=$miny)")
        delta = Pos(-minx, -miny)
    } else {
        delta = Pos(0, 0)
    }
    return delta
}

@JvmName("toCharXYMap")
fun Map<Pos, Char>.toXYMap(): XYMap<Char> = this.toXYMap(' ')

@JvmName("toIntXYMap")
fun Map<Pos, Int>.toXYMap(): XYMap<Int> = this.toXYMap(0)

@JvmName("toLongXYMap")
fun Map<Pos, Long>.toXYMap(): XYMap<Long> = this.toXYMap(0L)

class IntXYMap(minx: Int, maxx: Int, miny: Int, maxy: Int, values: List<Int>, default: Int, autoexpand: Boolean) : XYMap<Int>(minx, maxx, miny, maxy, values, default, autoexpand) {
    constructor(dimx: Int, dimy: Int, values: List<Int>, default: Int, autoexpand: Boolean) : this(0, dimx - 1, 0, dimy - 1, values, default, autoexpand)
    constructor(dimx: Int, dimy: Int, values: List<Int>) : this(dimx, dimy, values, 0, false)
    constructor(dimx: Int, dimy: Int, values: (Int, Int) -> Int) : this(dimx, dimy, (0 until dimy).flatMap { y -> (0 until dimx).map { x -> values(x, y) } })
    constructor(dimx: Int, dimy: Int) : this(dimx, dimy, List(dimx * dimy) { 0 }, 0, false)
    constructor() : this(1, 1, listOf(0), 0, true)
}

class CharXYMap(minx: Int, maxx: Int, miny: Int, maxy: Int, values: List<Char>, autoexpand: Boolean) : XYMap<Char>(minx, maxx, miny, maxy, values, ' ', autoexpand) {

    constructor(dimx: Int, dimy: Int, values: List<Char>, autoexpand: Boolean) : this(0, dimx - 1, 0, dimy - 1, values, autoexpand)
    constructor(dimx: Int, dimy: Int, values: List<Char>) : this(dimx, dimy, values, false)
    constructor(dimx: Int, dimy: Int, values: (Int, Int) -> Char) : this(dimx, dimy, (0 until dimy).flatMap<Int, Char> { y: Int -> (0 until dimx).map { x -> values(x, y) } })
    constructor(dimx: Int, dimy: Int) : this(dimx, dimy, List(dimx * dimy) { ' ' }, false)
    constructor() : this(1, 1, listOf(' '), true)
}

val colors = listOf(white, red, blue, yellow, green, cyan, magenta)

fun <T> colorMapper(values: List<T>, blackVal: T?): (T, String) -> String {
    if (values.size > colors.size) {
        throw IllegalArgumentException("Too many different values: $values")
    }
    val colorMap = values
        .filter { it != blackVal }
        .mapIndexed { index, v -> v to colors[index] }.toMap()
    return { it, s ->
        val c = if (it == blackVal) {
            black
        } else {
            colorMap[it]!!
        }
        "${c}$s${reset}"
    }
}

fun Pos.isOnBorderOf(map: XYMap<*>) = map.isOnBorder(this)

fun Pos.isInBoundsOf(map: XYMap<*>) = map.isInBounds(this)

fun Pos.wrapAround(map: XYMap<*>): Pos {
    val x = (x - map.minx).mod(map.dimx) + map.minx
    val y = (y - map.miny).mod(map.dimy) + map.miny
    return Pos(x, y)
}
