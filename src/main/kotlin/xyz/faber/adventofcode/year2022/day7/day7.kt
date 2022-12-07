package xyz.faber.adventofcode.year2022.day7

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Dir(val parent: Dir?, val name: String) {
    val subdirs = mutableListOf<Dir>()
    var filesizes = 0L
}

fun Dir.recursiveExpand(): Sequence<Dir> = sequenceOf(this) + this.subdirs.flatMap { it.recursiveExpand() }

fun Dir.totalSize() = this.recursiveExpand().sumOf { it.filesizes }

class Day7 : AdventSolution<Long>() {
    fun traverse(input: List<String>): Dir {
        val cmds = input.map { it.split(" ") }
        val root = Dir(null, "/")
        var currentDir = root
        for (cmd in cmds) {
            if (cmd[0] == "$" && cmd[1] == "cd" && cmd[2] == "/") {
                currentDir = root
            } else if (cmd[0] == "$" && cmd[1] == "cd" && cmd[2] == "..") {
                currentDir = currentDir.parent!!
            } else if (cmd[0] == "$" && cmd[1] == "cd") {
                var nextDir = currentDir.subdirs.singleOrNull { it.name == cmd[2] }
                assert(nextDir != null) { "Did not ls ${cmd[1]} before cd" }
                currentDir = nextDir!!
            } else if (cmd[0] == "$" && cmd[1] == "ls") {
                //
            } else if (cmd[0] == "dir") {
                val dir = Dir(currentDir, cmd[1])
                currentDir.subdirs += dir
            } else {
                currentDir.filesizes += cmd[0].toLong()
            }
        }
        return root
    }

    override fun part1(input: List<String>): Long {
        val root = traverse(input)

        val smallDirs = root.recursiveExpand()
            .filter { it.totalSize() <= 100000L }

        return smallDirs.sumOf { it.totalSize() }
    }

    override fun part2(input: List<String>): Long {
        val root = traverse(input)

        val missingSpace = root.totalSize() - (70000000 - 30000000)

        val bestDir = root.recursiveExpand()
            .filter { it.totalSize() >= missingSpace }
            .minBy { it.totalSize() }

        return bestDir.totalSize()
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 7, Day7()).run()

}
