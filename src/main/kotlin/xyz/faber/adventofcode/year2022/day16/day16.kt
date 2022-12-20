package xyz.faber.adventofcode.year2022.day16

import xyz.faber.adventofcode.util.*
import java.util.*
import kotlin.math.min

data class State(val minute: Int, val currentFlow: Int, val totalFlow: Int, val position: String, val opened: Set<String>)
data class State2(
    val minute: Int,
    val currentFlow: Int,
    val totalFlow: Int,
    val position1: String,
    val position2: String,
    val opened: Set<String>,
    val p1movinguntil: Int,
    val p2movinguntil: Int,
    val remainingFlow: Int, val path1: List<String>, val path2: List<String>
)

class Day16 : AdventSolution<Int>() {
    fun parse(input: List<String>): Pair<Map<String, Int>, Map<String, List<String>>> {
        val flows = mutableMapOf<String, Int>()
        val graph = mutableMapOf<String, List<String>>()
        val regex = "Valve (..) has flow rate=(.*); tunnels? leads? to valves? (.*)".toRegex()
        for (line in input) {
            val (valve, flow, exits) = regex.find(line)!!.destructured
            flows[valve] = flow.toInt()
            graph[valve] = exits.split(", ")
        }
        return flows to graph
    }


    override fun part1(input: List<String>): Int {
        val (flows, tunnels) = parse(input)

        val nextStates = { prev: State ->
            if (prev.minute == 30) {
                emptyList<State>()
            } else {
                val res = tunnels[prev.position]!!.map { State(prev.minute + 1, prev.currentFlow, prev.totalFlow + prev.currentFlow, it, prev.opened) }
                val flow = flows[prev.position]!!
                if (flow == 0 || prev.position in prev.opened) {
                    res
                } else {
                    res + State(prev.minute + 1, prev.currentFlow + flow, prev.totalFlow + prev.currentFlow, prev.position, prev.opened + prev.position)
                }
            }
        }

        val graph = nextStates.toDirectedGraph()
        val start = State(0, 0, 0, "AA", emptySet())

        val visited = mutableSetOf<State>()
        val bestByPosAndTime = mutableMapOf<Pair<String, Int>, Int>()
        val sequence = sequence<State> {
            val queue = LinkedList<State>()
            queue.addLast(start)
            while (!queue.isEmpty()) {
                val current = queue.pop()
                if (current in visited) {
                    continue
                }
                val best = bestByPosAndTime[current.position to current.minute]
                if (best != null && current.totalFlow <= best) {
                    continue
                }
                bestByPosAndTime[current.position to current.minute] = current.totalFlow
                yield(current)
                visited.add(current)
                val neighbours = graph.getNeighbours(current)
                queue.addAll(neighbours.filter { it !in visited })
            }
        }

        return sequence.maxOf { it.totalFlow }
    }

    override fun part2(input: List<String>): Int {
        val (flows, tunnels) = parse(input)

        val valves = flows.entries.filter { it.value > 0 || it.key == "AA" }.map { it.key }.toSet()

        val dwg = tunnels.toDirectedGraph().toFullDirectedWeightedGraph(valves)

        val nextStates = { prev: State2 ->
            if (prev.minute >= 25) {
                emptyList()
            } else {
                val remaining = 25 - prev.minute
                if (prev.minute >= prev.p1movinguntil && prev.minute >= prev.p2movinguntil) {
                    val neighbours1 = dwg.getNeighbours(prev.position1).filter { it.to !in prev.opened && it.cost + 1 <= remaining }
                    val neighbours2 = dwg.getNeighbours(prev.position2).filter { it.to !in prev.opened && it.cost + 1 <= remaining }
                    neighbours1.flatMap { edge1 ->
                        val flow1 = flows[edge1.to]!!
                        neighbours2.filter { it.to != edge1.to }.map { edge2 ->
                            val flow2 = flows[edge2.to]!!
                            State2(
                                prev.minute + min(edge1.cost + 1, edge2.cost + 1),
                                prev.currentFlow + flow1 + flow2,
                                prev.totalFlow + (flow1 * (remaining - edge1.cost)) + (flow2 * (remaining - edge2.cost)),
                                edge1.to,
                                edge2.to,
                                prev.opened + edge1.to + edge2.to,
                                prev.minute + edge1.cost + 1,
                                prev.minute + edge2.cost + 1,
                                prev.remainingFlow - flow1 - flow2,
                                prev.path1 + edge1.to,
                                prev.path2 + edge2.to
                            )
                        }
                    }
                } else if (prev.minute >= prev.p1movinguntil) {
                    val neighbours1 = dwg.getNeighbours(prev.position1).filter { it.to !in prev.opened && it.cost + 1 <= remaining }
                    if (neighbours1.isEmpty()) {
                        listOf(
                            State2(
                                prev.p2movinguntil,
                                prev.currentFlow,
                                prev.totalFlow,
                                prev.position1,
                                prev.position2,
                                prev.opened,
                                26,
                                prev.p2movinguntil,
                                prev.remainingFlow,
                                prev.path1,
                                prev.path2
                            )
                        )
                    } else {
                        neighbours1.map { edge1 ->
                            val flow1 = flows[edge1.to]!!
                            State2(
                                min(prev.minute + edge1.cost + 1, prev.p2movinguntil),
                                prev.currentFlow + flow1,
                                prev.totalFlow + (flow1 * (remaining - edge1.cost)),
                                edge1.to,
                                prev.position2,
                                prev.opened + edge1.to,
                                prev.minute + edge1.cost + 1,
                                prev.p2movinguntil,
                                prev.remainingFlow - flow1,
                                prev.path1 + edge1.to,
                                prev.path2
                            )
                        }
                    }
                } else {
                    val neighbours2 = dwg.getNeighbours(prev.position2).filter { it.to !in prev.opened && it.cost + 1 <= remaining }
                    if (neighbours2.isEmpty()) {
                        listOf(
                            State2(
                                prev.p1movinguntil,
                                prev.currentFlow,
                                prev.totalFlow,
                                prev.position1,
                                prev.position2,
                                prev.opened,
                                prev.p1movinguntil,
                                26,
                                prev.remainingFlow,
                                prev.path1,
                                prev.path2
                            )
                        )
                    } else {
                        neighbours2.map { edge2 ->
                            val flow2 = flows[edge2.to]!!
                            State2(
                                min(prev.p1movinguntil, prev.minute + edge2.cost + 1),
                                prev.currentFlow + flow2,
                                prev.totalFlow + (flow2 * (remaining - edge2.cost)),
                                prev.position1,
                                edge2.to,
                                prev.opened + edge2.to,
                                prev.p1movinguntil,
                                prev.minute + edge2.cost + 1,
                                prev.remainingFlow - flow2,
                                prev.path1,
                                prev.path2 + edge2.to
                            )
                        }
                    }
                }
            }
        }


        val graph = nextStates.toDirectedGraph()
        val start = State2(0, 0, 0, "AA", "AA", emptySet(), 0, 0, flows.values.sum(), emptyList(), emptyList())

        val visited = mutableSetOf<State2>()
        var bestOverall = 0
        val sequence = sequence<State2> {
            val stack = LinkedList<State2>()
            stack.push(start)
            while (!stack.isEmpty()) {
                val current = stack.pop()
                val c = current
                if (c in visited) {
                    continue
                }
                val maxRemaining = c.remainingFlow * (25 - c.minute)
                if (maxRemaining + c.totalFlow < bestOverall) {
                    continue
                }
                if (c.totalFlow > bestOverall) {
                    bestOverall = c.totalFlow
                }
                yield(c)
                val neighbours = graph.getNeighbours(c)
                neighbours.forEach { stack.push(it) }
            }
        }

        val x = sequence.maxBy { it.totalFlow }
        return x.totalFlow
    }
}

fun main(args: Array<String>) {
    AdventRunner(2022, 16, Day16()).run()

}
