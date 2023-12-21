package xyz.faber.adventofcode.year2023.day21

import xyz.faber.adventofcode.util.*
import java.util.*

class Day21 : AdventSolutionWithTransform<Long, CharXYMap>() {
  override fun transformAll(input: String): CharXYMap {
    return input.toXYMap()
  }


  override fun part1(input: CharXYMap): Long {
    val start = input.single { it.value == 'S' }.pos
    val graph = input.toDirectedGraph { it == 'S' || it == '.' }
    val distances = graph.getDistancesCloserThan(start, 64)
    /*input.printChars {
      val d = distances[it]?:99
      if(d<=6 && (d%2)==0)
        'O'
      else
        input[it]
    }*/
    return distances.values.count { (it % 2) == 0 }.toLong()
  }

  fun part2slow(input: CharXYMap, range: Int): Long {
    val start = input.single { it.value == 'S' }.pos
    val graph = input.toInfiniteDirectedGraph { it == 'S' || it == '.' }
    val distances = graph.getDistancesCloserThan(start, range)
    /*input.printChars {
      val d = distances[it]?:99
      if(d<=6 && (d%2)==0)
        'O'
      else
        input[it]
    }*/
    val possible = distances.filter { (it.value%2)==(range%2) }
    /*val map = possible.map{it.key}.toXYMap()
    map.print{if(it==start)"S" else if(map[it]) "O" else if(input[it.x.mod(input.dimx), it.y.mod(input.dimy)]=='#') "#" else "."}*/
    return possible.size.toLong()
  }


  fun reachablePointsInSquare(map: CharXYMap, start: Pos, pos: Pos, range: Int): Long {
    val closestPoint = when {
      pos.x == 0 && pos.y == 0 -> start
      pos.x == 0 && pos.y > 0 -> Pos(start.x, map.miny)
      pos.x == 0 && pos.y < 0 -> Pos(start.x, map.maxy)
      pos.x > 0 && pos.y == 0 -> Pos(map.minx, start.y)
      pos.x < 0 && pos.y == 0 -> Pos(map.maxx, start.y)
      pos.x < 0 && pos.y > 0 -> Pos(map.maxx, map.miny)
      pos.x < 0 && pos.y < 0 -> Pos(map.maxx, map.maxy)
      pos.x > 0 && pos.y < 0 -> Pos(map.minx, map.maxy)
      pos.x > 0 && pos.y > 0 -> Pos(map.minx, map.miny)
      else -> throw IllegalArgumentException("Unexpected")
    }
    val closestPointFullCoords = Pos(pos.x * map.dimx + closestPoint.x, pos.y * map.dimy + closestPoint.y)
    val distanceToClosestPoint = manhattanDistance(start, closestPointFullCoords)
    if (distanceToClosestPoint > range) {
      return 0
    }
    val remainingRange = range - distanceToClosestPoint
    val graph = map.toDirectedGraph { it == 'S' || it == '.' }
    val distances = graph.getDistancesCloserThan(closestPoint, remainingRange)
    val possible = distances.filter { (it.value%2)==remainingRange%2 }
    //println(pos)
    //map.print{if(it in possible) "O" else ""+map[it]}
    return possible.size.toLong()
  }

  override fun part2(input: CharXYMap): Long {
    if (input.any { it.value == 'O' }) throw IllegalArgumentException("SKip")
    val range = 26501365
    val start = input.single { it.value == 'S' }.pos
    // validate conditions
    assert(input.xrange.all { input[it, start.y] == '.' || input[it, start.y] == 'S' })
    assert(input.yrange.all { input[start.x, it] == '.' || input[start.x, it] == 'S' })
    assert(start.x == input.maxx / 2)
    assert(start.y == input.maxy / 2)
    assert(input.maxx == input.maxy)

    //println(part2slow(input, range))

    /*val dim=range/input.dimx+1
    val values = XYMap(-dim,dim,-dim,dim){x,y->reachablePointsInSquare(input, start, Pos(x,y), range)}
    values.print{values[it].toString().padEnd(5)}
    println(values.sumOf { it.value })*/

    val b = (range / input.dimx) -2
    //val r = (range - 2) % input.dimx
    val r = range - start.y - start.x - 2 - b*input.dimy
    val r2 = range - start.y - 1 - (b+1)*input.dimy
    //val r2 = (range - input.maxx / 2 - 1) % input.dimx
    val graph = input.toInfiniteDirectedGraph { it == 'S' || it == '.' }
    val positions = input.filter { it.value == 'S' || it.value == '.' }.map { it.pos }
    val singleMapCount = graph.getDistancesCloserThan(start, range, {input.isInBounds(it)}).count { it.value % 2 == range % 2 }
    val singleMapCount2 = graph.getDistancesCloserThan(start, range, {input.isInBounds(it)}).count { it.value % 2 == (range + 1) % 2 }
    val reachableEvenFullSquares = 1L*(((b) / 2) * 2+2) * (((b) / 2) * 2+2)
    val reachableOddFullSquares = 1L*(((b + 1) / 2) * 2 + 1) * (((b + 1) / 2) * 2 + 1)
    //assert(reachableEvenFullSquares + reachableOddFullSquares == 2 * b * (b + 1) + 1)
    val count1 = graph.getDistancesCloserThan(Pos(0, 0), r, {it.y in input.yrange && it.x>=0}).count { it.value % 2 == r % 2 }
    val count2 =
      graph.getDistancesCloserThan(Pos(0, input.maxy), r, {it.y in input.yrange && it.x>=0}).count { it.value % 2 == r % 2 }
    val count3 =
      graph.getDistancesCloserThan(Pos(input.maxx, 0), r, {it.y in input.yrange && it.x<=input.maxx}).count { it.value % 2 == r % 2 }
    val count4 = graph.getDistancesCloserThan(Pos(input.maxx, input.maxy), r, {it.y in input.yrange && it.x<=input.maxx}).count { it.value % 2 == r % 2 }
    val count5 = graph.getDistancesCloserThan(Pos(0, start.y), r2, {it.x>=0 && it.y in input.yrange}).count { it.value % 2 == r2 % 2 }
    val count6 = graph.getDistancesCloserThan(Pos(start.x, 0), r2, {it.y>=0}).count { it.value % 2 == r2 % 2 }
    val count7 =
      graph.getDistancesCloserThan(Pos(start.x, input.maxy), r2, {it.y<=input.maxy}).count { it.value % 2 == r2 % 2 }
    val count8 =
      graph.getDistancesCloserThan(Pos(input.maxx, start.y), r2, {it.x<=input.maxx && it.y in input.yrange}).count { it.value % 2 == r2 % 2 }

    // 642811076369852 too high
    // 631808572686919 too low
    // not 632417028377989
    // not 632417357171621
    val res = reachableOddFullSquares * singleMapCount + reachableEvenFullSquares * singleMapCount2 +
      count5 + count6 + count7 + count8 +
      (count1 + count2 + count3 + count4) * (b + 1L)
    return res
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 21, Day21()).run()

}
