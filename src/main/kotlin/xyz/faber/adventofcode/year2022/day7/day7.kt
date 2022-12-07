package xyz.faber.adventofcode.year2022.day7

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.recursiveFlatten

class Dir(val parent: Dir?, val name: String) {
    val subdirs = mutableListOf<Dir>()
    var filesizes = 0L
}

fun Dir.recursiveFlatten(): Sequence<Dir> = this.recursiveFlatten { it.subdirs.asSequence() }

fun Dir.totalSize() = this.recursiveFlatten().sumOf { it.filesizes }

class Day7 : AdventSolution<Long>() {
    fun traverse(input: List<String>): Dir {
        val cmds = input.map { it.split(" ") }
        val root = Dir(null, "/")
        var currentDir = root
        for (cmd in cmds) {
            when {
                cmd[0] == "$" && cmd[1] == "cd" && cmd[2] == "/" -> currentDir = root
                cmd[0] == "$" && cmd[1] == "cd" && cmd[2] == ".." -> currentDir = currentDir.parent!!
                cmd[0] == "$" && cmd[1] == "cd" -> currentDir = currentDir.subdirs.singleOrNull { it.name == cmd[2] } ?: throw IllegalArgumentException("Did not ls ${cmd[1]} before cd")
                cmd[0] == "$" && cmd[1] == "ls" -> {}
                cmd[0] == "dir" -> currentDir.subdirs += Dir(currentDir, cmd[1])
                else -> currentDir.filesizes += cmd[0].toLong()
            }
        }
        return root
    }

    override fun part1(input: List<String>): Long {
        val root = traverse(input)

        val smallDirs = root.recursiveFlatten()
            .filter { it.totalSize() <= 100000L }

        return smallDirs.sumOf { it.totalSize() }
    }

    override fun part2(input: List<String>): Long {
        val root = traverse(input)

        val missingSpace = root.totalSize() - (70000000 - 30000000)

        val bestDir = root.recursiveFlatten()
            .filter { it.totalSize() >= missingSpace }
            .minBy { it.totalSize() }

        return bestDir.totalSize()
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 7, Day7()).run()

}
