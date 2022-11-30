package xyz.faber.adventofcode.year2019.day19

import adventofcode.year2019.intcode.runMachine
import xyz.faber.adventofcode.util.*
import kotlin.math.abs

class Day19 {
    val input = getInputLongsFromCsv(2019, 19)

    fun part1() {
        val map = XYMap(50, 50) { p -> runMachine(input, p.x, p.y).single() == 1L }
        val res = map.positions().count { map[it] }
        println(res)
        map.printBlockIf { it }
    }


    fun part2() {
        val y = findMax({ testy(it.toInt()) == null }, 100).toInt() + 1

        println(testy(y))
    }

    fun part3(){
        XYMap(50,50){(x,y)->test(x,y,18,89,136)}.printBlockIf { it }
        val map = XYMap(10, 10) { p -> runMachine(input, p.x, p.y).single() == 1L }
        val res = (0..200).toList().powercombo(3).filter { (a,b,c)->match(map, a,b,c) }
        println(res)
        val map2 = XYMap(100, 100) { p -> runMachine(input, p.x, p.y).single() == 1L }
        val res2 = res.filter { (a,b,c)->match(map2, a,b,c) }

        println(res2)
        //14, 149, 127

    }
    fun match(map:XYMap<Boolean>, a:Int, b:Int, c:Int):Boolean{
        return map.positions().all { test(it.x,it.y,a,b,c)==map[it] }
    }

    fun test(x:Int, y:Int, a:Int, b:Int, c:Int)=(a*x*y) >= abs(b*x*x-c*y*y)

    private fun xrange(y: Int): IntRange {
        var min = 0
        while (!test(min, y)) min++
        var max = findMax({ test(it.toInt(), y) }, min.toLong()).toInt()
        return min..max
    }

    private fun testy(y: Int): Pos? {
        val xrange = xrange(y)
        val xrange2 = xrange(y + 99)
        if ((xrange2.first + 99) in xrange) {
            return Pos(xrange2.first, y)
        }
        return null
    }

    private fun test(p: Pos): Boolean = test(p.x, p.y)

    private fun test(x: Int, y: Int) = runMachine(input, x, y).single() == 1L
}

fun main(args: Array<String>) {
    val d = Day19()
    //d.part1()
    //d.part2()
    d.part3()
}
