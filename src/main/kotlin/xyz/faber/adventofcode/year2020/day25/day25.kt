package xyz.faber.adventofcode.year2020.day25

import xyz.faber.adventofcode.util.getInputFromLines
import java.math.BigInteger

class Day25 {
    val input = getInputFromLines(2020, 25).map { it.toLong() }

    fun part1() {
        val cpriv = inverse(7, input[0])

        println(cpriv)

        val res = transform(input[1], cpriv)

        println(res)

    }

    fun inverse(v: Long, key: Long): Long {
        val vbi = BigInteger.valueOf(v)
        val kbi = BigInteger.valueOf(key)
        val mod = BigInteger.valueOf(20201227L)
        for (i in 1 until 20201227) {
            if (vbi.modPow(BigInteger.valueOf(i.toLong()), mod).equals(kbi)) {
                return i.toLong()
            }
        }
        throw IllegalArgumentException("Not invertible")
    }

    fun transform(v: Long, loopSize: Long): Long {
        val vbi = BigInteger.valueOf(v)
        val lbi = BigInteger.valueOf(loopSize)
        val mod = BigInteger.valueOf(20201227L)
        return vbi.modPow(lbi, mod).longValueExact()
    }


    fun part2() {
    }

}

fun main(args: Array<String>) {
    val d = Day25()
    d.part1()
    d.part2()
}
