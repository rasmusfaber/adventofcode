package xyz.faber.adventofcode.year2020.day18

import xyz.faber.adventofcode.util.getInputFromLines
import java.util.*

class Day18 {
    val input = getInputFromLines(2020, 18)

    fun part1() {
        val results = input.map { evaluate(it) }
        val res = results.sum()
        println(res)
    }

    private fun evaluate(line: String): Long {
        val tokens = line.toCharArray().toList()
        val stack = LinkedList<Long>()
        val opstack = LinkedList<Char>()
        var res = 0L
        var op = '+'
        for (token in tokens) {
            when (token) {
                '+' -> op = '+'
                '*' -> op = '*'
                in '0'..'9' -> if (op == '+') res += ("" + token).toLong() else res *= ("" + token).toLong()
                '(' -> {
                    stack.push(res)
                    res = 0
                    opstack.push(op)
                    op = '+'
                }
                ')' -> {
                    op = opstack.pop()
                    if (op == '+') res = stack.pop() + res else res = stack.pop() * res
                }

            }
        }
        return res
    }


    fun part2() {
        val results = input.map { evaluate2(it) }
        val res = results.sum()
        println(res)
    }

    private fun evaluate2(line: String): Long {
        val tokens = line.toCharArray().toList().filter{it!=' '}
        val queue: Queue<Char> = LinkedList<Char>()
        val opstack: Stack<Char> = Stack<Char>()
        var res = 0L
        var op = '+'
        val operators = mapOf('+' to 1, '*' to 0, '(' to 0, ')' to 0)
        for (token in tokens) {
            when (token) {
                in '0'..'9' -> queue.add(token)
                '(' -> opstack.push(token)
                ')' -> {
                    while (opstack.peek() != '(') {
                        queue.add(opstack.pop())
                    }
                    if (opstack.peek() == '(') {
                        opstack.pop()
                    }
                    /* if function at top of operator stack then pop function to output*/
                }
                in operators -> {
                    while (opstack.isNotEmpty() && opstack.peek() in operators
                            && ((operators[opstack.peek()]!! > operators[token]!!) || (operators[opstack.peek()]!! == operators[token]!! /* && token is left associative */))
                            && (opstack.peek() != '(')) {
                        queue.add(opstack.pop())
                    }
                    opstack.push(token)
                }
            }
        }
        while (opstack.isNotEmpty()) {
            queue.add(opstack.pop())
        }
        val resstack: Stack<Long> = Stack()
        for (token in queue) {
            when (token) {
                in '0'..'9' -> resstack.push(token.toString().toLong())
                '*' -> resstack.push(resstack.pop() * resstack.pop())
                '+' -> resstack.push(resstack.pop() + resstack.pop())
            }
        }
        return resstack.pop()
    }


}

fun main(args: Array<String>) {
    val d = Day18()
    d.part1()
    d.part2()
}
