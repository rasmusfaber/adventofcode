package xyz.faber.adventofcode.year2022.day14

import xyz.faber.adventofcode.util.*

class Day14 : AdventSolution<Int>() {
    fun parseMap(input: List<String>): XYMap<Boolean> {
        val map = XYMap(false)
        for (line in input) {
            val points = line.split(" -> ").map { it.toPos() }
            for ((segmentStart, segmentEnd) in points.windowed(2)) {
                var it = segmentStart
                map[it] = true
                do {
                    it += (segmentEnd - segmentStart).sign()
                    map[it] = true
                } while (it != segmentEnd)
            }
        }
        return map
    }

    override fun part1(input: List<String>): Int {
        val map = parseMap(input)
        var sandCount = 0
        while (true) {
            var sandPos = Pos(500, 0)
            var stopped = false
            while(!stopped) {
                if (!map[sandPos + Pos(0, 1)]) {
                    sandPos += Pos(0, 1)
                } else if (!map[sandPos + Pos(-1, 1)]) {
                    sandPos += Pos(-1, 1)
                } else if (!map[sandPos + Pos(1, 1)]) {
                    sandPos += Pos(1, 1)
                } else {
                    stopped = true
                }
                if(sandPos.y > map.maxy){
                    return sandCount
                }
            }
            map[sandPos] = true
            sandCount++
        }
    }

    override fun part2(input: List<String>): Int {
        val map = parseMap(input)
        val bottom = map.maxy+2
        var sandCount = 0
        while (true) {
            var sandPos = Pos(500, 0)
            var stopped = false
            while(!stopped) {
                if(sandPos.y==bottom-1){
                    stopped=true
                }else if (!map[sandPos + Pos(0, 1)]) {
                    sandPos += Pos(0, 1)
                } else if (!map[sandPos + Pos(-1, 1)]) {
                    sandPos += Pos(-1, 1)
                } else if (!map[sandPos + Pos(1, 1)]) {
                    sandPos += Pos(1, 1)
                } else {
                    stopped = true
                }
            }
            map[sandPos] = true
            sandCount++
            if(sandPos.y==0){
                return sandCount
            }
        }
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 14, Day14()).run()
}
