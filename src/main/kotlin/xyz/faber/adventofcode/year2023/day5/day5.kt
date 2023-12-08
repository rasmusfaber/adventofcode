package xyz.faber.adventofcode.year2023.day5

import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolutionWithTransform
import xyz.faber.adventofcode.util.split

data class MappingTable(val name: String, val mappings: List<Mapping>) {
  fun apply(v: Long): Long {
    return mappings.firstOrNull { it.applies(v) }?.apply(v) ?: v
  }

  fun apply(ranges: List<LongRange>): List<LongRange> {
    return ranges.flatMap { apply(it) }
  }

  fun apply(r: LongRange): List<LongRange> {
    var i = r.start
    val m = mappings.find { it.applies(i) }
    if (m != null) {
      if (m.applies(r.endInclusive)) {
        return listOf(LongRange(m.apply(r.start), m.apply(r.endInclusive)))
      } else {
        return listOf(
          LongRange(
            m.apply(r.start),
            m.apply(m.source + m.range - 1)
          )
        ) + apply(LongRange(m.source + m.range, r.endInclusive))
      }
    }
    val m2 =
      mappings.filter { it.source + it.range > r.start && it.source <= r.endInclusive }.minByOrNull { it.source }
    if (m2 != null) {
      return listOf(LongRange(r.start, m2.source - 1)) + apply(LongRange(m2.source, r.endInclusive))
    } else {
      return listOf(r)
    }
  }
}

data class Mapping(val destination: Long, val source: Long, val range: Long) {
  fun applies(v: Long): Boolean = v >= source && v < source + range
  fun apply(v: Long): Long {
    if (!applies(v)) {
      throw IllegalArgumentException()
    }
    return v - source + destination
  }
}

class Day5 : AdventSolutionWithTransform<Long, Pair<List<Long>, List<MappingTable>>>() {
  override fun transformAll(input: List<String>): Pair<List<Long>, List<MappingTable>> {
    val seeds = input.first().split(": ")[1].split(" ").map { it.toLong() }

    val tables = input.drop(2).split(listOf("")).map {
      val name = it.first()
      val mappings = it.drop(1).map { it.split(" ").map { it.toLong() }.let { Mapping(it[0], it[1], it[2]) } }
      MappingTable(name, mappings)
    }

    return seeds to tables
  }

  override fun part1(input: Pair<List<Long>, List<MappingTable>>): Long {
    val seeds = input.first
    val tables = input.second
    val locations = seeds.map { tables.fold(it) { acc, table -> table.apply(acc) } }
    return locations.min()!!
  }

  override fun part2(input: Pair<List<Long>, List<MappingTable>>): Long {
    val seeds = input.first.windowed(2, 2).map { LongRange(it[0], it[0] + it[1] - 1) }
    val tables = input.second
    val locationRanges = seeds.map { tables.fold(listOf(it)) { acc, table -> table.apply(acc) } }
    return locationRanges.flatMap { it.map { it.start } }.min()
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 5, Day5()).run()

}
