package xyz.faber.adventofcode.util

import kotlin.math.max

fun <T> List<T>.replace(a: List<T>, b: List<T>): List<T> {
    val res = mutableListOf<T>()
    var i = 0
    while (i < this.size) {
        if (i + a.size <= this.size && this.subList(i, i + a.size) == a) {
            res.addAll(b)
            i += a.size
        } else {
            res.add(this[i])
            i++
        }
    }
    return res
}

fun <T> List<T>.split(separator: List<T>): List<List<T>> {
    val res = mutableListOf<List<T>>()
    var i = 0
    var j = 0
    while (j <= this.size - separator.size) {
        if (separator == this.subList(j, j + separator.size)) {
            if (i != j) {
                res.add(this.subList(i, j))
            }
            j += separator.size
            i = j
        } else {
            j++
        }
    }
    if (i != this.size) {
        res.add(this.subList(i, this.size))
    }
    return res
}

fun <T> List<T>.setAt(index: Int, element: T): List<T> = when {
    index !in 0..size -> throw Error("Cannot put at index $index because size is $size")
    else -> List(this.size) { if (it != index) this[it] else element }
}


fun <T> List<T>.longestRepeatedSublist(): List<T> {
    val n = this.size
    val lcsre = IntArray2D(n + 1, n + 1)
    var resLength = 0
    var resIndex = 0
    var i = 1
    while (i <= n) {
        for (j in i + 1..n) {
            if (this[i - 1] == this[j - 1]
                    && lcsre[i - 1, j - 1] < j - i) {
                lcsre[i, j] = lcsre[i - 1, j - 1] + 1
                if (lcsre[i, j] > resLength) {
                    resLength = lcsre[i, j]
                    resIndex = max(i, resIndex)
                }
            } else {
                lcsre[i, j] = 0
            }
        }
        i++
    }
    return this.subList(resIndex - resLength, resIndex)
}

fun String.longestRepeatedSubstring(): String = this.toCharArray().toList().longestRepeatedSublist().joinToString("")

private class IntArray2D(private val dimx: Int, dimy: Int) {
    private val innerArray = IntArray(dimx * dimy)

    operator fun get(x: Int, y: Int) = innerArray[x + y * dimx]

    operator fun set(x: Int, y: Int, v: Int) = innerArray.set(x + y * dimx, v)
}
