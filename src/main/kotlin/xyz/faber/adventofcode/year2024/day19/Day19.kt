package xyz.faber.adventofcode.year2024.day19

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.split

class Day19 : AdventSolution<Long>() {
    fun isPossible(pattern: String, towels: List<String>): Boolean {
        if(pattern.isEmpty()){
            return true
        }
        for(towel in towels){
            if(pattern.startsWith(towel)){
                if(isPossible(pattern.substring(towel.length), towels)){
                    return true
                }
            }
        }
        return false
    }

    override fun part1(input: List<String>): Long {
        val split = input.split(listOf(""))
        val towels = split[0].joinToString("").split(", ")
        val patterns = split[1]
        val possiblePatterns = patterns.filter{isPossible(it, towels)}
        return possiblePatterns.count().toLong()
    }

    fun countPossible(pattern: String, towels: List<String>, memo: MutableMap<String, Long>): Long {
        if(pattern.isEmpty()){
            return 1
        }
        if(memo.containsKey(pattern)){
            return memo[pattern]!!
        }
        var res = 0L
        for(towel in towels){
            if(pattern.startsWith(towel)){
                res += countPossible(pattern.substring(towel.length), towels, memo)
            }
        }
        memo[pattern] = res
        return res
    }

    override fun part2(input: List<String>): Long {
        val split = input.split(listOf(""))
        val towels = split[0].joinToString("").split(", ")
        val patterns = split[1]
        val memo = mutableMapOf<String, Long>()
        val possiblePatternCounts = patterns.map{countPossible(it, towels, memo)}
        return possiblePatternCounts.sum()
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 19, Day19()).run()
}