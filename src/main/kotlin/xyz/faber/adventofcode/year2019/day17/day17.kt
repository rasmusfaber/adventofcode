package xyz.faber.adventofcode.year2019.day17

import adventofcode.year2019.intcode.Machine
import xyz.faber.adventofcode.util.*

class Day17 {
    val input = getInputLongsFromCsv(2019, 17)

    fun part1() {
        val map = getMap()
        val res = map.positions().filter { map[it] == '#' && it.adjacentNonDiagonal().all { map[it] == '#' } }
                .sumBy { it.x * it.y }
        println(res)
    }

    private fun getMap(): XYMap<Char> {
        val machine = Machine(input)

        val sb = StringBuilder()
        machine.sendOutput = { sb.append(it.toChar()) }
        machine.run()
        val output = sb.toString()

        return output.toXYMap()
    }

    fun findSubFunctions(input: List<List<String>>, maxFunctionLength: Int, maxSubFunctions: Int): List<List<String>>? {
        if (input.isEmpty()) {
            return emptyList()
        }
        if (maxSubFunctions == 0) {
            return null
        }
        for (i in 1..input[0].size) {
            val candidate = input[0].subList(0, i)
            if (candidate.joinToString(",").length > maxFunctionLength) {
                break;
            }
            val fragments = input.flatMap { it.split(candidate) }.filter { it.isNotEmpty() }
            val res = findSubFunctions(fragments, maxFunctionLength, maxSubFunctions - 1)
            if (res != null) {
                return res.plusAt(0, candidate)
            }
        }
        return null;
    }

    fun part2() {
        val map = getMap()
        var path = makeSimplePath(map)
        val res = findSubFunctions(listOf(path), 20, 3)
        val (a, b, c) = res!!
        path = path.replace(a, listOf("A"))
        path = path.replace(b, listOf("B"))
        path = path.replace(c, listOf("C"))

        val machine = Machine(input)
        machine.mem[0] = 2
        machine.sendAsciiString(path.joinToString(",") + "\n")
        machine.sendAsciiString(a.joinToString(",") + "\n")
        machine.sendAsciiString(b.joinToString(",") + "\n")
        machine.sendAsciiString(c.joinToString(",") + "\n")
        machine.sendAsciiString("n\n")

        machine.sendOutput = {print(it.toChar())}
        machine.run()

        println(machine.lastOutput)

    }

    fun makeSimplePath(map: XYMap<Char>): List<String> {
        val start = map.positions().filter { map[it] == '^' }.single()
        var direction = Direction.N
        var pos = start
        val visited = mutableSetOf<Pos>()
        val res = mutableListOf<String>()
        var forwardCount = 0
        while (true) {
            if (map[pos + direction] == '#') {
                forwardCount++
                pos += direction
            } else if (map[pos + direction.turnRight()] == '#') {
                if (forwardCount > 0) {
                    res.add(forwardCount.toString())
                }
                res.add("R")
                direction = direction.turnRight()
                forwardCount = 0
            } else if (map[pos + direction.turnLeft()] == '#') {
                if (forwardCount > 0) {
                    res.add(forwardCount.toString())
                }
                res.add("L")
                direction = direction.turnLeft()
                forwardCount = 0
            } else {
                res.add(forwardCount.toString())
                return res
            }
        }
    }

    fun part3() {
        val input = """
.........................................
...................#############.........
...................#.....................
...................#.....................
...................#.....................
...................#.....................
...................#.....................
...................###########...........
.............................#...........
...................#######...#...........
...................#.....#...#...........
...................#.....#...#...........
...................#.....#...#...........
...................#.....#...#...........
...................#.....#...#...........
.###########.......#.....#...#...........
.#.........#.......#.....#...#...........
.#.........#.....#####...#...#...........
.#.........#.....#.#.#...#...#...........
.#.....#############.#...#...###########.
.#.....#...#.....#...#...#.............#.
.#.....#...#######...#...#####.........#.
.#.....#.............#.......#.........#.
.#.....#.............#.......#.........#.
.#.....#.............#.......#.........#.
.#.....#.........#######################.
.#.....#.........#...#.......#...........
.#.....#.......#######.#######...........
.#.....#.......#.#.....#.................
.#######.......#.#.....#.................
...............#.#.....#.................
...#######.....#.###########.............
...#.....#.....#.......#...#.............
...#.....#.....#####...#...#.............
...#.....#.........#...#...#.............
...#.....#.........#...#...#.............
...#.....#.........#...#...#.............
...#.....###########...#####.............
...#.....................................
...#.....................................
...#.....................................
...###########...........................
.............#...........................
.............#...........................
.............#...........................
.............#...........................
.............#...........................
...^##########...........................
.........................................            
        """.trim()
        val map = input.toXYMap()

        val intersections = map.positions().filter { map[it] == '#' && it.adjacentNonDiagonal().all { map[it] == '#' } }

        val combinations = (0..2).toList().powercombo(intersections.size*2)

        var i = 0
        for (combo in combinations) {
            var path = makePathWithCombo(map, combo)
            if(path==null){
                continue
            }
            val res = findSubFunctions(listOf(path), 20, 3)
            if (res != null) {
                val (a, b, c) = res!!
                path = path.replace(a, listOf("A"))
                path = path.replace(b, listOf("B"))
                path = path.replace(c, listOf("C"))
                makePathWithCombo(map, combo)
                println(combo)
                println(path.joinToString(","))
                println(a.joinToString(","))
                println(b.joinToString(","))
                println(c.joinToString(","))
                break;
            }
            i++
            println(i)
        }
    }

    fun makePathWithCombo(map: XYMap<Char>, combo: List<Int>): List<String>? {
        val start = map.positions().filter { map[it] == '^' }.single()
        val count = map.positions().filter { map[it] == '#' }.count()
        var direction = Direction.N
        var pos = start
        val visited = mutableSetOf<Pos>()
        visited.add(pos)
        val res = mutableListOf<String>()
        var forwardCount = 0
        var comboIndex = 0
        while (true) {
            if (map[pos + direction] == '#'
                    && map[pos + direction.turnRight()] == '#'
                    && map[pos + direction.turnLeft()] == '#') {
                when (combo.getOrElse(comboIndex){0}) {
                    1 -> {
                        if (forwardCount > 0) {
                            res.add(forwardCount.toString())
                        }
                        res.add("R")
                        direction = direction.turnRight()
                        forwardCount = 0
                    }
                    2 -> {
                        if (forwardCount > 0) {
                            res.add(forwardCount.toString())
                        }
                        res.add("L")
                        direction = direction.turnLeft()
                        forwardCount = 0
                    }
                }
                comboIndex++
                forwardCount++
                pos += direction
                visited.add(pos)
            } else if (map[pos + direction] == '#') {
                forwardCount++
                pos += direction
                visited.add(pos)
            } else if (map[pos + direction.turnRight()] == '#') {
                if (forwardCount > 0) {
                    res.add(forwardCount.toString())
                }
                res.add("R")
                direction = direction.turnRight()
                forwardCount = 0
                forwardCount++
                pos += direction
                visited.add(pos)
            } else if (map[pos + direction.turnLeft()] == '#') {
                if (forwardCount > 0) {
                    res.add(forwardCount.toString())
                }
                res.add("L")
                direction = direction.turnLeft()
                forwardCount = 0
                forwardCount++
                pos += direction
                visited.add(pos)
            } else {
                res.add(forwardCount.toString())
                if (visited.size != count + 1) {
                    return null
                }
                return res
            }
        }
    }
}

fun main(args: Array<String>) {
    val d = Day17()
    //d.part1()
    d.part2()
    //d.part3()

    //decompile(d.input)
}
