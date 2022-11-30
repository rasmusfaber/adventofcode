package xyz.faber.adventofcode.year2020.day20

import com.marcinmoskala.math.product
import xyz.faber.adventofcode.util.Pos
import xyz.faber.adventofcode.util.XYMap
import xyz.faber.adventofcode.util.getInput
import xyz.faber.adventofcode.util.toXYMap
import java.lang.Integer.min
import java.util.*
import kotlin.math.sqrt

class Day20 {
    val input = getInput(2020, 20)
    private val tiles = input.split("\n\n").filter { it.isNotBlank() }.map { parseTile(it) }.map { it.id to it }.toMap()

    fun part1() {
        val alledgescount = tiles.values.flatMap { it.edgeIds }.groupingBy { it }.eachCount()
        val corners = tiles.values.filter { it.edgeIds.count { alledgescount[it] == 1 } == 2 }.map { it.id }
        println(corners.product())
    }

    private fun getEdges(tile: XYMap<Char>): List<String> {
        return listOf(
                ((0..tile.maxx).map { tile[it, 0] }.joinToString("")),
                ((0..tile.maxy).map { tile[tile.maxx, it] }.joinToString("")),
                ((0..tile.maxx).map { tile[it, tile.maxy] }.joinToString("").reversed()),
                ((0..tile.maxy).map { tile[0, it] }.joinToString("").reversed()),
        )
    }


    fun part2() {
        val map = makeFullMap()
        rotateFlipAndRemoveSeaMonsters(map)
        val res = map.count { it.value == '#' }
        println(res)
    }

    fun rotateFlipAndRemoveSeaMonsters(map: XYMap<Char>) {
        for (i in 1..4) {
            val c = removeSeaMonsters(map)
            if (c > 0) {
                return
            }
            map.rotateCW()
        }
        map.flipHorizontal()
        for (i in 1..4) {
            val c = removeSeaMonsters(map)
            if (c > 0) {
                return
            }
            map.rotateCW()
        }
        throw RuntimeException("bad")
    }

    fun makeFullMap(): XYMap<Char> {
        val edgeIdToTile = tiles.values.flatMap { it.edgeIds.map { e -> e to it } }.groupBy({ it.first }, { it.second })
        val alledgescount = tiles.values.flatMap { it.edgeIds }.groupingBy { it }.eachCount()
        val outeredges = alledgescount.entries.filter { it.value == 1 }.map { it.key }.toSet()
        val corners = tiles.values.filter { it.edgeIds.count { alledgescount[it] == 1 } == 2 }.map { it.id }

        val sqrt = sqrt(tiles.size.toDouble()).toInt()
        val map = XYMap<Tile?>(sqrt, sqrt, null)
        val corner1Id = corners[0]!!
        val corner1 = tiles[corner1Id]!!
        while (!(toEdgeId(corner1.edges[0]) in outeredges && toEdgeId(corner1.edges[3]) in outeredges)) {
            corner1.rotateCW()
        }
        map[0, 0] = corner1
        for (x in 1..sqrt - 1) {
            val tile = map[x - 1, 0]!!
            val rightEdgeId = tile.edgeIds[1]
            val next = edgeIdToTile[rightEdgeId]!!.filter { it.id != tile.id }.single()
            while (next.edgeIds[3] != rightEdgeId) {
                next.rotateCW()
            }
            if (next.edges[3].reversed() != tile.edges[1]) {
                next.flipVertical()
            }
            map[x, 0] = next
        }
        for (y in 1..sqrt - 1) {
            val up = map[0, y - 1]!!
            val downEdgeId = up.edgeIds[2]
            val next = edgeIdToTile[downEdgeId]!!.filter { it.id != up.id }.single()
            while (next.edgeIds[0] != downEdgeId) {
                next.rotateCW()
            }
            if (next.edges[0].reversed() != up.edges[2]) {
                next.flipHorizontal()
            }
            map[0, y] = next
            for (x in 1..sqrt - 1) {
                val tile = map[x - 1, y]!!
                val rightEdgeId = tile.edgeIds[1]
                val next = edgeIdToTile[rightEdgeId]!!.filter { it.id != tile.id }.single()
                while (next.edgeIds[3] != rightEdgeId) {
                    next.rotateCW()
                }
                if (next.edges[3].reversed() != tile.edges[1]) {
                    next.flipVertical()
                }
                map[x, y] = next
            }
        }
        return joinTilesRemoveEdges(map)
    }

    private fun joinTilesRemoveEdges(map: XYMap<Tile?>): XYMap<Char> {
        val tiledimx = map[0, 0]!!.map.dimx - 2
        val dimx = tiledimx * map.dimx
        val tiledimy = map[0, 0]!!.map.dimy - 2
        val dimy = tiledimy * map.dimy
        val res = XYMap(dimx, dimy, ' ')
        for (p in map.positions()) {
            val tilemap = map[p]!!.map
            for (x in tilemap.minx + 1..tilemap.maxx - 1) {
                for (y in tilemap.miny + 1..tilemap.maxy - 1) {
                    res[p.x * tiledimx + x - 1, p.y * tiledimy + y - 1] = tilemap[x, y]
                }
            }
        }
        return res
    }

    private fun joinTilesWithExtraSpace(map: XYMap<Tile?>): XYMap<Char> {
        val tiledimx = map[0, 0]!!.map.dimx + 1
        val dimx = tiledimx * map.dimx
        val tiledimy = map[0, 0]!!.map.dimy + 1
        val dimy = tiledimy * map.dimy
        val res = XYMap(dimx, dimy, ' ')
        for (p in map.positions()) {
            map[p]?.map?.copyTo(res, p.x * tiledimx, p.y * tiledimy)
        }
        return res
    }

    private fun parseTile(data: String): Tile {
        val lines = data.lines()
        val id = lines[0].substring(5, lines[0].length - 1).toInt()
        val map = lines.subList(1, lines.size).toXYMap()
        return Tile(id, map)
    }
}

private class Tile(val id: Int, val map: XYMap<Char>) {
    var edges = getEdges(map).toMutableList()
    var edgeIds = edges.map { toEdgeId(it) }.toMutableList()

    private fun getEdges(tile: XYMap<Char>): List<String> {
        return listOf(
                ((0..tile.maxx).map { tile[it, 0] }.joinToString("")),
                ((0..tile.maxy).map { tile[tile.maxx, it] }.joinToString("")),
                ((tile.maxx downTo 0).map { tile[it, tile.maxy] }.joinToString("")),
                ((tile.maxy downTo 0).map { tile[0, it] }.joinToString("")),
        )
    }

    fun rotateCW() {
        map.rotateCW()
        Collections.rotate(edges, 1)
        Collections.rotate(edgeIds, 1)
    }

    fun flipVertical() {
        map.flipVertical()
        val temp1 = edges[0]
        edges[0] = edges[2].reversed()
        edges[2] = temp1.reversed()
        val temp2 = edgeIds[0]
        edgeIds[0] = edgeIds[2]
        edgeIds[2] = temp2
        edges[1] = edges[1].reversed()
        edges[3] = edges[3].reversed()
    }

    fun flipHorizontal() {
        map.flipHorizontal()
        val temp1 = edges[1]
        edges[1] = edges[3].reversed()
        edges[3] = temp1.reversed()
        val temp2 = edgeIds[1]
        edgeIds[1] = edgeIds[3]
        edgeIds[3] = temp2
        edges[0] = edges[0].reversed()
        edges[2] = edges[2].reversed()
    }
}

private fun toEdgeId(s: String): Int {
    val bin = s.replace(".", "0").replace("#", "1")
    return min(bin.toInt(2), bin.reversed().toInt(2))
}


private fun <T> XYMap<T>.copyTo(target: XYMap<T>, dx: Int, dy: Int) {
    val delta = Pos(dx, dy)
    for (p in this.positions()) {
        target[p + delta] = this[p]
    }
}

private val seamonsterpos = ("                  # \n" +
        "#    ##    ##    ###\n" +
        " #  #  #  #  #  #   ").toXYMap().filter { it.value == '#' }.map { it.pos }

private fun removeSeaMonsters(map: XYMap<Char>): Int {
    var count = 0
    for (x in map.minx..map.maxx) {
        for (y in map.miny + 1..map.maxy) {
            val pos = Pos(x, y)
            if (seamonsterpos.all { map[it + pos] == '#' }) {
                seamonsterpos.forEach { map[it + pos] = '.' }
                count++
            }
        }
    }
    return count
}

fun main(args: Array<String>) {
    val d = Day20()
    d.part1()
    d.part2()
}
