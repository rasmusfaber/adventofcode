package xyz.faber.adventofcode.year2021.day18

import xyz.faber.adventofcode.util.getInputFromLines
import java.io.StringReader
import java.util.*

data class BinaryTree<T>(var value: T? = null, var left: BinaryTree<T>? = null, var right: BinaryTree<T>? = null) {
    constructor(value: T) : this(value, null, null)
    constructor(left: BinaryTree<T>, right: BinaryTree<T>) : this(null, left, right)

    override fun toString(): String {
        return if (value != null) {
            value.toString()
        } else {
            "[${left.toString()},${right.toString()}]"
        }
    }

    fun copy(): BinaryTree<T> = BinaryTree(value, left?.copy(), right?.copy())
}

fun parseBinaryTree(s: String): BinaryTree<Int> {
    return parseBinaryTreeNode(StringReader(s))
}

fun parseBinaryTreeNode(sr: StringReader): BinaryTree<Int> {
    val first = sr.read().toChar()
    if (first == '[') {
        val left = parseBinaryTreeNode(sr)
        sr.read() // ,
        val right = parseBinaryTreeNode(sr)
        sr.read() // ]
        return BinaryTree(left, right)
    } else {
        val value = first.toString().toInt()
        return BinaryTree(value)
    }
}

fun explode(a: BinaryTree<Int>): Boolean {
    var prevValue: BinaryTree<Int>? = null

    var stack = LinkedList<Pair<BinaryTree<Int>, Int>>()
    stack.push(a to 1)
    var nextToAdd: Int? = null
    var exploded = false
    var exploding = false

    while (!stack.isEmpty()) {
        val (c, depth) = stack.pop()
        if (c.value != null) {
            prevValue = c
            if (nextToAdd != null) {
                c.value = c.value!! + nextToAdd
                nextToAdd = null
                return true
            }
        } else if (depth == 5 && !exploding) {
            if (prevValue != null) {
                prevValue.value = prevValue.value!! + c.left!!.value!!
            }
            nextToAdd = c.right!!.value!!
            c.value = 0
            c.left = null
            c.right = null
            exploded = true
            exploding = true
        } else {
            stack.push(c.right!! to depth + 1)
            stack.push(c.left!! to depth + 1)
        }
    }
    return false
}

fun split(a: BinaryTree<Int>): Boolean {
    var stack = LinkedList<BinaryTree<Int>>()
    stack.push(a)

    while (!stack.isEmpty()) {
        val c = stack.pop()
        if (c.value != null) {
            if (c.value!! > 9) {
                c.left = BinaryTree(c.value!! / 2)
                c.right = BinaryTree((c.value!! + 1) / 2)
                c.value = null
                return true
            }
        } else {
            stack.push(c.right)
            stack.push(c.left)
        }
    }
    return false
}

fun reduce(a: BinaryTree<Int>) {
    var done = false
    while (!done) {
        if (!explode(a)) {
            if (!split(a)) {
                done = true
            }
        }
    }
}

fun addAndReduce(a: BinaryTree<Int>, b: BinaryTree<Int>): BinaryTree<Int> {
    val res = BinaryTree(null, a, b)
    reduce(res)
    return res
}

fun magnitude(a: BinaryTree<Int>): Int {
    if (a.value != null) {
        return a.value!!
    } else {
        return 3 * magnitude(a.left!!) + 2 * magnitude(a.right!!)
    }
}

class Day18 {
    val input = getInputFromLines(2021, 18)

    fun part1() {
        val res = input.map { parseBinaryTree(it) }
            .reduce(::addAndReduce)
            .let { magnitude(it) }
        println(res)
    }


    fun part2() {
        val values = input.map { parseBinaryTree(it) }
        var max = 0
        for (i in values) {
            for (j in values) {
                if (i == j) continue
                val x = addAndReduce(i.copy(), j.copy())
                max = maxOf(max, magnitude(x))
            }
        }
        println(max)
    }
}

fun main(args: Array<String>) {
    val d = Day18()

    d.part1()
    d.part2()
}
