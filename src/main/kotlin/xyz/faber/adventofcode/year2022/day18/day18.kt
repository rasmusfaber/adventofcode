package xyz.faber.adventofcode.year2022.day18

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.Pos3D
import xyz.faber.adventofcode.util.manhattanDistance
import java.util.LinkedList

class Day18 : AdventSolution<Int>() {
    override fun part1(input: List<String>): Int {
        val cubes = input.map { it.split(',').map { it.toInt() } }.map { (x, y, z) -> Pos3D(x, y, z) }.toSet()
        val surfaceArea = cubes.sumOf { cube1 ->
            6 - cubes.count { cube2 -> manhattanDistance(cube1, cube2) == 1 }
        }
        return surfaceArea
    }

    override fun part2(input: List<String>): Int {
        val cubes = input.map { it.split(',').map { it.toInt() } }.map { (x, y, z) -> Pos3D(x, y, z) }.toSet()
        val minx = cubes.minOf { it.x }
        val maxx = cubes.maxOf { it.x }
        val miny = cubes.minOf { it.y }
        val maxy = cubes.maxOf { it.y }
        val minz = cubes.minOf { it.z }
        val maxz = cubes.maxOf { it.z }
        val air = floodfill(minx - 1, maxx + 1, miny - 1, maxy + 1, minz - 1, maxz + 1, cubes)
        val surfaceArea = cubes.sumOf { cube1 ->
            air.count { aircell -> manhattanDistance(cube1, aircell) == 1 }
        }
        return surfaceArea
    }

    private fun floodfill(minx: Int, maxx: Int, miny: Int, maxy: Int, minz: Int, maxz: Int, cubes: Set<Pos3D>): Set<Pos3D> {
        val fill = mutableSetOf<Pos3D>()
        val stack = LinkedList<Pos3D>()
        stack.push(Pos3D(minx, miny, minz))
        while(!stack.isEmpty()){
            val pos = stack.pop()
            if (pos in cubes) continue
            if (pos in fill) continue
            if (pos.x !in minx..maxx || pos.y !in miny..maxy || pos.z !in minz..maxz) continue
            fill.add(pos)
            stack.push(pos + Pos3D(-1, 0, 0))
            stack.push(pos + Pos3D(1, 0, 0))
            stack.push(pos + Pos3D(0, -1, 0))
            stack.push(pos + Pos3D(0, 1, 0))
            stack.push(pos + Pos3D(0, 0, -1))
            stack.push(pos + Pos3D(0, 0, 1))
        }
        return fill
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 18, Day18()).run()

}
