package xyz.faber.adventofcode.util

import java.util.PriorityQueue

data class KdNode<T>(
    val point: T,
    val split: Int,
    val left: KdNode<T>?,
    val right: KdNode<T>?
)

class KdTree<T>(points: Collection<T>, val dim: Int, private val coordinate: (T, Int) -> Double, private val distance: (T, T) -> Double) {
    private var root: KdNode<T>? = null

    init {
        root = buildTree(points, 0)
    }

    private fun buildTree(points: Collection<T>, depth: Int): KdNode<T>? {
        if (points.isEmpty()) {
            return null
        }

        val axis = depth % dim
        val sorted = points.sortedBy { coordinate(it, axis) }

        val median = sorted.size / 2
        return KdNode(
            sorted[median],
            axis,
            buildTree(sorted.subList(0, median), depth + 1),
            buildTree(sorted.subList(median + 1, sorted.size), depth + 1)
        )
    }

    fun nearestNeighbor(target: T): T {
        var best = root!!.point
        var bestDist = distance(target, best)
        fun search(node: KdNode<T>?) {
            if (node == null) {
                return
            }

            val dist = distance(target, node.point)
            if (dist < bestDist) {
                best = node.point
                bestDist = dist
            }

            val axis = node.split
            val axisValue = coordinate(node.point, axis)
            val targetValue = coordinate(target, axis)

            val (near, far) = if (targetValue < axisValue) {
                node.left to node.right
            } else {
                node.right to node.left
            }

            search(near)

            if (bestDist > Math.abs(axisValue - targetValue)) {
                search(far)
            }
        }

        search(root)
        return best
    }

    fun nNearestNeighbors(target: T, n: Int): List<T> {
        val queue = PriorityQueue<Pair<T, Double>>(compareByDescending { it.second })

        fun search(node: KdNode<T>?) {
            if (node == null) return

            val dist = distance(target, node.point)
            if (queue.size < n || dist < queue.peek().second) {
                queue.offer(node.point to dist)
                if (queue.size > n) queue.poll()
            }

            val axis = node.split
            val axisValue = coordinate(node.point, axis)
            val targetValue = coordinate(target, axis)

            val (near, far) = if (targetValue < axisValue) {
                node.left to node.right
            } else {
                node.right to node.left
            }

            search(near)

            if (queue.size < n || Math.abs(axisValue - targetValue) < queue.peek().second) {
                search(far)
            }
        }

        search(root)
        return queue.map { it.first }
    }

    fun nthNearestNeighbor(target: T, n: Int): T? {
        val neighbors = nNearestNeighbors(target, n)
        return if (neighbors.size >= n) neighbors[0] else null
    }

}