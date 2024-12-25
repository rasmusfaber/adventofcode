package xyz.faber.adventofcode.util

import java.math.BigInteger
import kotlin.math.abs
import kotlin.math.sign

tailrec fun gcd(n1: Int, n2: Int): Int {
    if (n2 != 0)
        return gcd(n2, n1 % n2)
    else
        return abs(n1)
}

tailrec fun gcdExtended(a: Int, b: Int): Triple<Int, Int, Int> {
    // Base Case
    if (a == 0) {
        return Triple(abs(b), 0, b.sign)
    }

    val (gcd, x, y) = gcdExtended(b % a, a)

    return Triple(gcd, y - b / a * x, x)
}

tailrec fun gcdExtended(a: Long, b: Long): Triple<Long, Long, Long> {
    // Base Case
    if (a == 0L) {
        return Triple(abs(b), 0, b.sign.toLong())
    }

    val (gcd, x, y) = gcdExtended(b % a, a)

    return Triple(gcd, y - b / a * x, x)
}

tailrec fun gcdExtended(a: BigInteger, b: BigInteger): Triple<BigInteger, BigInteger, BigInteger> {
    // Base Case
    if (a.signum() == 0) {
        return Triple(b.abs(), BigInteger.ZERO, BigInteger.valueOf(b.signum().toLong()))
    }

    val (gcd, x, y) = gcdExtended(b % a, a)

    return Triple(gcd, y - b / a * x, x)
}

tailrec fun gcd(n1: Long, n2: Long): Long {
    if (n2 != 0L)
        return gcd(n2, n1 % n2)
    else
        return abs(n1)
}

fun lcm(a: Int, b: Int) = a / gcd(a, b) * b

fun lcm(a: Long, b: Long) = a / gcd(a, b) * b

fun lcm(vararg values: Long) = values.reduce { a, b -> lcm(a, b) }

fun lcm(vararg values: Int) = values.reduce { a, b -> lcm(a, b) }

fun lcm(values: Collection<Long>) = values.reduce { a, b -> lcm(a, b) }

fun lcm(values: Collection<Int>) = values.reduce { a, b -> lcm(a, b) }

fun modinverse(a: Long, m: Long): Long {
    val (_, x, y) = gcdExtended(a, m)
    return (x % m + m) % m
}

fun moddiv(a: Long, b: Long, m: Long): Long {
    val binv = modinverse(b, m)
    return (binv * a) % m
}

// pairs are period, start.
fun commonPeriod(a: Pair<Long, Long>, b: Pair<Long, Long>): Pair<Long, Long> {
    //Solve a1*x+a2 = b1*y+b2 <=> a1*x - b2*y = b2-a2
    val (a1, a2) = a
    val (b1, b2) = b
    val (d, xi, yi) = gcdExtended(a1, -b1)
    val n = b2 - a2
    if (n % d != 0L) {
        throw IllegalArgumentException("No solution")
    }
    val x = xi * n / d
    val y = yi * n / d
    val lcm = a1 * b1 / d
    val offset = Math.floorMod(a1 * x + a2, lcm)
    return lcm to offset
}

fun commonPeriod(vararg pairs: Pair<Long, Long>) = pairs.reduce { a, b -> commonPeriod(a, b) }

fun solveLinearDiophantine(a: Int, b: Int, c: Int): Pair<Pair<Int, Int>, Pair<Int, Int>>? {
    val (d, x, y) = gcdExtended(a, b)
    if (c % d != 0) {
        return null
    }
    val k = c / d
    return ((x * k to b/d) to (y * k to -a/d))
}

fun solveSimultaneousDiophantine(
    a1: Int, b1: Int, c1: Int,
    a2: Int, b2: Int, c2: Int
): Pair<Int, Int>? {
    // TODO
    // Solve each equation
    val eq1 = solveLinearDiophantine(a1, b1, c1) ?: return null
    val eq2 = solveLinearDiophantine(a2, b2, c2) ?: return null

    val gcd = gcdExtended(a1 * b2 - a2 * b1, b1 * b2).first
    if ((c2 - c1) % gcd != 0) return null // No solution

    val tCommon = solveLinearDiophantine(a1 * b2 - a2 * b1, b1 * b2, c2 - c1)?.first?.first ?: return null

    // Substitute tCommon back to get x and y
    val x = eq1.first.first + eq1.first.second * tCommon
    val y = eq1.second.first + eq1.second.second * tCommon
    return x to y
}
