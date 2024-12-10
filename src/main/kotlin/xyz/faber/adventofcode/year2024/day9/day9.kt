package xyz.faber.adventofcode.year2024.day9

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import java.util.*

class Day9 : AdventSolution<Long>() {
    private fun makeFileSystem(input: String): MutableList<Int> {
        val b = mutableListOf<Int>()
        var block = true
        var blockCount = 0
        for (c in input.trim()) {
            val d = +c.digitToInt()
            if (block) {
                for (i in 0 until d) {
                    b.add(blockCount)
                }
                blockCount++
            } else {
                for (i in 0 until d) {
                    b.add(-1)
                }
            }
            block = !block
        }
        return b
    }

    private fun checksum(b: List<Int>) =
        b.withIndex().sumOf { (i, v) -> if (v != -1) i * v * 1L else 0L }

    override fun part1(input: String): Long {
        val b = makeFileSystem(input)
        var i = 0
        var j = b.size - 1
        while (i < j) {
            if (b[j] == -1) {
                j--
            } else if (b[i] == -1) {
                b[i] = b[j]
                b[j] = -1
            } else {
                i++
            }
        }

        return checksum(b)
    }

    private fun makeFileSystem2(input: String): MutableList<Pair<Int, Int>> {
        val b = ArrayList<Pair<Int, Int>>()
        var block = true
        var blockCount = 0
        for (c in input.trim()) {
            val d = +c.digitToInt()
            if (block) {
                if (d > 0) {
                    b.add(blockCount to d)
                }
                blockCount++
            } else {
                if (d > 0) {
                    b.add(-1 to d)
                }
            }
            block = !block
        }
        return b
    }

    private fun checksum2(b: List<Pair<Int, Int>>): Long {
        var res = 0L
        var i = 0
        for ((blockIndex, len) in b) {
            if (blockIndex == -1) {
                i += len
            } else {
                for (j in i until i + len) {
                    res += j * blockIndex
                }
                i += len
            }
        }
        return res
    }

    private fun print(b: List<Pair<Int, Int>>) {
        for ((blockIndex, blockLength) in b) {
            for (j in 0 until blockLength) {
                if (blockIndex == -1) {
                    print(".")
                } else {
                    print(blockIndex)
                }
            }
        }
        println()
    }

    override fun part2(input: String): Long {
        val b = makeFileSystem2(input)
        var i = b.size - 1
        var firstHole = 1
        while (i >= 0) {
            val blockIndex = b[i].first
            if (blockIndex == -1) {
                i--
            } else {
                var foundHole = false
                for (j in firstHole until i) {
                    if (!foundHole && b[j].first == -1) {
                        foundHole = true
                        firstHole = j
                    }
                    if (b[j].first == -1 && b[j].second >= b[i].second) {
                        val blockLength = b[i].second
                        val emptyLength = b[j].second
                        val diff = emptyLength - blockLength
                        b[j] = b[i]
                        if (diff > 0) {
                            if (b[j + 1].first == -1) {
                                b[j + 1] = -1 to b[j + 1].second + diff
                            } else {
                                b.add(j + 1, -1 to diff)
                                i++
                            }
                        }
                        b[i] = -1 to blockLength
                        if (b.size > i + 1 && b[i + 1].first == -1) {
                            b[i] = -1 to b[i].second + b[i + 1].second
                            b.removeAt(i + 1)
                        }
                        if (b[i - 1].first == -1) {
                            b[i - 1] = -1 to b[i - 1].second + b[i].second
                            b.removeAt(i)
                        }
                        break
                    }
                }
                i--
            }
        }

        return checksum2(b) //6330095022244
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 9, Day9()).run()

}
