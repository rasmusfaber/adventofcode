package xyz.faber.adventofcode.util

import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.List
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.math.max
import kotlin.math.min


interface AABBShape {
  fun getAABB(): AABB
}

fun IntRange.overlaps(other: IntRange) = !(this.last < other.first || other.last < this.first)

data class AABB(
  val xrange: IntRange,
  val yrange: IntRange,
  val zrange: IntRange
) {
  constructor(p1: Pos3D, p2: Pos3D) : this(
    min(p1.x, p2.x)..max(p1.x, p2.x),
    min(p1.y, p2.y)..max(p1.y, p2.y),
    min(p1.z, p2.z)..max(p1.z, p2.z)
  )

  fun merge(other: AABB): AABB {
    return AABB(
      min(xrange.first, other.xrange.first)..max(xrange.last, other.xrange.last),
      min(yrange.first, other.yrange.first)..max(yrange.last, other.yrange.last),
      min(zrange.first, other.zrange.first)..max(zrange.last, other.zrange.last)
    )
  }

  val space: Long
    get() = 1L * (xrange.last - xrange.first + 1) * (yrange.last - yrange.first + 1) * (zrange.last - zrange.first + 1)

  fun overlaps(other: AABB) =
    xrange.overlaps(other.xrange) && yrange.overlaps(other.yrange) && zrange.overlaps(other.zrange)

  fun contains(p: Pos3D) = p.x in xrange && p.y in yrange && p.z in zrange

}

class AABBTree<T : AABBShape> {
  private val nodes = mutableMapOf<T, AABBTreeNode<T>>()
  private var root: AABBTreeNode<T>? = null
  fun add(o: T) {
    /*if (contains(o)) {
      update(o)
      return
    }*/
    val aabb = o.getAABB()
    if (root == null) {
      val root = AABBTreeNode(aabb, o, null, null, null)
      nodes[o] = root
      this.root = root
      return
    }
    var current = root!!
    var newAabb = current.aabb
    while (!current.isLeaf()) {
      val left = current.left!!
      val right = current.right!!
      val newNodeAabb = current.aabb!!.merge(aabb)
      val newLeftAabb = left.aabb!!.merge(aabb)
      val newRightAabb = right.aabb!!.merge(aabb)
      val volumeDifference = newNodeAabb.space - current.aabb!!.space
      if (volumeDifference > 0) {
        var leftCost = 0L
        var rightCost = 0L
        if (left.isLeaf()) {
          leftCost = newLeftAabb.space + volumeDifference
        } else {
          leftCost = newLeftAabb.space - left.aabb!!.space + volumeDifference
        }

        if (right.isLeaf()) {
          rightCost = newRightAabb.space + volumeDifference
        } else {
          rightCost = newRightAabb.space - right.aabb!!.space + volumeDifference
        }

        if (newNodeAabb.space < leftCost * 1.3 && newNodeAabb.space < rightCost * 1.3) {
          break
        }

        current.aabb = newNodeAabb
        if (leftCost > rightCost) {
          current = right
          newAabb = newRightAabb
        } else {
          current = left
          newAabb = newLeftAabb
        }
      } else {
        current.aabb = newNodeAabb
        val leftVolumeIncrease = newLeftAabb.space - left.aabb!!.space
        val rightVolumeIncrease = newRightAabb.space - right.aabb!!.space
        if (leftVolumeIncrease > rightVolumeIncrease) {
          current = right
          newAabb = newRightAabb
        } else {
          current = left
          newAabb = newLeftAabb
        }
      }
    }
    val newChild = AABBTreeNode(
      current.aabb,
      current.o,
      current,
      current.right,
      current.left
    )
    if (newChild.o != null) {
      nodes[newChild.o!!] = newChild
    }
    if (newChild.right != null) {
      newChild.right!!.parent = newChild
    }
    if (newChild.left != null) {
      newChild.left!!.parent = newChild
    }

    current.left = newChild
    current.right = AABBTreeNode(aabb, o, current, null, null)
    current.aabb = if (current == root) {
      this.root!!.aabb!!.merge(aabb)
    } else {
      newAabb
    }
    current.o = null

    nodes[o] = current.right!!
  }

  fun remove(o: T) {
    val node = nodes[o]
    if (node == null) {
      return
    }
    removeNode(node)
    nodes.remove(o)
  }

  private fun removeNode(node: AABBTreeNode<T>) {
    if (node.parent == null) {
      this.root = null
      return
    }
    val parent = node.parent!!
    val sibling = if (parent.left == node) parent.right!! else parent.left!!
    parent.aabb = sibling.aabb
    parent.o = sibling.o
    parent.left = sibling.left
    parent.right = sibling.right

    if (!sibling.isLeaf()) {
      val left = sibling.left!!
      val right = sibling.right!!
      left.parent = parent
      right.parent = parent
    }

    if (parent.o != null && nodes.containsKey(parent.o)) {
      nodes[parent.o!!] = parent
    }

    var current = parent.parent
    while (current != null) {
      current.aabb = current.left!!.aabb!!.merge(current.right!!.aabb!!)
      current = current.parent
    }
  }

  fun detectOverlap(a: AABB): List<T> {
    val res = mutableListOf<T>()
    if (root == null) {
      return res
    }

    val stack: ArrayDeque<AABBTreeNode<T>> = ArrayDeque()
    stack.addFirst(root!!)

    while (!stack.isEmpty()) {
      val node = stack.removeFirst()

      val nodeAABB = node.aabb!!
      if (a.overlaps(nodeAABB)) {
        if (node.isLeaf()) {
          res.add(node.o!!)
        } else {
          stack.addFirst(node.left!!)
          stack.addFirst(node.right!!)
        }
      }
    }
    return res
  }

  fun update(block: T) {
    remove(block)
    add(block)
  }
}


class AABBTreeNode<T>(
  var aabb: AABB?,
  var o: T?,
  var parent: AABBTreeNode<T>?,
  var left: AABBTreeNode<T>?,
  var right: AABBTreeNode<T>?
) {
  fun isLeaf() = left == null && right == null

  override fun toString(): String {
    return "$o: $aabb"
  }
}
