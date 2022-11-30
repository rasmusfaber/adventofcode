package xyz.faber.adventofcode.year2019.day22

import xyz.faber.adventofcode.util.getInputFromLines
import java.math.BigInteger

class Day22 {
    val input = getInputFromLines(2019, 22)

    fun part1a() {
        val c = 10007
        var deck = (0 until c).toList()
        for (l in input) {
            deck = shuffle(l, deck)
        }
        val res = deck.indexOf(2019)
        println(res)
    }

    fun part1b() {
        val c = 10007
        val t = translate(input, 10007)
        val res = (2019 *t.first+t.second)%10007
        println(res)
    }

    private fun shuffle(l: String, deck: List<Int>): List<Int> {
        return when {
            l.startsWith("deal into") -> {
                 deck.reversed()
            }
            l.startsWith("cut") -> {
                val v = (l.split(" ").last().toInt() + deck.size) % deck.size
                 deck.subList(v, deck.size) + deck.subList(0, v)
            }
            l.startsWith("deal with increment") -> {
                val n = l.split(" ").last().toInt()
                var deck2 = MutableList(deck.size) { -1 }
                var co = 0
                for (i in 0 until deck2.size) {
                    deck2[co] = deck[i]
                    co = (co + n) % deck2.size
                }
                 deck2
            }
            else -> throw IllegalArgumentException(l)
        }
    }


    fun part2() {
        val ds = 119315717514047L
        val repeat = 101741582076661L
        var t2 = translate(input, ds)
        val a = BigInteger.valueOf(t2.first)
        val b = BigInteger.valueOf(t2.second)
        val n = BigInteger.valueOf(repeat)
        val m = BigInteger.valueOf(ds)
        val an = a.modPow(n, m)
        val temp = a.modPow(n,m).minus(BigInteger.ONE)
        val temp2 = a.minus(BigInteger.ONE)
        val temp3 = temp2.modInverse(m)
        val bn = temp.multiply(temp3).mod(m).multiply(b).mod(m)
        // an*x+bn = 2020

        val temp4 = BigInteger.valueOf(2020)
        val temp5 = temp4.minus(bn)
        val temp6 = an.modInverse(m)
        val res = temp5.multiply(temp6).mod(m)
        println(res)



    }

    private fun translate(input: List<String>, deckSize: Long): Pair<Long, Long> {
        var res = 1L to 0L
        for (l in input) {
            res = translate(res, l, deckSize)
        }
        return res
    }

    private fun translate(res: Pair<Long, Long>, l: String, deckSize: Long): Pair<Long, Long> {
        var res1 = res
        res1 = when {
            l.startsWith("deal into") -> {
                (deckSize - res1.first) % deckSize to (deckSize -1 - res1.second) % deckSize
            }
            l.startsWith("cut") -> {
                var v = l.split(" ").last().toLong()
                res1.first to (res1.second - v + deckSize) % deckSize
            }
            l.startsWith("deal with increment") -> {
                val n = l.split(" ").last().toLong()
                (res1.first * n) % deckSize to (res1.second * n) % deckSize
            }
            else -> throw RuntimeException(l)
        }
        return res1
    }

}

fun main(args: Array<String>) {
    val d = Day22()
    d.part1a()
    d.part1b()
    d.part2()
}
