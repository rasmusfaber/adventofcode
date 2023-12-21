package xyz.faber.adventofcode.year2023.day20

import com.marcinmoskala.math.product
import guru.nidi.graphviz.attribute.Rank
import guru.nidi.graphviz.attribute.Rank.RankType
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.graph
import guru.nidi.graphviz.model.Factory.node
import guru.nidi.graphviz.toGraphviz
import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.lcm
import xyz.faber.adventofcode.util.show

class Day20 : AdventSolution<Long>() {
  data class Module(val name: String, val type: Char, val destinations: List<String>)

  data class State(
    val flipflops: MutableMap<String, Boolean>,
    val conjunctions: Map<String, MutableMap<String, Boolean>>,
  ) {
    fun flip(name: String): Boolean {
      return flipflops.compute(name) { _, v -> !(v!!) }!!
    }

    operator fun get(name: String): Boolean {
      return flipflops[name] ?: conjunctions[name]?.all { it.value } ?: throw IllegalArgumentException("unknown")
    }
  }

  data class Signal(val source: String, val dest: String, val pulse: Boolean)

  fun parseModule(s: String): Module {
    val (namePart, destinationPart) = s.split(" -> ")
    val destinations = destinationPart.split(", ")
    if (namePart == "broadcaster") {
      return Module(namePart, ' ', destinations)
    } else if (namePart == "rx") {
      return Module(namePart, 'r', destinations)
    } else {
      return Module(namePart.substring(1), namePart[0], destinations)
    }
  }

  private fun sendSignal(
    queue: ArrayDeque<Signal>,
    source: String,
    dest: String,
    pulse: Boolean,
    signalListener: (Signal) -> Unit
  ) {
    val signal = Signal(source, dest, pulse)
    queue.addLast(signal)
    signalListener(signal)
    //println("${source} ${if (!pulse) "-low" else "-high"} -> ${dest}")
  }

  private fun run(
    state: State,
    moduleMap: Map<String, Module>,
    signalListener: (Signal) -> Unit
  ) {
    val queue = ArrayDeque<Signal>()
    sendSignal(queue, "button", "broadcaster", false, signalListener)
    while (queue.isNotEmpty()) {
      val signal = queue.removeFirst()
      val moduleName = signal.dest
      val pulse = signal.pulse
      val module = moduleMap[moduleName] ?: continue
      when (module.type) {
        ' ' -> {
          module.destinations.forEach {
            sendSignal(queue, module.name, it, false, signalListener)
          }
        }

        '%' -> {
          if (!pulse) {
            val newState = state.flip(module.name)
            module.destinations.forEach {
              sendSignal(queue, module.name, it, newState, signalListener)
            }
          }
        }

        '&' -> {
          val conjunctionState = state.conjunctions[module.name]!!
          conjunctionState[signal.source] = pulse
          val res = !conjunctionState.all { it.value }
          module.destinations.forEach {
            sendSignal(queue, module.name, it, res, signalListener)
          }
        }

        else -> throw IllegalArgumentException("Bad module type")
      }
    }
  }

  fun initState(modules: List<Module>): State {
    val flipflops = modules.filter { it.type == '%' }
    val conjunctions = modules.filter { it.type == '&' }
    val flipflopstates = flipflops.map { it.name to false }.toMap().toMutableMap()
    val conjunctionStates = conjunctions.map { c ->
      c.name to modules.filter { m -> c.name in m.destinations }.map { it.name to false }.toMap().toMutableMap()
    }.toMap()
    return State(flipflopstates, conjunctionStates)
  }

  override fun part1(input: List<String>): Long {
    val modules = input.map { parseModule(it) }
    val moduleMap = modules.map { it.name to it }.toMap()
    val state = initState(modules)
    var lowPulses = 0L
    var highPulses = 0L
    for (i in 1..1000) {
      run(state, moduleMap) {
        if (it.pulse) {
          highPulses++
        } else {
          lowPulses++
        }
      }
    }
    return lowPulses * highPulses
  }

  fun showAsGraph(modules: List<Module>) {
    val rxsources = modules.filter { "rx" in it.destinations }.map { it.name }
    graph(directed = true) {
      graph().graphAttrs().add(Rank.inSubgraph(RankType.SOURCE)).add(node("broadcaster"))
      graph().graphAttrs().add(Rank.inSubgraph(RankType.SINK)).add(node("rx")).add(rxsources.map { node(it) })

      for (module in modules) {
        for (destination in module.destinations) {
          module.name - destination
        }
      }

    }.toGraphviz().render(Format.PNG).toImage().show()
  }

  fun findCycleLength(modules: List<Module>, target: String): Long {
    val state = initState(modules)
    val moduleMap = modules.map { it.name to it }.toMap()
    var targetReceivedLowSignal = false
    var i = 0L
    while (!targetReceivedLowSignal) {
      i++
      run(state, moduleMap) {
        if (!it.pulse && it.dest == target) {
          targetReceivedLowSignal = true
        }
      }
    }
    return i
  }

  override fun part2(input: List<String>): Long {
    val modules = input.map { parseModule(it) }
    if (!modules.any { "rx" in it.destinations }) {
      // Not in real data
      return -1
    }
    val inputs = modules.flatMap { it.destinations }.map { it to modules.filter { m -> it in m.destinations } }.toMap()
    showAsGraph(modules)
    val rxsources = inputs["rx"]!!.flatMap { inputs[it.name]!!.map { it.name } }
    val cycles = rxsources.map { findCycleLength(modules, it) }
    return lcm(cycles)
  }
}

fun main(args: Array<String>) {
  AdventRunner(2023, 20, Day20()).run()

}
