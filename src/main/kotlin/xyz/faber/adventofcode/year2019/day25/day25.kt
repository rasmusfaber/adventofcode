package xyz.faber.adventofcode.year2019.day25

import adventofcode.year2019.intcode.Machine
import kotlinx.coroutines.runBlocking
import xyz.faber.adventofcode.util.getInputLongsFromCsv
import xyz.faber.adventofcode.util.powercombo
import xyz.faber.adventofcode.util.powerset
import java.io.File

class Day25 {
    val input = getInputLongsFromCsv(2019, 25)

    fun part1() {
        val program = input
        val file = File("src/main/resources/adventofcode/2019/day25/input.txt")
        var prevmem: List<Long>? = null
        while (true) {
            var lastModified = file.lastModified()
            var input = file.readText().replace("\r\n", "\n")
            var lines = input.lines().filter { it.isNotEmpty() && it[0] != '#' }.toMutableList()
            var takesCount = lines.count { it.startsWith("take") }

            input = lines.joinToString("\n") + "\n"
            val machine = Machine(program)
            machine.printOutputAsAscii = true
            machine.sendOutput = {}
            machine.sendAsciiString(input)
            val deferred = machine.runAsyncSwallowException()
            while (!machine.idle && !machine.done) {
                Thread.sleep(10)
            }
            if (machine.idle) {
                machine.done = true
                machine.channelIn.close()
            }
            if (prevmem != null) {
                for (i in 0 until prevmem.size) {
                    if (prevmem[i] != machine.mem[i]) {
                        //println("$i: ${prevmem[i]} ${machine.mem[i]} ${machine.mem[i]-prevmem[i]}")
                    }
                }
            }
            prevmem = machine.mem
            while (!machine.done && lastModified == file.lastModified()) {
                Thread.sleep(100L)
            }
            if (!machine.done) {
                machine.done = true
                machine.channelIn.close()
            }
            val res = runBlocking { deferred.await() }
            println(res)
            println("----------")
            while (lastModified == file.lastModified()) {
                Thread.sleep(100L)
            }
        }
    }

    fun part1b() {
        val program = input
        val file = File("src/main/resources/adventofcode/2019/day25/input.txt")
        var input = file.readText().replace("\r\n", "\n")
        var lines = input.lines()
        var takesCount = lines.count { it.startsWith("take") || it.startsWith("#take") }
        val combinations = listOf(true, false).powercombo(takesCount)
        for (combination in combinations) {
            val attempt = lines.toMutableList()
            var i = 0
            for ((index, line) in lines.withIndex()) {
                if (line.startsWith("take") || line.startsWith("#take")) {
                    if (!combination[i]) {
                        attempt[index] = "#" + attempt[index]
                    } else {
                        attempt[index] = attempt[index].replace("#", "")
                    }
                    i++
                }
            }
            val attemptinput = attempt.filter { it.isNotEmpty() && it[0] != '#' }.joinToString("\n") + "\n"
            val machine = Machine(program)
            val sb = StringBuilder()
            machine.sendOutput = {
                sb.append(it.toInt().toChar())
            }
            machine.printOutputAsAscii = false
            machine.sendAsciiString(attemptinput)
            val deferred = machine.runAsyncSwallowException()
            while (!machine.done) {
                if (machine.idle) {
                    machine.done = true
                    machine.channelIn.close()
                }
            }
            val ok = !(sb.contains("are lighter") || sb.contains("are heavier"))
            if (ok) {
                println(sb.toString())
                break
            }
        }
    }

    fun part1c() {
        val program = input
        val machine = Machine(program)
        machine.printOutputAsAscii = false
        val explorer = Explorer(machine)
        explorer.explore()

    }

}

class Explorer(val machine: Machine) {
    val locations = mutableMapOf<String, Location>()
    val missingExits = mutableSetOf<Pair<String, String>>()
    val handledExits = mutableSetOf<Pair<String, String>>()
    fun explore() {
        var currentLocation = walk(null)!!
        locations[currentLocation.name] = currentLocation
        missingExits += (currentLocation.getExitPairs() - handledExits)
        val items = mutableSetOf<String>()
        while (missingExits.isNotEmpty() && currentLocation.name != "Security Checkpoint") {
            val randomDirection = currentLocation.exits.random()
            missingExits -= (currentLocation.name to randomDirection)
            handledExits += (currentLocation.name to randomDirection)
            currentLocation = walk(randomDirection) ?: currentLocation
            if (currentLocation.name !in locations.keys && currentLocation.name != "Pressure-Sensitive Floor") {
                locations[currentLocation.name] = currentLocation
                missingExits += (currentLocation.getExitPairs() - handledExits)
            }
            currentLocation.items
                    .filter { it !in setOf("infinite loop", "giant electromagnet", "photons", "molten lava", "escape pod") }
                    .forEach { take(it); items.add(it) }
        }

        val combinations = items.powerset()
        var currentItems: Set<String> = items
        for (combination in combinations) {
            (currentItems - combination).forEach { drop(it) }
            (combination - currentItems).forEach { take(it) }
            currentItems = combination
            val location = walk("west")
            if (location != null) {
                println(location.fullText)
                return;
            }
        }
    }

    fun walk(direction: String?): Location? {
        if (direction != null) {
            machine.sendAsciiString("$direction\n")
        }
        val output = machine.runUntilInputAndReturnAscii()
        if (output.contains("back to the checkpoint")) {
            return null
        }
        return parseLocation(output)
    }

    fun take(item: String) {
        machine.sendAsciiString("take $item\n")
        val output = machine.runUntilInputAndReturnAscii()
    }

    fun drop(item: String) {
        machine.sendAsciiString("drop $item\n")
        val output = machine.runUntilInputAndReturnAscii()
    }
}

data class Location(val name: String, val description: String, val exits: List<String>, val items: List<String>, val fullText: String) {
    fun getExitPairs() = exits.map { this.name to it }.toSet()
}

fun parseLocation(s: String): Location {
    val regex = "== (.*) ==\n(.*)\n\nDoors here lead:\n((?:- .*\n)*)(?:\nItems here:\n((?:- .*\n)*))?".toRegex()
    val match = regex.find(s) ?: throw IllegalArgumentException(s)
    val (name, description, exits, items) = match.destructured
    val exitList = exits.lines().filter { it.isNotBlank() }.map { it.replace("- ", "") }
    val itemList = items.lines().filter { it.isNotBlank() }.map { it.replace("- ", "") }
    return Location(name, description, exitList, itemList, s)
}

fun main(args: Array<String>) {
    val d = Day25()
    d.part1c()
}
