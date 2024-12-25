package xyz.faber.adventofcode.util

import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class AdventRunner<T>(val year: Int, val day: Int, val solution: BaseAdventSolution<T>) {
  fun run() {
    var partsCompleted: Int;
    try {
      runPart2Empty(solution)
      partsCompleted = 2
    } catch (e: Error) {
      partsCompleted = 1
    }
    val tests = getTests(year, day, partsCompleted)
    val input = getInput(year, day)
    println("Test data:")
    tests.withIndex().forEach { (i, test) ->
      try {
        solution.testdata = true
        run(solution, test, ignoreExceptions = true)
      } catch (e: Exception) {
      }
    }
    println("Real data:")
    solution.testdata = false
    run(solution, input)
  }

  private fun run(solution: BaseAdventSolution<T>, input: String, ignoreExceptions: Boolean = false) {
    when (solution) {
      is AdventSolution<T> -> run(solution, input, ignoreExceptions)
      is AdventSolutionWithTransform<T, *> -> run(solution, input, ignoreExceptions)
    }
  }

  private fun run(solution: AdventSolution<T>, input: String, ignoreExceptions: Boolean) {
    val str = input.let { solution.transform(it) }
    tryRun("Part 1", str, solution::part1, ignoreExceptions)
    tryRun("Part 2", str, solution::part2, ignoreExceptions)

    val lines = input.lines().dropLastWhile { it.isBlank() }.let { solution.transform(it) }
    tryRun("Part 1", lines, solution::part1, ignoreExceptions)
    tryRun("Part 2", lines, solution::part2, ignoreExceptions)

    val csv = input.split(",").let { solution.transform(it) }
    tryRun("Part 1", csv, solution::part1Csv, ignoreExceptions)
    tryRun("Part 2", csv, solution::part2Csv, ignoreExceptions)
  }

  private fun <I> run(solution: AdventSolutionWithTransform<T, I>, input: String, ignoreExceptions: Boolean) {
    var transformed = input.let { solution.transformAll(it) }
    if (transformed != null) {
      tryRun("Part 1", transformed, solution::part1, ignoreExceptions)
      transformed = input.let { solution.transformAll(it) }!!
      tryRun("Part 2", transformed, solution::part2, ignoreExceptions)
    }

    val rawLines = input.lines().dropLastWhile { it.isBlank() }
    var transformed2 = solution.transformAll(rawLines)
    if (transformed2 != null) {
      tryRun("Part 1", transformed2, solution::part1, ignoreExceptions)
      transformed2 = solution.transformAll(rawLines)!!
      tryRun("Part 2", transformed2, solution::part2, ignoreExceptions)
    }

    var lines = rawLines
      .map { solution.transformLine(it) }
      .filterNotNull()
    if (lines.isNotEmpty()) {
      tryRun("Part 1", lines, solution::part1, ignoreExceptions)
      lines = rawLines
        .map { solution.transformLine(it) }
        .filterNotNull()
      tryRun("Part 2", lines, solution::part2, ignoreExceptions)
    }
  }

  private fun runPart2Empty(solution: BaseAdventSolution<T>) {
    when (solution) {
      is AdventSolution<T> -> {
        runPart2Empty(solution)
      }

      is AdventSolutionWithTransform<T, *> -> {
        runPart2Empty(solution)
      }
    }
  }

  private fun runPart2Empty(solution: AdventSolution<T>) {
    try {
      solution.part2("")
    } catch (e: NotImplementedError) {
      throw e
    } catch (e: Exception) {
      // ignore
    }
    try {
      solution.part2(emptyList<String>())
    } catch (e: NotImplementedError) {
      throw e
    } catch (e: Exception) {
      // ignore
    }
    try {
      solution.part2(emptyList<Int>())
    } catch (e: NotImplementedError) {
      throw e
    } catch (e: Exception) {
      // ignore
    }
    try {
      solution.part2Csv(emptyList())
    } catch (e: NotImplementedError) {
      throw e
    } catch (e: Exception) {
      // ignore
    }
  }

  private fun <I> runPart2Empty(solution: AdventSolutionWithTransform<T, I>) {
    try {
      val t = solution.transformAll(" ")
      if (t != null) {
        solution.part2(t)
      }
    } catch (e: NotImplementedError) {
      throw e
    } catch (e: Exception) {
      // ignore
    }
    try {
      solution.part2(emptyList())
    } catch (e: NotImplementedError) {
      throw e
    } catch (e: Exception) {
      // ignore
    }
  }


  @OptIn(ExperimentalTime::class)
  fun <I> tryRun(part: String, input: I, runFunc: (I) -> (T?), ignoreExceptions: Boolean) {
    val (res, elapsed) = measureTimedValue {
      try {
        runFunc(input)
      } catch (e: NotImplementedError) {
        null
      } catch (e: Error) {
        if (ignoreExceptions) {
          null
        } else {
          throw e
        }
      }
    }
    if (res != null) {
      println("$part: $res (${elapsed})")
    }
  }
}

sealed class BaseAdventSolution<T> {
  var testdata = false
}

abstract class AdventSolution<T> : BaseAdventSolution<T>() {
  open fun transform(input: String) = input

  open fun transform(input: List<String>) = input

  @Suppress("INAPPLICABLE_JVM_NAME")
  @JvmName("transformInts")
  open fun transform(input: List<Int>) = input

  open fun part1(input: String): T? {
    return null
  }

  open fun part2(input: String): T? {
    return null
  }

  open fun part1(input: List<String>): T? {
    return null
  }

  open fun part2(input: List<String>): T? {
    return null
  }

  open fun part1Csv(input: List<String>): T? {
    return null
  }

  open fun part2Csv(input: List<String>): T? {
    return null
  }

  @Suppress("INAPPLICABLE_JVM_NAME")
  @JvmName("part1Ints")
  open fun part1(input: List<Int>): T? {
    return null
  }

  @Suppress("INAPPLICABLE_JVM_NAME")
  @JvmName("part2Ints")
  open fun part2(input: List<Int>): T? {
    return null
  }
}

abstract class AdventSolutionWithTransform<T, I> : BaseAdventSolution<T>() {
  open fun transformLine(input: String): I? {
    return null
  }

  @Suppress("INAPPLICABLE_JVM_NAME")
  @JvmName("transform3")
  open fun transformAll(input: String): I? {
    return null
  }

  open fun transformAll(input: List<String>): I? {
    return null
  }

  open fun part1(input: I): T? {
    return null
  }

  open fun part2(input: I): T? {
    return null
  }

  open fun part1(input: List<I>): T? {
    return null
  }

  open fun part2(input: List<I>): T? {
    return null
  }
}

private class TestSolution : AdventSolution<String>() {
  override fun part1(input: List<String>): String {
    return input[0]
  }
}

fun main() {
  AdventRunner(
    2021, 2, TestSolution()
  ).run()
}
