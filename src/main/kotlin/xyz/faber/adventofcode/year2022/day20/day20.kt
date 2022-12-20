package xyz.faber.adventofcode.year2022.day20

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution

class Node(val number: Long) {
    var prev: Node? = null
    var next: Node? = null
}

class Day20 : AdventSolution<Long>() {
    override fun part1(input: List<String>): Long {
        val numbers = input.map { it.toLong() }
        val nodesInOriginalOrder = mutableListOf<Node>()
        var prev: Node? = null
        for (number in numbers) {
            val node = Node(number)
            if (prev != null) {
                node.prev = prev
                prev.next = node
            }
            nodesInOriginalOrder += node
            prev = node
        }
        var zeroNode = nodesInOriginalOrder.single { it.number == 0L }

        prev!!.next = nodesInOriginalOrder[0]
        nodesInOriginalOrder[0].prev = prev!!

        for (node in nodesInOriginalOrder) {
            var node2 = getNode(node, node.number.mod(numbers.size - 1L))
            if (node != node2) {
                node.next!!.prev = node.prev
                node.prev!!.next = node.next
                node.prev = node2
                node.next = node2.next
                node.prev!!.next = node
                node.next!!.prev = node
            }
        }

        val a = getNode(zeroNode, 1000).number
        val b = getNode(zeroNode, 2000).number
        val c = getNode(zeroNode, 3000).number
        return a + b + c
    }

    override fun part2(input: List<String>): Long {
        val numbers = input.map { it.toLong() }
        val nodesInOriginalOrder = mutableListOf<Node>()
        var prev: Node? = null
        for (number in numbers) {
            val node = Node(number * 811589153L)
            if (prev != null) {
                node.prev = prev
                prev.next = node
            }
            nodesInOriginalOrder += node
            prev = node
        }
        var zeroNode = nodesInOriginalOrder.single { it.number == 0L }

        prev!!.next = nodesInOriginalOrder[0]
        nodesInOriginalOrder[0].prev = prev!!

        for (i in 1..10) {
            for (node in nodesInOriginalOrder) {
                var node2 = getNode(node, node.number.mod(numbers.size - 1L))
                if (node != node2) {
                    move(node, node2)
                }
            }
        }

        val a = getNode(zeroNode, 1000).number
        val b = getNode(zeroNode, 2000).number
        val c = getNode(zeroNode, 3000).number
        return a + b + c
    }

    private fun move(node: Node, insertAfter: Node) {
        node.next!!.prev = node.prev
        node.prev!!.next = node.next
        node.next = insertAfter.next
        insertAfter.next = node
        node.prev = insertAfter
        node.next!!.prev = node
    }

    fun print(node: Node) {
        var it = node
        do {
            print(it.number)
            print(" ")
            it = it.next!!
        } while (it != node)
        println()
    }

    fun getNode(start: Node, i: Long): Node {
        var node = start
        if (i > 0) {
            for (j in 1..i) {
                node = node.next!!
            }
        } else if (i < 0) {
            for (j in 0..-i) {
                node = node.prev!!
            }
        }
        return node
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 20, Day20()).run()

}
