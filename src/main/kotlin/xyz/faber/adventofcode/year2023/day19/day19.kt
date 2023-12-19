package xyz.faber.adventofcode.year2023.day19

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.split

class Day19 : AdventSolution<Long>() {
  data class Part(val values: List<Int>)

  fun parsePart(s: String): Part {
    val values = s.substring(1, s.length - 1).split(',').map { it.substring(2).toInt() }
    return Part(values)
  }

  data class PartRange(val ranges: List<IntRange>) {
    fun below(categoryIndex: Int, value: Int): PartRange {
      val range = ranges[categoryIndex]
      val newRange = if (value in range) {
        IntRange(range.first, value - 1)
      } else if (value >= range.last) {
        range
      } else {
        IntRange.EMPTY
      }
      return PartRange(ranges.toMutableList().apply { this[categoryIndex] = newRange })
    }

    fun above(categoryIndex: Int, value: Int): PartRange {
      val range = ranges[categoryIndex]
      val newRange = if (value in range) {
        IntRange(value + 1, range.last)
      } else if (value <= range.first) {
        range
      } else {
        IntRange.EMPTY
      }
      return PartRange(ranges.toMutableList().apply { this[categoryIndex] = newRange })
    }

    fun combinations() =
      (ranges[0].last - ranges[0].first + 1L) * (ranges[1].last - ranges[1].first + 1L) * (ranges[2].last - ranges[2].first + 1L) * (ranges[3].last - ranges[3].first + 1L)

    companion object {
      val EMPTY: PartRange = PartRange(listOf(IntRange.EMPTY, IntRange.EMPTY, IntRange.EMPTY, IntRange.EMPTY))
    }

  }

  data class Rule(val categoryIndex: Int, val comparision: Char, val rhs: Int, val res: String) {
    fun test(part: Part): Boolean {
      if (categoryIndex == -1) {
        return true
      }
      val lhs = part.values[categoryIndex]
      return when (comparision) {
        '<' -> lhs < rhs
        '>' -> lhs > rhs
        else -> throw IllegalArgumentException("Bad comparison " + comparision)
      }
    }

    fun splitBy(partRange: PartRange): Pair<PartRange, PartRange> {
      if (categoryIndex == -1) {
        return partRange to PartRange.EMPTY
      }
      return when (comparision) {
        '<' -> partRange.below(categoryIndex, rhs) to partRange.above(categoryIndex, rhs - 1)
        '>' -> partRange.above(categoryIndex, rhs) to partRange.below(categoryIndex, rhs + 1)
        else -> throw IllegalArgumentException("Bad comparison " + comparision)
      }
    }
  }

  fun parseRule(s: String): Rule {
    if (!s.contains(':')) {
      return Rule(-1, ' ', 0, s)
    }
    val category = s[0]
    val categoryIndex = "xmas".indexOf(category)
    val comparision = s[1]
    val split = s.substring(2).split(':')
    val rhs = split[0].toInt()
    val res = split[1]
    return Rule(categoryIndex, comparision, rhs, res)
  }

  data class Workflow(val name: String, val rules: List<Rule>)

  fun parseWorkflow(s: String): Workflow {
    val (name, rulesString) = s.split('{', '}')
    val ruleStrings = rulesString.split(',')
    return Workflow(name, ruleStrings.map { parseRule(it) })
  }

  fun applyWorkflows(workflows: Map<String, Workflow>, part: Part): String {
    var workflowName = "in"
    while (workflowName != "A" && workflowName != "R") {
      val workflow = workflows[workflowName]!!
      workflowName = workflow.rules.first { it.test(part) }.res
    }
    return workflowName
  }

  fun applyWorkflows(workflows: Map<String, Workflow>, partRange: PartRange): Map<PartRange, String> {
    val queue = ArrayDeque<Pair<PartRange, String>>()
    queue += partRange to "in"
    val res = mutableMapOf<PartRange, String>()
    while (queue.isNotEmpty()) {
      val element = queue.removeFirst()
      val workflow = workflows[element.second]!!
      var remainingRange = element.first
      for (rule in workflow.rules) {
        val (t, f) = rule.splitBy(remainingRange)
        if (rule.res == "A" || rule.res == "R") {
          res += t to rule.res
        } else {
          queue += t to rule.res
        }
        remainingRange = f
      }
    }
    return res
  }

  override fun part1(input: List<String>): Long {
    val (workflowStrings, partStrings) = input.split(listOf(""))
    val workflows = workflowStrings.map { parseWorkflow(it) }.map { it.name to it }.toMap()
    val parts = partStrings.map { parsePart(it) }

    val acceptedParts = parts.filter { applyWorkflows(workflows, it) == "A" }
    return acceptedParts.sumOf { it.values.sum().toLong() }
  }

  override fun part2(input: List<String>): Long {
    val (workflowStrings) = input.split(listOf(""))
    val workflows = workflowStrings.map { parseWorkflow(it) }.map { it.name to it }.toMap()
    val ranges = applyWorkflows(workflows, PartRange(listOf(1..4000, 1..4000, 1..4000, 1..4000)))
    return ranges.entries.filter { it.value == "A" }.map { it.key }.sumOf { it.combinations() }
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 19, Day19()).run()

}
