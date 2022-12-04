package xyz.faber.adventofcode.util

class AdventRunner<T>(val year: Int, val day: Int, val solution: BaseAdventSolution<T>) {
    fun run() {
        val test = getTest(year, day)
        val input = getInput(year, day)
        if (test != null) {
            println("Test data:")
            run(solution, test)
        }
        println("Real data:")
        run(solution, input)
    }

    private fun run(solution: BaseAdventSolution<T>, input: String) {
        return when(solution){
            is AdventSolution<T> -> run(solution, input)
            is AdventSolutionWithTransform<T, *> -> run(solution, input)
        }
    }

    private fun run(solution: AdventSolution<T>, input: String) {
        val str = input.let { solution.transform(it) }
        tryRun("Part 1", str, solution::part1)
        tryRun("Part 2", str, solution::part2)

        val lines = input.lines().dropLastWhile { it.isBlank() }.let { solution.transform(it) }
        tryRun("Part 1", lines, solution::part1)
        tryRun("Part 2", lines, solution::part2)

        val csv = input.split(",").let { solution.transform(it) }
        tryRun("Part 1", csv, solution::part1Csv)
        tryRun("Part 2", csv, solution::part2Csv)
    }

    private fun <I> run(solution: AdventSolutionWithTransform<T, I>, input: String) {
        val transformed = input.let { solution.transformAll(it) }
        if (transformed != null) {
            tryRun("Part 1", transformed, solution::part1)
            tryRun("Part 2", transformed, solution::part2)
        }

        val lines: List<I> = input.lines().dropLastWhile { it.isBlank() }
            .map { solution.transformLine(it) }
            .filterNotNull()
        tryRun("Part 1", lines, solution::part1)
        tryRun("Part 2", lines, solution::part2)
    }

    fun <I> tryRun(part: String, input: I, runFunc: (I) -> (T?)) {
        val res = runFunc(input)
        if (res != null) {
            println("$part: $res")
        }
    }
}

sealed interface BaseAdventSolution<T>

abstract class AdventSolution<T>: BaseAdventSolution<T> {
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

abstract class AdventSolutionWithTransform<T, I>: BaseAdventSolution<T> {
    open fun transformLine(input: String): I? {
        return null
    }

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("transform3")
    open fun transformAll(input: String): I? {
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
