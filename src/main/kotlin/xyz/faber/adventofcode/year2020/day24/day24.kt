package xyz.faber.adventofcode.year2020.day24

import xyz.faber.adventofcode.util.Pos
import xyz.faber.adventofcode.util.getInputFromLines

class Day24 {
    val regex = "(e|se|sw|w|nw|ne)".toRegex()
    val input = getInputFromLines(2020, 24).map { regex.findAll(it)!!.toList().map { it.value } }
    /*val input = ("sesenwnenenewseeswwswswwnenewsewsw\n" +
            "neeenesenwnwwswnenewnwwsewnenwseswesw\n" +
            "seswneswswsenwwnwse\n" +
            "nwnwneseeswswnenewneswwnewseswneseene\n" +
            "swweswneswnenwsewnwneneseenw\n" +
            "eesenwseswswnenwswnwnwsewwnwsene\n" +
            "sewnenenenesenwsewnenwwwse\n" +
            "wenwwweseeeweswwwnwwe\n" +
            "wsweesenenewnwwnwsenewsenwwsesesenwne\n" +
            "neeswseenwwswnwswswnw\n" +
            "nenwswwsewswnenenewsenwsenwnesesenew\n" +
            "enewnwewneswsewnwswenweswnenwsenwsw\n" +
            "sweneswneswneneenwnewenewwneswswnese\n" +
            "swwesenesewenwneswnwwneseswwne\n" +
            "enesenwswwswneneswsenwnewswseenwsese\n" +
            "wnwnesenesenenwwnenwsewesewsesesew\n" +
            "nenewswnwewswnenesenwnesewesw\n" +
            "eneswnwswnwsenenwnwnwwseeswneewsenese\n" +
            "neswnwewnwnwseenwseesewsenwsweewe\n" +
            "wseweeenwnesenwwwswnew").lines().map { regex.findAll(it)!!.toList().map { it.value } }*/

    fun part1() {
        val tiles = input.map { getHexCoordinates(it) }
        val counts = tiles.groupingBy { it }.eachCount()
        val res = counts.count { it.value % 2 == 1 }
        println(res)
    }

    private fun getHexCoordinates(movements: List<String>): Pos {
        var pos = Pos(0, 0)
        for (movement in movements) {
            pos += when (movement) {
                "e" -> Pos(2, 0)
                "w" -> Pos(-2, 0)
                "ne" -> Pos(1, -1)
                "nw" -> Pos(-1, -1)
                "sw" -> Pos(-1, 1)
                "se" -> Pos(1, 1)
                else -> throw IllegalArgumentException(movement)
            }
        }
        return pos
    }


    fun part2() {
        val tiles = input.map { getHexCoordinates(it) }
        val counts = tiles.groupingBy { it }.eachCount()
        var black = counts.filter { it.value % 2 == 1 }.keys
        for (i in 1..100) {
            val pos = black.flatMap { neighbors(it)+it }.toSet()
            val next = pos.filter {
                val c = neighbors(it).count { it in black }
                val b = it in black
                (b && c in 1..2) || (!b && c == 2)
            }.toSet()
            black = next
        }
        println(black.size)
    }

    fun neighbors(p: Pos) = listOf(
            Pos(p.x - 2, p.y),
            Pos(p.x - 1, p.y - 1),
            Pos(p.x + 1, p.y - 1),
            Pos(p.x + 2, p.y),
            Pos(p.x + 1, p.y + 1),
            Pos(p.x - 1, p.y + 1)
    )
}

fun main(args: Array<String>) {
    val d = Day24()
    d.part1()
    d.part2()
}
