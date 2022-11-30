package xyz.faber.adventofcode.util

import kotlin.math.roundToInt

fun <P> aStar(graph: DirectedGraph<P>, heuristic: (P, P) -> Int, start: P, end: P) = aStar(graph.toDirectedWeightedGraph(), heuristic, start, end)

fun <P> aStar(graph: DirectedWeightedGraph<P>, heuristic: (P, P) -> Int, start: P, end: P): PathSolution<P>? {
    return aStarPredicate(graph, { heuristic(it, end) }, start, { it == end })
}

fun <P> aStarPredicate(graph: DirectedWeightedGraph<P>, heuristicToEnd: (P) -> Int, start: P, isGoal: (P) -> Boolean): PathSolution<P>? {
    return aStarPredicate({graph.getNeighbours(it)}, heuristicToEnd, start, isGoal)
}

fun <P> aStarPredicate(neighbours: (P)->Collection<Edge<P>>, heuristicToEnd: (P) -> Int, start: P, isGoal: (P) -> Boolean): PathSolution<P>? {
    /**
     * Use the cameFrom values to Backtrack to the start position to generate the path
     */
    fun generatePath(currentPos: P, cameFrom: Map<P, Edge<P>>): PathSolution<P> {
        val path = mutableListOf<Edge<P>>()
        var current = currentPos
        while (cameFrom.containsKey(current)) {
            val edge = cameFrom.getValue(current)
            path.add(edge)
            current = edge.from
        }
        path.reverse()
        return PathSolution(path.toList())
    }

    val estimatedTotalCost = mutableMapOf(start to heuristicToEnd(start))
    val openVertices = HeapPriorityQueue<P>()
    val closedVertices = mutableSetOf<P>()
    val costFromStart = mutableMapOf(start to 0)

    val cameFrom = mutableMapOf<P, Edge<P>>()  // Used to generate path by back tracking

    openVertices.add(start, heuristicToEnd(start))

    while (openVertices.isNotEmpty()) {

        val currentPos = openVertices.remove()

        // Check if we have reached the end
        if (isGoal(currentPos)) {
            println("${closedVertices.size} closed, ${openVertices.size} open")
            // Backtrack to generate the most efficient path
            return generatePath(currentPos, cameFrom)
        }

        // Mark the current vertex as closed
        closedVertices.add(currentPos)

        neighbours(currentPos)
                .filterNot { closedVertices.contains(it.to) }  // Exclude previous visited vertices
                .forEach { edge ->
                    val score = costFromStart.getValue(currentPos) + edge.cost
                    val neighborCostFromStart = costFromStart.get(edge.to)
                    if (neighborCostFromStart == null || score < neighborCostFromStart) {
                        openVertices.addOrUpdate(edge.to, score + heuristicToEnd(edge.to))
                        cameFrom[edge.to] = edge
                        costFromStart[edge.to] = score
                    }
                }
        if(closedVertices.size%1000000==0) {
            println("${closedVertices.size} closed, ${openVertices.size} open (${(100.0 * openVertices.size/closedVertices.size).roundToInt()}%)")
        }
    }

    return null
}

class XYMapTopology<T>(val map: XYMap<T>, val isOpen: (T) -> Boolean) : DirectedGraph<Pos> {
    override fun getNeighbours(pos: Pos): Collection<Pos> = pos.adjacentNonDiagonal().filter { isOpen(map[it]) }
}

fun <T> XYMap<T>.toGraph(barriers: Set<T>) = this.toGraph { it in barriers }

fun <T> XYMap<T>.toGraph(isOpen: (T) -> Boolean) = XYMapTopology(this, isOpen)

fun <T> XYMap<T>.toGraph2(openings: Set<T>) = this.toGraph { it !in openings }

fun <T> XYMap<T>.printPath(path: PathSolution<Pos>, pathChar: String) {
    val pathSet = path.path.map{it.to}.toSet()
    this.print { if (it in pathSet) pathChar else this[it].toString() }
}

fun PathSolution<Pos>.toDirections(): List<Direction> = this.path.map { (it.to - it.from).toDirection() }
