package xyz.faber.adventofcode.year2020.day22

import xyz.faber.adventofcode.util.getInput
import java.util.*

class Day22 {
    val input = getInput(2020, 22)
    /*val input = "Player 1:\n" +
            "9\n" +
            "2\n" +
            "6\n" +
            "3\n" +
            "1\n" +
            "\n" +
            "Player 2:\n" +
            "5\n" +
            "8\n" +
            "4\n" +
            "7\n" +
            "10"*/
    val inputSplit = input.split("\n\n")
    val deck1Start = inputSplit[0].lines().filter { it.isNotBlank() }.let { it.subList(1, it.size) }.map { it.toInt() }
    val deck2Start = inputSplit[1].lines().filter { it.isNotBlank() }.let { it.subList(1, it.size) }.map { it.toInt() }

    fun part1() {
        val deck1 = LinkedList(deck1Start)
        val deck2 = LinkedList(deck2Start)
        while (deck1.isNotEmpty() && deck2.isNotEmpty()) {
            val c1 = deck1.pop()
            val c2 = deck2.pop()
            if (c1 > c2) {
                deck1.add(c1)
                deck1.add(c2)
            } else {
                deck2.add(c2)
                deck2.add(c1)
            }
        }
        val winner = if (deck1.isNotEmpty()) deck1 else deck2
        val res = winner.withIndex().sumBy { (winner.size - it.index) * it.value }
        println(res)
    }


    fun part2() {
        val (winner, winningDeck) = recursiveCombat(deck1Start, deck2Start)
        val res = winningDeck.withIndex().sumBy { (winningDeck.size - it.index) * it.value }
        println(res)
    }

    private fun recursiveCombat(deck1: List<Int>, deck2: List<Int>): Pair<Int, List<Int>> {
        val deck1 = LinkedList(deck1)
        val deck2 = LinkedList(deck2)
        val previousDeck1s = mutableSetOf<List<Int>>()
        while (deck1.isNotEmpty() && deck2.isNotEmpty()) {
            if (deck1 in previousDeck1s) {
                return 1 to deck1
            }
            previousDeck1s.add(deck1.toList())
            val c1 = deck1.pop()
            val c2 = deck2.pop()
            if (c1 <= deck1.size && c2 <= deck2.size) {
                val rw = recursiveCombat(deck1.subList(0, c1), deck2.subList(0, c2))
                if (rw.first == 1) {
                    deck1.add(c1)
                    deck1.add(c2)
                } else {
                    deck2.add(c2)
                    deck2.add(c1)
                }
            } else {
                if (c1 > c2) {
                    deck1.add(c1)
                    deck1.add(c2)
                } else {
                    deck2.add(c2)
                    deck2.add(c1)
                }
            }
        }
        return if (deck1.isNotEmpty()) 1 to deck1 else 2 to deck2 // not 35327
    }

}

fun main(args: Array<String>) {
    val d = Day22()
    d.part1()
    d.part2()
}
