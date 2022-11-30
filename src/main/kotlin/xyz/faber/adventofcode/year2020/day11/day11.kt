package xyz.faber.adventofcode.year2020.day11

import xyz.faber.adventofcode.util.Pos
import xyz.faber.adventofcode.util.XYMap
import xyz.faber.adventofcode.util.getInput
import xyz.faber.adventofcode.util.toXYMap

class Day11 {
    val input = getInput(2020, 11).toXYMap()

    fun part1() {
        val seats = input.filter { it.value == 'L' }.map { it.pos }
        var occupied = emptySet<Pos>()
        while (true) {
            val next = seats.filter {
                val occupiedNeighbors = it.adjacent().count { n -> n in occupied }
                if (it !in occupied) {
                    occupiedNeighbors == 0
                } else {
                    occupiedNeighbors <= 3
                }
            }.toSet()
            if (next == occupied) {
                println(next.size)
                return
            }
            occupied = next
        }
    }


    fun part2() {
        val seats = input.filter { it.value == 'L' }.map { it.pos }
        val neighbors = seats.map { it to getVisibleNeighbors(input, it) }.toMap()


        var occupied = emptySet<Pos>()
        while (true) {
            val next = seats.filter {
                val occupiedNeighbors = neighbors[it]!!.count { n -> n in occupied }
                if (it !in occupied) {
                    occupiedNeighbors == 0
                } else {
                    occupiedNeighbors <= 4
                }
            }.toSet()
            if (next == occupied) {
                println(next.size)
                return
            }
            occupied = next
        }
    }

    private fun getVisibleNeighbors(xyMap: XYMap<Char>, p: Pos) : List<Pos>{
        val directions = listOf(Pos(- 1, - 1), Pos(0,  - 1), Pos(1, - 1),
                Pos(- 1, 0), Pos( 1,0),
                Pos( - 1, 1), Pos(0,  1), Pos( 1,  1))
       return directions.map{firstVisible(xyMap, p, it)}.filterNotNull()
    }

    private fun firstVisible(xyMap: XYMap<Char>, p: Pos, dir: Pos):Pos? {
        var res = p + dir
        while(xyMap.isInBounds(res)){
            if(xyMap[res]=='L'){
                return res
            }
            res += dir
        }
        return null
    }

}

fun main(args: Array<String>) {
    val d = Day11()
    d.part1()
    d.part2()
}
