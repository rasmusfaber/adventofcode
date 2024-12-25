package xyz.faber.adventofcode.year2024.day24

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.split

data class Rule(val input1: InputOrRule, val op: String, val input2: InputOrRule) : InputOrRule() {
    override fun toString(): String {
        return "($input1 $op $input2)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rule

        if (op != other.op) return false

        return (input1 == other.input1 && input2 == other.input2) || (input1 == other.input2 && input2 == other.input1)
    }

    override fun depth(): Int {
        return 1 + maxOf(input1.depth(), input2.depth())
    }

    override fun hashCode(): Int {
        var result = (input1.hashCode() + input2.hashCode())
        result = 31 * result + op.hashCode()
        return result
    }


}

data class Input(val value: String) : InputOrRule() {
    constructor(xy: Char, bit: Int) : this("$xy${bit.toString().padStart(2, '0')}")

    override fun toString(): String {
        return value
    }

    override fun depth(): Int = 1
}

sealed class InputOrRule {
    abstract fun depth(): Int
}

class Day24 : AdventSolution<String>() {
    private fun calculate(
        output: String,
        rules: Map<String, Triple<String, String, String>>,
        values: MutableMap<String, Boolean>
    ): Boolean {
        if (output in values) {
            return values[output]!!
        }
        val rule = rules[output]!!
        val input1 = calculate(rule.first, rules, values)
        val input2 = calculate(rule.third, rules, values)
        val res = when (rule.second) {
            "AND" -> input1 and input2
            "OR" -> input1 or input2
            "XOR" -> input1 xor input2
            else -> throw NotImplementedError()
        }
        values[output] = res
        return res
    }

    override fun part1(input: List<String>): String {
        val split = input.split(listOf(""))
        val values = split[0].map { it.split(": ") }.map { it[0] to (it[1].toInt() == 1) }.toMap().toMutableMap()
        val rules = split[1].map { it.split(" ") }.map { it[4] to Triple(it[0], it[1], it[2]) }.toMap()
        val outputs = rules.keys.filter { it.startsWith("z") }.sortedDescending()
        val res = outputs.map { calculate(it, rules, values) }
        return res.map { if (it) "1" else "0" }.joinToString("").toLong(2).toString()
    }

    private fun fullRule(
        output: String,
        rules: Map<String, Triple<String, String, String>>
    ): InputOrRule {
        val rule = rules[output]
        if (rule == null) {
            return Input(output)
        }
        val input1 = fullRule(rule.first, rules)
        val input2 = fullRule(rule.third, rules)
        if (input1 is Input && input2 is Input) {
            if (input1.value > input2.value)
                return Rule(input2, rule.second, input1)
            else
                return Rule(input1, rule.second, input2)
        }
        if (input1 is Input) {
            return Rule(input1, rule.second, input2)
        }
        if (input2 is Input) {
            return Rule(input2, rule.second, input1)
        }
        if (input1 !is Rule || input2 !is Rule) {
            throw NotImplementedError()
        }
        if (input1.depth() > input2.depth())
            return Rule(input2, rule.second, input1)
        return Rule(input1, rule.second, input2)
    }

    private fun carryRule(bit: Int): Rule {
        if (bit == 0) {
            return Rule(Input('x', 0), "AND", Input('y', 0))
        }
        return Rule(
            Rule(Input('x', bit), "AND", Input('y', bit)),
            "OR",
            Rule(Rule(Input('x', bit), "XOR", Input('y', bit)), "AND", carryRule(bit - 1))
        )
    }

    private fun carryRule(bit: Int, carries: Map<String, String>): Rule {
        if (bit == 0) {
            return Rule(Input('x', 0), "AND", Input('y', 0))
        }
        return Rule(
            Rule(Input('x', bit), "AND", Input('y', bit)),
            "OR",
            Rule(
                Rule(Input('x', bit), "XOR", Input('y', bit)),
                "AND",
                Input(carries["c${(bit - 1).toString().padStart(2, '0')}"]!!)
            )
        )
    }

    private fun addRule(bit: Int): Rule {
        if (bit == 0) {
            return Rule(Input("x00"), "XOR", Input("y00"))
        }
        return Rule(Input('x', bit), "XOR", Rule(Input('y', bit), "XOR", carryRule(bit - 1)))
        //return Rule(Input('x', bit), "XOR", Rule(Input('y', bit), "XOR", Input('c', bit - 1)))
    }

    private fun findRule(rule: InputOrRule, ruleMap: Map<Triple<String, String, String>, String>): String? {
        if (rule is Input) {
            return rule.value
        }
        if (rule !is Rule) {
            throw NotImplementedError()
        }
        val input1 = findRule(rule.input1, ruleMap)
        val input2 = findRule(rule.input2, ruleMap)
        if (input1 != null && input2 != null) {
            val foundRule = ruleMap[Triple(input1!!, rule.op, input2!!)]
            return foundRule
        }
        if (rule.input1 is Rule && rule.input1.op == rule.op && input2 != null) {
            val input11 = findRule(rule.input1.input1, ruleMap)
            val input12 = findRule(rule.input1.input2, ruleMap)
            if (input11 != null && input12 != null) {
                val inputx1 = findRule(Rule(rule.input1.input1, rule.op, rule.input2), ruleMap)
                if (inputx1 != null) {
                    val foundRule = ruleMap[Triple(input12, rule.op, inputx1)]
                    if (foundRule != null) {
                        return foundRule
                    }
                }
                val inputx2 = findRule(Rule(rule.input1.input2, rule.op, rule.input2), ruleMap)
                if (inputx1 != null) {
                    val foundRule = ruleMap[Triple(input11, rule.op, inputx2)]
                    if (foundRule != null) {
                        return foundRule
                    }
                }
            }

        }
        if (rule.input2 is Rule && rule.input2.op == rule.op && input1 != null) {
            val input21 = findRule(rule.input2.input1, ruleMap)
            val input22 = findRule(rule.input2.input2, ruleMap)
            if (input21 != null && input22 != null) {
                val inputx1 = findRule(Rule(rule.input2.input1, rule.op, rule.input1), ruleMap)
                if (inputx1 != null) {
                    val foundRule = ruleMap[Triple(input22, rule.op, inputx1)]
                    if (foundRule != null) {
                        return foundRule
                    }
                }
                val inputx2 = findRule(Rule(rule.input2.input2, rule.op, rule.input1), ruleMap)
                if (inputx1 != null) {
                    val foundRule = ruleMap[Triple(input21, rule.op, inputx2)]
                    if (foundRule != null) {
                        return foundRule
                    }
                }
            }

        }
        if (rule.input1 is Rule && rule.input1.op == rule.input1.op && rule.input2 is Rule && rule.input2.op == rule.input2.op) {
            val input11 = findRule(rule.input2.input1, ruleMap)
            val input12 = findRule(rule.input2.input2, ruleMap)
            val input21 = findRule(rule.input2.input1, ruleMap)
            val input22 = findRule(rule.input2.input2, ruleMap)
            if (input11 != null && input12 != null && input21 != null && input22 != null) {
                val input1121 = findRule(Rule(rule.input1.input1, rule.op, rule.input2.input1), ruleMap)
                val input1222 = findRule(Rule(rule.input1.input2, rule.op, rule.input2.input2), ruleMap)
                if (input1121 != null && input1222 != null) {
                    val foundRule = ruleMap[Triple(input1121, rule.op, input1222)]
                    if (foundRule != null) {
                        return foundRule
                    }
                }
                val input1122 = findRule(Rule(rule.input1.input1, rule.op, rule.input2.input2), ruleMap)
                val input1221 = findRule(Rule(rule.input1.input2, rule.op, rule.input2.input1), ruleMap)
                if (input1122 != null && input1221 != null) {
                    val foundRule = ruleMap[Triple(input1122, rule.op, input1221)]
                    if (foundRule != null) {
                        return foundRule
                    }
                }
            }
        }
        return null
    }


    private fun findCarries(ruleMap: Map<Triple<String, String, String>, String>): Map<String, String> {
        var i = 0
        val carries = mutableMapOf<String, String>()
        while (true) {
            val expectedRule = carryRule(i, carries)
            val output = "c${i.toString().padStart(2, '0')}"
            val foundRule = findRule(expectedRule, ruleMap)
            if (foundRule == null) {
                break
            }
            carries[output] = foundRule
            i++
        }
        return carries
    }

    override fun part2(input: List<String>): String {
        val switch = mapOf(
            "z05" to "gdd",
            "gdd" to "z05",
            "z09" to "cwt",
            "cwt" to "z09",
            "css" to "jmv",
            "jmv" to "css",
            "z37" to "pqt",
            "pqt" to "z37"
        ).toMap()
        val split = input.split(listOf(""))
        val values = split[0].map { it.split(": ") }.map { it[0] to (it[1].toInt() == 1) }.toMap().toMutableMap()
        val originalRules = split[1].map { it.split(" ") }.map { it[4] to Triple(it[0], it[1], it[2]) }.toMap()
        val rules = originalRules.map { switch.getOrDefault(it.key, it.key) to it.value }.toMap()
        val ruleMap = (rules.entries.map { it.value to it.key } + rules.entries.map {
            Triple(
                it.value.third,
                it.value.second,
                it.value.first
            ) to it.key
        }).toMap()
        val parsedRules =
            rules.map { it.key to Rule(Input(it.value.first), it.value.second, Input(it.value.third)) }.toMap()
        val outputs = rules.keys.filter { it.startsWith("z") }.sorted()

        outputs.withIndex().forEach {
            val fullRule = fullRule(it.value, rules)
            //println("${it.value} = $fullRule")
            val expectedRule = addRule(it.index)
            //println("${it.value} = $expectedRule")
            val foundRule = findRule(expectedRule, ruleMap)
            //println(foundRule)
        }
        return switch.keys.sorted().joinToString(",")
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 24, Day24()).run()
}