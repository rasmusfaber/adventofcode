package xyz.faber.adventofcode.year2023.day22

import xyz.faber.adventofcode.util.*
import kotlin.math.max
import kotlin.math.min

class Block(val name: String, val xrange: IntRange, val yrange: IntRange, var zrange: IntRange) : AABBShape {
  constructor(name: String, p1: Pos3D, p2: Pos3D) : this(
    name,
    min(p1.x, p2.x)..max(p1.x, p2.x),
    min(p1.y, p2.y)..max(p1.y, p2.y),
    min(p1.z, p2.z)..max(p1.z, p2.z)
  )

  fun overlap2D(other: Block) = this.xrange.overlaps(other.xrange) && this.yrange.overlaps(other.yrange)
  override fun getAABB(): AABB {
    return AABB(xrange, yrange, zrange)
  }

  fun getBelow(): AABB {
    return AABB(xrange, yrange, 1..zrange.first - 1)
  }

  fun directlyAbove(): AABB {
    return AABB(xrange, yrange, zrange.last + 1..zrange.last + 1)
  }

  fun directlyBelow(): AABB {
    return AABB(xrange, yrange, zrange.first - 1..zrange.first - 1)
  }

  override fun toString(): String {
    return "$name: $xrange, $yrange, $zrange)"
  }


}

fun parseBlock(index: Int, s: String): Block {
  val (p1, p2) = s.split('~').map { it.split(',').map { it.toInt() } }.map { (x, y, z) -> Pos3D(x, y, z) }
  return Block(index.toString(), p1, p2)
}

class Day22 : AdventSolution<Int>() {
  private fun fall(
    blocks: List<Block>,
    tree: AABBTree<Block>
  ) {
    var sortedBlocks = blocks.sortedBy { it.zrange.first }
    for (block in sortedBlocks) {
      val blocksBelow = tree.detectOverlap(block.getBelow())
      val maxz = blocksBelow.maxOfOrNull { it.zrange.last } ?: 0
      val deltaz = block.zrange.first - (maxz + 1)
      if (deltaz > 0) {
        block.zrange = (block.zrange.first - deltaz)..(block.zrange.last - deltaz)
        tree.update(block)
      }
    }
  }

  override fun part1(input: List<String>): Int {
    val blocks = input.withIndex().map { (i, s) -> parseBlock(i, s) }
    val tree = AABBTree<Block>()
    blocks.forEach {
      tree.add(it)
    }
    fall(blocks, tree)
    val safeBlocks = blocks.filter {
      tree.detectOverlap(it.directlyAbove()).all { tree.detectOverlap(it.directlyBelow()).size >= 2 }
    }
    return safeBlocks.size
  }

  fun countFalling(removed: Set<Block>, tree: AABBTree<Block>): Set<Block> {
    val causesToFall = removed.flatMap { tree.detectOverlap(it.directlyAbove()) }.distinct()
      .filter { it !in removed }
      .filter { tree.detectOverlap(it.directlyBelow()).all { it in removed } }.toSet()
    if (causesToFall.isEmpty()) {
      return emptySet()
    }
    return causesToFall + countFalling(removed + causesToFall, tree)
  }

  override fun part2(input: List<String>): Int {
    val blocks = input.withIndex().map { (i, s) -> parseBlock(i, s) }
    val tree = AABBTree<Block>()
    blocks.forEach {
      tree.add(it)
    }
    fall(blocks, tree)
    return blocks.sumOf {
      countFalling(setOf(it), tree).size
    }
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 22, Day22()).run()

}
