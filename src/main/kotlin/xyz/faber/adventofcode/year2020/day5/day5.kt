package xyz.faber.adventofcode.year2020.day5

import xyz.faber.adventofcode.util.getInputFromLines

class Day5 {
    val input = getInputFromLines(2020, 5)

  fun part1() {
        println(input.map{ toRowColumn(it)}.map { it.first*8+it.second }.maxOrNull())
    }


    fun part2() {
        val ids = input.map { toRowColumn(it) }.map { it.first * 8 + it.second }.toSet()
        val min = ids.minOrNull()!!
        val max = ids.maxOrNull()!!
        val res = (min..max).first { it !in ids}
        println(res)

    }

}

private fun toRowColumn(s: String):Pair<Int,Int>{
    val r=s.substring(0,7).replace("F", "0").replace("B", "1").toInt(2)
    val c =s.substring(7,10).replace("L", "0").replace("R", "1").toInt(2)
    return r to c
}


fun main(args: Array<String>) {
   val d = Day5()
    d.part1()
    d.part2()
}
