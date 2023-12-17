package xyz.faber.adventofcode.util

import java.util.*

interface DirectedGraph<P> {
  fun getNeighbours(pos: P): Collection<P>
}

fun <P> Map<P, List<P>>.toDirectedGraph(): DirectedGraph<P> {
  val self = this
  return object : DirectedGraph<P> {
    override fun getNeighbours(pos: P): Collection<P> {
      return self[pos] ?: emptyList()
    }
  }
}

fun <P> ((P) -> Collection<P>).toDirectedGraph(): DirectedGraph<P> {
  val self = this
  return object : DirectedGraph<P> {
    override fun getNeighbours(pos: P): Collection<P> {
      return self.invoke(pos)
    }
  }
}

fun <P> DirectedGraph<P>.toDirectedWeightedGraph(): DirectedWeightedGraph<P> {
  val dg = this
  return object : DirectedWeightedGraph<P> {
    override fun getNeighbours(p: P): Collection<Edge<P>> = dg.getNeighbours(p).map { Edge(p, it, 1) }
  }
}


fun <P> DirectedGraph<P>.toDirectedWeightedGraph(costFunc: (P, P) -> Int): DirectedWeightedGraph<P> {
  val dg = this
  return object : DirectedWeightedGraph<P> {
    override fun getNeighbours(p: P): Collection<Edge<P>> = dg.getNeighbours(p).map { Edge(p, it, costFunc(p, it)) }
  }
}

fun <P> bfs(graph: DirectedGraph<P>, start: P): Sequence<P> = sequence {
  val visited = mutableSetOf<P>()
  val queue = LinkedList<P>()
  queue.addLast(start)
  while (!queue.isEmpty()) {
    val current = queue.pop()
    if (current in visited) {
      continue
    }
    yield(current)
    visited.add(current)
    val neighbours = graph.getNeighbours(current)
    queue.addAll(neighbours.filter { it !in visited })
  }
}

fun <P> bfs(graph: DirectedGraph<P>, start: P, predicate: (P) -> Boolean) = bfs(graph, start).filter(predicate)

fun <P> bfsWithLength(graph: DirectedGraph<P>, start: P): Sequence<Pair<P, Int>> = sequence {
  val visited = mutableSetOf<P>()
  val queue = LinkedList<Pair<P, Int>>()
  queue.addLast(start to 1)
  while (!queue.isEmpty()) {
    val current = queue.pop()
    if (current.first in visited) {
      continue
    }
    yield(current)
    visited.add(current.first)
    val neighbours = graph.getNeighbours(current.first)
    queue.addAll(neighbours.filter { it !in visited }.map { it to current.second + 1 })
  }
}

fun <P> bfsWithLength(graph: DirectedGraph<P>, start: P, predicate: (P) -> Boolean) =
  bfsWithLength(graph, start).filter { predicate(it.first) }

fun <P> bfsWithPath(graph: DirectedGraph<P>, start: P): Sequence<List<P>> = sequence {
  val visited = mutableSetOf<P>()
  val queue = LinkedList<List<P>>()
  queue.addLast(listOf(start))
  while (!queue.isEmpty()) {
    val current = queue.pop()
    if (current.last() in visited) {
      continue
    }
    yield(current)
    visited.add(current.last())
    val neighbours = graph.getNeighbours(current.last())
    queue.addAll(neighbours.filter { it !in visited }.map { current.plus(it) })
  }
}

fun <P> bfsWithPath(graph: DirectedGraph<P>, start: P, predicate: (P) -> Boolean) =
  bfsWithPath(graph, start).filter { predicate(it.last()) }

fun <P> dfs(graph: DirectedGraph<P>, start: P): Sequence<P> = sequence {
  val visited = mutableSetOf<P>()
  val stack = LinkedList<P>()
  stack.push(start)
  while (!stack.isEmpty()) {
    val current = stack.pop()
    if (current in visited) {
      continue
    }
    yield(current)
    visited.add(current)
    val neighbours = graph.getNeighbours(current)
    neighbours.filter { it !in visited }.asReversed().forEach { stack.push(it) }
  }
}

fun <P> dfs(graph: DirectedGraph<P>, start: P, predicate: (P) -> Boolean) = dfs(graph, start).filter(predicate)

fun <P> dfsWithPath(graph: DirectedGraph<P>, start: P): Sequence<List<P>> = sequence {
  val visited = mutableSetOf<P>()
  val stack = LinkedList<List<P>>()
  stack.push(listOf(start))
  while (!stack.isEmpty()) {
    val current = stack.pop()
    if (current.last() in visited) {
      continue
    }
    yield(current)
    visited.add(current.last())
    val neighbours = graph.getNeighbours(current.last())
    neighbours.filter { it !in visited }.map { current.plus(it) }.asReversed().forEach { stack.push(it) }
  }
}

fun <P> dfsWithPath(graph: DirectedGraph<P>, start: P, predicate: (P) -> Boolean) =
  dfsWithPath(graph, start).filter { predicate(it.last()) }

interface DirectedWeightedGraph<P> {
  fun getNeighbours(pos: P): Collection<Edge<P>>
}

class DirectedWeightedGraphBuilder<P> {
  private val connections = mutableMapOf<P, MutableList<Edge<P>>>()

  fun add(from: P, to: P, cost: Int) {
    connections.computeIfAbsent(from) { mutableListOf() }.add(Edge(from, to, cost))
  }

  fun build(): DirectedWeightedGraph<P> = DirectedWeightedGraphByMap(connections)
}

fun <P> DirectedWeightedGraph<P>.withExtraConnections(extra: Map<P, Collection<Edge<P>>>) =
  DirectedWeightedGraphWithExtraConnections(this, extra)

class DirectedWeightedGraphWithExtraConnections<P>(
  private var inner: DirectedWeightedGraph<P>,
  private var extra: Map<P, Collection<Edge<P>>>
) : DirectedWeightedGraph<P> {
  override fun getNeighbours(pos: P): Collection<Edge<P>> = inner.getNeighbours(pos) + (extra[pos] ?: emptyList())
}

fun <P> dijkstra(graph: DirectedWeightedGraph<P>, start: P): Map<P, Edge<P>> {
  val (shortestEdges, _) = dijkstraImpl(graph, start, null)
  return shortestEdges
}

fun <P> dijkstra(graph: DirectedWeightedGraph<P>, start: P, goal: P): PathSolution<P>? {
  val (shortestEdges, _) = dijkstraImpl(graph, start) { it == goal }
  return pathAndTotalCost(shortestEdges, start, goal)
}

fun <P> dijkstraPredicate(graph: DirectedWeightedGraph<P>, start: P, isGoal: ((P) -> Boolean)): PathSolution<P>? {
  val (shortestEdges, goal) = dijkstraImpl(graph, start, isGoal)
  return if (goal != null) pathAndTotalCost(shortestEdges, start, goal) else null
}

data class Edge<P>(val from: P, val to: P, val cost: Int, val label: String?) {
  constructor(from: P, to: P, cost: Int) : this(from, to, cost, null)
}

data class PathSolution<P>(val path: List<Edge<P>>) {
  val totalCost: Int = path.sumBy { it.cost }

  operator fun component2() = totalCost

  fun <Q> map(transform: (P) -> Q): PathSolution<Q> =
    PathSolution(path.map { Edge(transform(it.from), transform(it.to), it.cost, it.label) })

  fun print() {
    for (e in path) {
      if (e.label != null) {
        println("${e.label} : From ${e.from} to ${e.to} cost ${e.cost}")
      } else {
        println("From ${e.from} to ${e.to} cost ${e.cost}")
      }
    }
  }
}

private fun <P> dijkstraImpl(
  graph: DirectedWeightedGraph<P>,
  start: P,
  isGoal: ((P) -> Boolean)?
): Pair<Map<P, Edge<P>>, P?> {
  val distances = mutableMapOf(start to 0)
  val shortestEdges = mutableMapOf<P, Edge<P>>()
  val queue = HeapPriorityQueue<P>()
  queue.add(start, 0)
  while (queue.isNotEmpty()) {
    val from = queue.remove()
    if (isGoal != null && isGoal(from)) {
      return shortestEdges to from
    }
    for (edge in graph.getNeighbours(from)) {
      if (distances[edge.to] ?: Int.MAX_VALUE > distances[from]!! + edge.cost) {
        distances[edge.to] = distances[from]!! + edge.cost
        shortestEdges[edge.to] = edge
        queue.addOrUpdate(edge.to, distances[from]!! + edge.cost)
      }
    }
  }
  return shortestEdges to null
}

private fun <P> pathAndTotalCost(shortestEdges: Map<P, Edge<P>>, start: P, goal: P): PathSolution<P>? {
  val path = mutableListOf<Edge<P>>()
  var c = goal
  while (c != start) {
    val e = shortestEdges[c] ?: return null
    path.add(e)
    c = e.from
  }
  path.reverse()
  return PathSolution(path)
}

class DirectedWeightedGraphByMap<T>(private val distances: Map<T, Collection<Edge<T>>>) : DirectedWeightedGraph<T> {
  override fun getNeighbours(pos: T): Collection<Edge<T>> = distances[pos] ?: emptyList()
}

fun <T> Map<T, Collection<Edge<T>>>.toDirectedWeightedGraph(): DirectedWeightedGraph<T> =
  DirectedWeightedGraphByMap<T>(this)

fun <T> XYMap<T>.toDirectedWeightedGraph(
  positions: Collection<Pos>,
  isOpen: (T) -> Boolean
): DirectedWeightedGraph<Pos> {
  val topology = this.toGraph(isOpen)
  return positions.map { p1 -> p1 to (topology.getDistances(p1, positions).map { (p2, dist) -> Edge(p1, p2, dist) }) }
    .toMap()
    .toDirectedWeightedGraph()
}

fun <T> XYMap<T>.addToBuilder(
  builder: DirectedWeightedGraphBuilder<Pos>,
  positions: Collection<Pos>,
  isOpen: (T) -> Boolean
) {
  val topology = this.toGraph(isOpen)
  positions.forEach { p1 -> topology.getDistances(p1, positions).forEach { (p2, dist) -> builder.add(p1, p2, dist) } }
}

fun <T> DirectedGraph<T>.toDirectedWeightedGraph(positions: Collection<T>): DirectedWeightedGraph<T> {
  return positions.associateWith { p1 -> (this.getDistances(p1, positions).map { (p2, dist) -> Edge(p1, p2, dist) }) }
    .toDirectedWeightedGraph()
}

fun <T> DirectedGraph<T>.toFullDirectedWeightedGraph(positions: Collection<T>): DirectedWeightedGraph<T> {
  return positions.associateWith { p1 ->
    (this.getDistances(p1, positions, true).map { (p2, dist) -> Edge(p1, p2, dist) })
  }
    .toDirectedWeightedGraph()
}

fun <T> XYMap<T>.toDirectedWeightedGraphByContent(
  positions: Collection<Pos>,
  isOpen: (T) -> Boolean
): DirectedWeightedGraph<T> {
  val topology = this.toGraph(isOpen)
  return positions.map { p1 ->
    this[p1] to (topology.getDistances(p1, positions).map { (p2, dist) -> Edge(this[p1], this[p2], dist) })
  }.toMap()
    .toDirectedWeightedGraph()
}

fun <P> DirectedGraph<P>.getDistances(
  start: P,
  destinations: Collection<P>,
  continueOnPos: Boolean = false
): Map<P, Int> {
  val missing = destinations.toMutableSet()
  val res = mutableMapOf<P, Int>()
  val visited = mutableSetOf<P>()
  val queue = LinkedList<Pair<P, Int>>()
  queue.addLast(start to 0)
  missing.remove(start)
  while (!(queue.isEmpty() || missing.isEmpty())) {
    val (pos, dist) = queue.pop()
    if (pos in visited) {
      continue
    }
    if (pos in missing) {
      res[pos] = dist
      missing.remove(pos)
      visited.add(pos)
      if (continueOnPos) {
        val neighbours = this.getNeighbours(pos)
        queue.addAll(neighbours.filter { it !in visited }.map { it to dist + 1 })
      }
    } else {
      visited.add(pos)
      val neighbours = this.getNeighbours(pos)
      queue.addAll(neighbours.filter { it !in visited }.map { it to dist + 1 })
    }
  }
  return res
}

fun <P> DirectedGraph<P>.getRoutes(
  start: P,
  destinations: Collection<P>,
  continueOnPos: Boolean = false
): Map<P, List<P>> {
  val missing = destinations.toMutableSet()
  val res = mutableMapOf<P, List<P>>()
  val visited = mutableSetOf<P>()
  val queue = LinkedList<Pair<P, List<P>>>()
  queue.addLast(start to emptyList())
  missing.remove(start)
  while (!(queue.isEmpty() || missing.isEmpty())) {
    val (pos, route) = queue.pop()
    if (pos in visited) {
      continue
    }
    if (pos in missing) {
      res[pos] = route
      missing.remove(pos)
      visited.add(pos)
      if (continueOnPos) {
        val neighbours = this.getNeighbours(pos)
        queue.addAll(neighbours.filter { it !in visited }.map { it to route + it })
      }
    } else {
      visited.add(pos)
      val neighbours = this.getNeighbours(pos)
      queue.addAll(neighbours.filter { it !in visited }.map { it to route + it })
    }
  }
  return res
}

