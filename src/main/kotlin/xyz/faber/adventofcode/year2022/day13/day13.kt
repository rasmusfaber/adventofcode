package xyz.faber.adventofcode.year2022.day13

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.split
import java.io.BufferedReader
import java.io.StringReader
import kotlin.math.min

class Day13 : AdventSolution<Int>() {
    data class Tree<T>(val value: T? = null, val children: List<Tree<T>>? = null) {
        constructor(value: T) : this(value, null)
        constructor(children: List<Tree<T>>) : this(null, children)

        override fun toString(): String {
            return if (value != null) {
                value.toString()
            } else {
                "[${children?.map { it.toString() }?.joinToString(",")}]"
            }
        }

        fun copy(): Tree<T> = Tree(value, children?.map { it.copy() })

    }

    fun compare(left: Tree<Int>, right: Tree<Int>): Int {
        if (left.value != null && right.value != null) {
            return Integer.compare(left.value, right.value)
        } else if (left.children != null && right.children != null) {
            for (i in 0 until min(left.children.size, right.children.size)) {
                val compare = compare(left.children[i], right.children[i])
                if (compare != 0) {
                    return compare
                }
            }
            return Integer.compare(left.children.size, right.children.size)
        } else if (left.value != null) {
            return compare(Tree(null, listOf(left)), right)
        } else {
            return compare(left, Tree(null, listOf(right)))
        }
    }

    fun parseTree(s: String): Tree<Int> {
        return parseTreeNode(BufferedReader(StringReader(s)))
    }

    fun parseTreeNode(sr: BufferedReader): Tree<Int> {
        val first = sr.read().toChar()
        if (first == '[') {
            val list = mutableListOf<Tree<Int>>()
            sr.mark(1)
            val c = sr.read().toChar()
            if (c == ']') {
                return Tree(list);
            }
            sr.reset()
            do {
                val value = parseTreeNode(sr)
                list.add(value)
                val c = sr.read().toChar() // , or ]
            } while (c == ',')
            return Tree(list)
        } else {
            sr.mark(2)
            var i = 0
            while (sr.read().toChar().isDigit()) {
                i++
            }
            sr.reset()
            val value = (first + (1..i).map { sr.read().toChar() }.joinToString("")).toInt()

            return Tree(value)
        }
    }

    override fun part1(input: List<String>): Int {
        val trees = input.split(listOf("")).map { parseTree(it[0]) to parseTree(it[1]) }
        return trees.withIndex()
            .filter { compare(it.value.first, it.value.second) < 0 }
            .sumOf { it.index + 1 }
    }

    override fun part2(input: List<String>): Int {
        val div1 = parseTree("[[2]]")
        val div2 = parseTree("[[6]]")
        val trees = input.filter { it.isNotEmpty() }
            .map { parseTree(it) } + div1 + div2

        val sorted = trees.sortedWith { a, b -> compare(a, b) }

        val index1 = sorted.indexOf(div1)
        val index2 = sorted.indexOf(div2)

        return (index1+1)*(index2+1)
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 13, Day13()).run()

}
