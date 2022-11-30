package xyz.faber.adventofcode.year2021.day25

import xyz.faber.adventofcode.util.XYMap
import xyz.faber.adventofcode.util.getInputFromLines
import xyz.faber.adventofcode.util.toXYMap

class Day25 {
    val input = getInputFromLines(2021, 25)

    fun part1() {
        var map = input.toXYMap()
        var steps = 0
        while (true) {
            //map.print()
            //println()
            val nextmap = XYMap(map.dimx, map.dimy, '.')
            var moved = false
            for ((pos, c) in map) {
                if (c == '>') {
                    val nextpos = pos.copy(x = (pos.x + 1) % map.dimx)
                    if (map[nextpos] == '.') {
                        nextmap[nextpos] = '>'
                        moved = true
                    } else {
                        nextmap[pos] = '>'
                    }
                }
            }
            //nextmap.print()
            //println()
            for ((pos, c) in map) {
                if (c == 'v') {
                    val nextpos = pos.copy(y = (pos.y + 1) % map.dimy)
                    if (map[nextpos] != 'v' && nextmap[nextpos] == '.') {
                        nextmap[nextpos] = 'v'
                        moved = true
                    } else {
                        nextmap[pos] = 'v'
                    }
                }
            }
            steps++
            if (!moved) {
                println(steps)
                break
            }
            map = nextmap
            if(steps%1000==0) println(steps)
        }
    }


    fun part2() {
    }
}

fun main(args: Array<String>) {
    val d = Day25()

    d.part1()
    d.part2()
}
