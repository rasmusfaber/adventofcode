package xyz.faber.adventofcode.year2022.day10

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.detectText
import xyz.faber.adventofcode.util.listsToXYMap
import kotlin.math.absoluteValue

class Day10 : AdventSolution<Int>() {
    fun run(program: List<String>, onTick: (i: Int, x: Int) -> Unit) {
        var counter = 0
        var x = 1
        for (line in program) {
            val s = line.split(' ')
            if (s[0] == "noop") {
                counter++
                onTick(counter, x)
            } else if (s[0] == "addx") {
                counter++
                onTick(counter, x)
                counter++
                onTick(counter, x)
                x += s[1].toInt()
            }
        }
    }

    override fun part1(input: List<String>): Int {
        var res = 0
        run(input) { i, x ->
            if (i % 40 == 20) {
                res += i * x
            }
        }
        return res
    }

    override fun part2(input: List<String>): Int {
        var output = mutableListOf<Boolean>()
        run(input) { i, x ->
            val dot = (x - ((i - 1) % 40)).absoluteValue <= 1
            output.add(dot)
        }
        val map = output.chunked(40)
            .listsToXYMap()
        map.printBlockIf { it }
        println(map.detectText())
        return 0
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 10, Day10()).run()

}
