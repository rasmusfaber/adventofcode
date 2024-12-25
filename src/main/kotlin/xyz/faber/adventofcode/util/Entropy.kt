package xyz.faber.adventofcode.util

import java.lang.Math.pow
import kotlin.math.ln
import kotlin.math.max

fun <T> knnEntropy(data: Collection<T>, coordinate: (T, Int) -> Double, dist: (T, T) -> Double, dim: Int, k: Int = 5): Double {
    val kdtree = KdTree(data, dim, coordinate, dist)
    val n = data.size

    val averageLogKDist = data.map { ln(dist(it, kdtree.nthNearestNeighbor(it, n)!!)) }.average()
    val cD = pow(Math.PI, dim.toDouble() / 2) / gamma(dim.toDouble() / 2 + 1)

    return digamma(n) - digamma(k) + ln(cD) + dim * averageLogKDist
}

fun knnEntropy(data: Collection<Pos>, k: Int = 5): Double {
    val coordinate2d = { p: Pos, i: Int -> if (i == 0) p.x.toDouble() else p.y.toDouble() }
    val dist2d = { p1: Pos, p2: Pos ->
        val dx = (p1.x - p2.x).toDouble()
        val dy = (p1.y - p2.y).toDouble()
        Math.sqrt(dx * dx + dy * dy)
    }
    return knnEntropy(data, coordinate2d, dist2d, 2, k)
}

fun <T> spacingEntropy(data: Collection<T>, coordinate: (T, Int) -> Double, dim: Int): Double {
    val spacings = 0.until(dim).map { i ->
        val sorted = data.sortedBy { coordinate(it, i) }
        sorted.zipWithNext { a, b -> coordinate(b, i) - coordinate(a, i) }
    }
    val delta = 0.0001
    val averageLogSpacing = spacings.map { it.map { it2->ln(max(it2, delta)) }.average() }.average()
    return -averageLogSpacing + ln(data.size.toDouble())
}

fun spacingEntropy(data: Collection<Pos>): Double {
    val coordinate2d = { p: Pos, i: Int -> if (i == 0) p.x.toDouble() else p.y.toDouble() }
    return spacingEntropy(data, coordinate2d, 2)
}

fun gamma(x: Double): Double {
    // Lanczos approximation
    var xx = x
    val p = doubleArrayOf(
        0.99999999999980993,
        676.5203681218851,
        -1259.1392167224028,
        771.32342877765313,
        -176.61502916214059,
        12.507343278686905,
        -0.13857109526572012,
        9.9843695780195716e-6,
        1.5056327351493116e-7
    )
    val g = 7
    if (xx < 0.5) return Math.PI / (Math.sin(Math.PI * xx) * gamma(1.0 - xx))
    xx--
    var a = p[0]
    val t = xx + g + 0.5
    for (i in 1 until p.size) a += p[i] / (xx + i)
    return Math.sqrt(2.0 * Math.PI) * Math.pow(t, xx + 0.5) * Math.exp(-t) * a
}

fun digamma(x: Int): Double {
    // Euler-Maclaurin
    var t = x.toDouble()
    var result = 0.0
    while (t < 7) {
        result -= 1 / t
        t += 1
    }
    t -= 0.5
    val xx = 1 / t
    result += ln(t) + 1 / 12 * xx * xx
    return result
}