package xyz.faber.adventofcode.year2024.day12

import xyz.faber.adventofcode.util.*

class Day12 : AdventSolution<Int>() {
    fun <V> detectRegions(map: XYMap<V>): Map<Pos, Int> {
        var regionCount = 0
        val res = mutableMapOf<Pos, Int>()
        for (pos in map.positions()) {
            if (res.containsKey(pos)) {
                continue
            }
            val region = regionCount
            regionCount++
            val queue = mutableListOf(pos)
            while (queue.isNotEmpty()) {
                val current = queue.removeAt(0)
                if (res.containsKey(current)) {
                    continue
                }
                res[current] = region
                val neighbors = current.adjacentNonDiagonal().filter{map.isInBounds(it)}
                for (neighbor in neighbors) {
                    if (res.containsKey(neighbor)) {
                        continue
                    }
                    if (map[neighbor] == map[current]) {
                        queue.add(neighbor)
                    }
                }
            }
        }
        return res
    }
    override fun part1(input: List<String>): Int {
        val map = input.toXYMap(' ', false)
        val regionMap = detectRegions(map)
        val regions = regionMap.values.toSet()
        val areas = regions.map{it to 0}.toMap().toMutableMap()
        val perimeters = regions.map{it to 0}.toMap().toMutableMap()
        for (pos in map.positions()) {
            val region = regionMap[pos]!!
            val neighbors = pos.adjacentNonDiagonal()
            for (neighbor in neighbors) {
                if (map[pos] != map[neighbor]) {
                    perimeters[region] = perimeters[region]!! + 1
                }
            }
            areas[region] = areas[region]!! + 1
        }
        return regions.sumOf{areas[it]!! * perimeters[it]!!}
    }

    override fun part2(input: List<String>): Int {
        val map = input.toXYMap(' ', false)
        val regionMap = detectRegions(map)
        val regions = regionMap.values.toSet()
        val areas = regions.map{it to 0}.toMap().toMutableMap()
        val corners = regions.map{it to 0}.toMap().toMutableMap()
        for (pos in map.positions()) {
            val region = regionMap[pos]!!
            val cornerChecks = pos.adjacentDiagonal()
            for (corner in cornerChecks) {
                if(map[pos] != map[pos.x, corner.y] && map[pos] != map[corner.x, pos.y]) {
                    // outer corner
                    corners[region] = corners[region]!! + 1
                }else if(map[pos] != map[corner] && map[pos] == map[pos.x, corner.y] && map[pos] == map[corner.x, pos.y]) {
                    // inner corner
                    corners[region] = corners[region]!! + 1
                }
            }
            areas[region] = areas[region]!! + 1
        }
        return regions.sumOf{areas[it]!! * corners[it]!!}
    }
}

fun main(args: Array<String>) {
    AdventRunner(2024, 12, Day12()).run()
}