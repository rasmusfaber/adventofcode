package xyz.faber.adventofcode.year2022.day19

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.year2021.day16.productOf
import kotlin.math.max


data class Vector4(val v1: Int, val v2: Int, val v3: Int, val v4: Int) {
    operator fun plus(other: Vector4): Vector4 = Vector4(this.v1 + other.v1, this.v2 + other.v2, this.v3 + other.v3, this.v4 + other.v4)

    operator fun minus(other: Vector4): Vector4 = Vector4(this.v1 - other.v1, this.v2 - other.v2, this.v3 - other.v3, this.v4 - other.v4)

    operator fun times(scalar: Int): Vector4 = Vector4(this.v1 * scalar, this.v2 * scalar, this.v3 * scalar, this.v4 * scalar)
    fun lessThan(other: Vector4): Boolean {
        return other.v1 >= this.v1 && other.v2 >= this.v2 && other.v3 >= this.v3 && other.v4 >= this.v4
    }

}

fun max(a: Vector4, b: Vector4): Vector4 = Vector4(max(a.v1, b.v1), max(a.v2, b.v2), max(a.v3, b.v3), max(a.v4, b.v4))

data class Blueprint(val id: Int, val oreRobotCost: Vector4, val clayRobotCost: Vector4, val obsidianRobotCost: Vector4, val geodeRobotCost: Vector4) {
    val maxCost = max(oreRobotCost, max(clayRobotCost, max(obsidianRobotCost, geodeRobotCost)))
    override fun hashCode(): Int {
        return id
    }
}

class Day19 : AdventSolution<Int>() {
    val regex =
        "Blueprint (.*): Each ore robot costs (.*) ore. Each clay robot costs (.*) ore. Each obsidian robot costs (.*) ore and (.*) clay. Each geode robot costs (.*) ore and (.*) obsidian.".toRegex()

    fun parse(input: String): Blueprint = regex.find(input)!!.destructured.let { (id, c1, c2, c3, c4, c5, c6) ->
        Blueprint(
            id.toInt(),
            Vector4(c1.toInt(), 0, 0, 0),
            Vector4(c2.toInt(), 0, 0, 0),
            Vector4(c3.toInt(), c4.toInt(), 0, 0),
            Vector4(c5.toInt(), 0, c6.toInt(), 0)
        )
    }

    fun parse(input: List<String>): List<Blueprint> = input.map { parse(it) }

    fun maxGeodes(blueprint: Blueprint, maxTime: Int): Int {
        var robots = Vector4(1, 0, 0, 0)
        var materials = Vector4(0, 0, 0, 0)
        val cache = mutableMapOf<CacheKey, Int>()
        val res = maxGeodes(blueprint, 1, maxTime, robots, materials, cache)
        return res
    }

    data class CacheKey(val blueprint: Blueprint, val time: Int, val robots: Vector4, val materials: Vector4)

    fun maxGeodes(blueprint: Blueprint, time: Int, maxTime: Int, robots: Vector4, materials: Vector4, cache: MutableMap<CacheKey, Int>): Int {
        if (time == maxTime) return robots.v4 + materials.v4
        if ((blueprint.geodeRobotCost * (maxTime - time)).lessThan(robots * (maxTime - time - 1) + materials)) {
            return (maxTime - time) * (maxTime - time + 1) / 2 + materials.v4 + (maxTime - time + 1) * robots.v4
        }

        if (time < maxTime - 4) {
            val cacheKey = CacheKey(blueprint, time, robots, materials)
            val cached = cache[cacheKey]
            if (cached != null) {
                return cached
            } else {
                val computed = maxGeodesNoCache(blueprint, materials, time, maxTime, robots, cache)
                cache[cacheKey] = computed
                return computed
            }
        }

        return maxGeodesNoCache(blueprint, materials, time, maxTime, robots, cache)
    }

    private fun maxGeodesNoCache(
        blueprint: Blueprint,
        materials: Vector4,
        time: Int,
        maxTime: Int,
        robots: Vector4,
        cache: MutableMap<CacheKey, Int>
    ): Int {
        var best = 0
        if (blueprint.geodeRobotCost.lessThan(materials)) {
            return maxGeodes(blueprint, time + 1, maxTime, robots + Vector4(0, 0, 0, 1), materials - blueprint.geodeRobotCost + robots, cache)
        }
        val checkObsidian = robots.v3 < blueprint.maxCost.v3 && blueprint.obsidianRobotCost.lessThan(materials)
        if (checkObsidian) {
            best = max(best, maxGeodes(blueprint, time + 1, maxTime, robots + Vector4(0, 0, 1, 0), materials - blueprint.obsidianRobotCost + robots, cache))
        }
        val checkOre = robots.v1 < blueprint.maxCost.v1 && blueprint.oreRobotCost.lessThan(materials)
        if (checkOre) {
            best = max(best, maxGeodes(blueprint, time + 1, maxTime, robots + Vector4(1, 0, 0, 0), materials - blueprint.oreRobotCost + robots, cache))
        }
        val checkClay = robots.v2 < blueprint.maxCost.v2 && blueprint.clayRobotCost.lessThan(materials)
        if (checkClay) {
            best = max(best, maxGeodes(blueprint, time + 1, maxTime, robots + Vector4(0, 1, 0, 0), materials - blueprint.clayRobotCost + robots, cache))
        }
        if(!checkOre || !checkClay){
            best = max(best, maxGeodes(blueprint, time + 1, maxTime, robots, materials + robots, cache))
        }
        return best
    }

    override fun part1(input: List<String>): Int {
        val blueprints = parse(input)
        val blueprintsAndMaxGeodes = blueprints.associateWith { maxGeodes(it, 24) }
        return blueprintsAndMaxGeodes.entries.sumOf { it.key.id * it.value }
    }

    override fun part2(input: List<String>): Int {
        val blueprints = parse(input).take(3)
        val blueprintsAndMaxGeodes = blueprints.associateWith { maxGeodes(it, 32) }
        return blueprintsAndMaxGeodes.entries.productOf { it.value.toLong() }.toInt()
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 19, Day19()).run()

}
