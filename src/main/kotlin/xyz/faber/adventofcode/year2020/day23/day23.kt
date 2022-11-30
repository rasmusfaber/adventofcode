package xyz.faber.adventofcode.year2020.day23

import xyz.faber.adventofcode.util.getInputAsDigits
import java.util.*

class Day23 {
    val input = getInputAsDigits(2020, 23)
    //val input = "389125467".toCharArray().map{it.toString().toInt()}

    fun part1() {
        val cups = LinkedList(input)
        val m = cups.size
        var current = 0
        for (i in 1..100) {
            val currentVal = cups[current]
            val p1 = cups.removeAt((current + 1) % cups.size)
            current = cups.indexOf(currentVal)
            val p2 = cups.removeAt((current + 1) % cups.size)
            current = cups.indexOf(currentVal)
            val p3 = cups.removeAt((current + 1) % cups.size)

            var next = (currentVal + m - 2) % m + 1
            var nextPos = cups.indexOf(next)
            while (nextPos == -1) {
                next = (next + m - 2) % m + 1
                nextPos = cups.indexOf(next)
            }

            cups.add(nextPos + 1, p3)
            cups.add(nextPos + 1, p2)
            cups.add(nextPos + 1, p1)

            current = cups.indexOf(currentVal)
            current = (current + 1) % m

            //println(cups.joinToString(""))
        }
        val indexOf1 = cups.indexOf(1)
        val res = cups.subList(indexOf1 + 1, cups.size).joinToString("") + cups.subList(0, indexOf1).joinToString("")
        println(res)
    }


    fun part2() {
        val cups = IntArray(1000001)
        cups[1000000]=input[0]
        for(i in 0..input.size-2){
            cups[input[i]] = input[i+1]
        }
        cups[input[input.size-1]] = input.size + 1
        for(i in input.size+1..1000000-1){
            cups[i] = i+1
        }
        var currentCup = input[0]
        for(i in 1..10000000) {
            val p1 = cups[currentCup]
            val p2 = cups[p1]
            val p3 = cups[p2]
            val next = cups[p3]

            var ins = (currentCup + 1000000 - 2) % 1000000 + 1
            while (ins == p1 || ins == p2 || ins == p3) {
                ins = (ins + 1000000 - 2) % 1000000 + 1
            }
            cups[p3] = cups[ins]
            cups[ins] = p1
            cups[currentCup] = next
            currentCup = next
        }
        val res = cups[1].toLong() * cups[cups[1]].toLong()
        println(res) // too low 343091157948
    }

}

fun main(args: Array<String>) {
    val d = Day23()
    d.part1()
    d.part2()
}
