package xyz.faber.adventofcode.year2020.day19

import xyz.faber.adventofcode.util.getInput
import java.lang.Integer.min

class Day19 {
    val input = getInput(2020, 19).split("\n\n")
    /*val input = ("42: 9 14 | 10 1\n" +
            "9: 14 27 | 1 26\n" +
            "10: 23 14 | 28 1\n" +
            "1: \"a\"\n" +
            "11: 42 31\n" +
            "5: 1 14 | 15 1\n" +
            "19: 14 1 | 14 14\n" +
            "12: 24 14 | 19 1\n" +
            "16: 15 1 | 14 14\n" +
            "31: 14 17 | 1 13\n" +
            "6: 14 14 | 1 14\n" +
            "2: 1 24 | 14 4\n" +
            "0: 8 11\n" +
            "13: 14 3 | 1 12\n" +
            "15: 1 | 14\n" +
            "17: 14 2 | 1 7\n" +
            "23: 25 1 | 22 14\n" +
            "28: 16 1\n" +
            "4: 1 1\n" +
            "20: 14 14 | 1 15\n" +
            "3: 5 14 | 16 1\n" +
            "27: 1 6 | 14 18\n" +
            "14: \"b\"\n" +
            "21: 14 1 | 1 14\n" +
            "25: 1 1 | 1 14\n" +
            "22: 14 14\n" +
            "8: 42\n" +
            "26: 14 22 | 1 20\n" +
            "18: 15 15\n" +
            "7: 14 5 | 1 21\n" +
            "24: 14 1\n" +
            "\n" +
            "abbbbbabbbaaaababbaabbbbabababbbabbbbbbabaaaa\n" +
            "bbabbbbaabaabba\n" +
            "babbbbaabbbbbabbbbbbaabaaabaaa\n" +
            "aaabbbbbbaaaabaababaabababbabaaabbababababaaa\n" +
            "bbbbbbbaaaabbbbaaabbabaaa\n" +
            "bbbababbbbaaaaaaaabbababaaababaabab\n" +
            "ababaaaaaabaaab\n" +
            "ababaaaaabbbaba\n" +
            "baabbaaaabbaaaababbaababb\n" +
            "abbbbabbbbaaaababbbbbbaaaababb\n" +
            "aaaaabbaabaaaaababaa\n" +
            "aaaabbaaaabbaaa\n" +
            "aaaabbaabbaaaaaaabbbabbbaaabbaabaaa\n" +
            "babaaabbbaaabaababbaabababaaab\n" +
            "aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba").split("\n\n")*/
    val ruleLines = input[0].lines().filter { it.isNotBlank() }
    val rules = ruleLines.map { it.split(": ") }.map { it[0].toInt() to it[1] }.toMap()
    val messages = input[1].lines().filter { it.isNotBlank() }

    fun part1() {
        val res = messages.filter { match(rules, it, 0, 0) == it.length }
        println(res)
        println(res.size)
    }

    private fun match(rules: Map<Int, String>, s: String, index: Int, ruleIndex: Int): Int {
        val rule = rules[ruleIndex]!!
        return match(rules, s, index, rule)
    }

    private fun match(rules: Map<Int, String>, s: String, index: Int, rule: String): Int {
        if (rule[0] == '"') {

            val c = rule[1]
            //for(j in index until s.length){
            val j = index
            if (j < s.length && s[j] == c) {
                return j + 1
            }
            //}
            return 1000
        }
        val ors = rule.split(" | ")
        var res = 1000
        for (or in ors) {
            val subrules = or.split(" ").map { it.toInt() }
            var j = index
            for (subrule in subrules) {
                j = match(rules, s, j, subrule)
                if (j == 1000) break
            }
            res = min(j, res)
        }
        return res
    }

    private fun match2(rules: Map<Int, String>, s: String, index: Int, ruleIndex: Int): Pair<Int, String> {
        val rule = rules[ruleIndex]!!
        if (rule[0] == '"') {
            val c = rule[1]
            //for(j in index until s.length){
            val j = index
            if (j < s.length && s[j] == c) {
                return j + 1 to "$ruleIndex=$c"
            }
            //}
            return 1000 to ""
        }
        val ors = rule.split(" | ")
        var best = 1000
        var bestrules = ""
        var bestor = ""
        for (or in ors) {
            val subrules = or.split(" ").map { it.toInt() }
            var j = index
            var theserules = ""
            for (subrule in subrules) {
                val (j2, r) = match2(rules, s, j, subrule)
                j = j2
                theserules += " " + r
                if (j == 1000) break
            }
            if (j < best) {
                best = j
                bestor = or
                bestrules = theserules
            }
        }
        return best to bestor + " ( " + bestrules + ")"
    }

    fun match3(rules: Map<Int, String>, s: String): Boolean {
        for (i in 2..20) {
            val rule = "42 ".repeat(i) + "31"
            if (s.length in match4(rules, s, 0, rule)) return true
        }
        return false
    }

    private fun match4(rules: Map<Int, String>, s: String, index: Int, ruleIndex: Int): Set<Int> {
        return match4(rules, s, index, rules[ruleIndex]!!)
    }

    private fun match4(rules: Map<Int, String>, s: String, index: Int, rule: String): Set<Int> {
        if (rule[0] == '"') {

            val c = rule[1]
            //for(j in index until s.length){
            val j = index
            if (j < s.length && s[j] == c) {
                return setOf(j + 1)
            }
            //}
            return emptySet()
        }
        val ors = rule.split(" | ")
        var res = mutableSetOf<Int>()
        for (or in ors) {
            val subrules = or.split(" ").map { it.toInt() }
            val first = match4(rules, s, index, subrules[0])
            if (subrules.size == 1) {
                res.addAll(first)
            } else if (subrules.size == 2) {
                for (f in first) {
                    res.addAll(match4(rules, s, f, subrules[1]))
                }
            } else {
                for (f in first) {
                    val second = match4(rules, s, f, subrules[1])
                    for (sec in second) {
                        res.addAll(match4(rules, s, sec, subrules[2]))
                    }
                }
            }
        }
        return res
    }


    fun part2() {
        rules.entries.sortedBy { it.key }.forEach { println(it.key.toString() + ": " + it.value) }

        val rules2 = rules.toMutableMap()
        rules2[8] = "42 | 42 8"
        rules2[11] = "42 31 | 42 11 31"
        println(possibleValues(rules2, 42))
        println(possibleValues(rules2, 31))
        println(match4(rules2, "babbbbaabbbbbabbbbbbaabaaabaaa", 0, 0))
        val res = messages.filter { it.length in match4(rules2, it, 0, 0) }
        println(res)
        println(res.size) // not 367, 321 too low
    }

    fun possibleValues(rules: Map<Int, String>, ruleIndex: Int): Set<String> {
        val rule = rules[ruleIndex]!!
        if (rule[0] == '\"') {
            return setOf(rule.substring(1, 2))
        }
        val ors = rule.split(" | ")
        val res = mutableSetOf<String>()
        for (or in ors) {
            val subrules = or.split(" ").map { it.toInt() }
            if (subrules.size == 1) {
                res.addAll(possibleValues(rules, subrules[0]))
            } else {
                val first = possibleValues(rules, subrules[0])
                val second = possibleValues(rules, subrules[1])
                for (f in first) {
                    for (s in second) {
                        res.add(f + s)
                    }
                }
            }
        }
        return res
    }

}

fun main(args: Array<String>) {
    val d = Day19()
    // d.part1()
    d.part2()
}
