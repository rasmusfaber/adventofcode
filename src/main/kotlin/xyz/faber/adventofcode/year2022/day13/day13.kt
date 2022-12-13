package xyz.faber.adventofcode.year2022.day13

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.split
import java.lang.RuntimeException
import kotlin.math.min

class Day13 : AdventSolution<Int>() {
    fun compare(left: JsonElement, right: JsonElement): Int {
        if (left is JsonPrimitive && right is JsonPrimitive) {
            return Integer.compare(left.content.toInt(), right.content.toInt())
        } else if (left is JsonArray && right is JsonArray) {
            for (i in 0 until min(left.size, right.size)) {
                val compare = compare(left[i], right[i])
                if (compare != 0) {
                    return compare
                }
            }
            return Integer.compare(left.size, right.size)
        } else if (left is JsonPrimitive && right is JsonArray) {
            return compare(JsonArray(listOf(left)), right)
        } else if (left is JsonArray && right is JsonPrimitive) {
            return compare(left, JsonArray(listOf(right)))
        } else {
            throw RuntimeException("Unexpected")
        }
    }

    override fun part1(input: List<String>): Int {
        val packets = input.split(listOf(""))
            .map { Json.decodeFromString<JsonArray>(it[0]) to Json.decodeFromString<JsonArray>(it[1]) }
        return packets.withIndex()
            .filter { compare(it.value.first, it.value.second) < 0 }
            .sumOf { it.index + 1 }
    }

    override fun part2(input: List<String>): Int {
        val div1 = Json.decodeFromString<JsonArray>("[[2]]")
        val div2 = Json.decodeFromString<JsonArray>("[[6]]")
        val packets = input.filter { it.isNotEmpty() }
            .map { Json.decodeFromString<JsonArray>(it) } + listOf(div1, div2)

        val sorted = packets.sortedWith { a, b -> compare(a, b) }

        val index1 = sorted.indexOf(div1)
        val index2 = sorted.indexOf(div2)

        return (index1 + 1) * (index2 + 1)
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 13, Day13()).run()

}
