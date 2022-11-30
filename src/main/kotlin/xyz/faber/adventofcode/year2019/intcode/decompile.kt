package adventofcode.year2019.intcode

import java.util.*

fun decompile(program: List<Long>) {
    val parsed = parse(program)
    val locations = parsed.map { it.location }.toCollection(TreeSet())
    val referencesFromImmediate = parsed
            .filter { it.opcode != null }
            .map { it.opcode!! }
            .flatMap { it.inputParameters.plus(it.outputParameters) }
            .filter { it.mode == ParameterMode.Position }
            .map { it.value }
    val referencesFromJump = parsed
            .filter { it.opcode != null }
            .map { it.opcode!! }
            .filter { it.opcode.isJump }
            .map { it.inputParameters[1] }
            .map { it.value }
    val references = referencesFromImmediate.plus(referencesFromJump).distinct()
    val referenceToLocation = references
            .map { it to (locations.floor(it) ?: 0L) }
            .toMap()
    val usedLocations = referenceToLocation.values.toSortedSet()
    val usedLocationName = usedLocations.toList().mapIndexed { index, it -> it to "loc$index" }.toMap()
    for (element in parsed) {
        if (element.location in usedLocations) {
            val name = usedLocationName[element.location]!!
            print("$name: " + " ".repeat(6 - name.length))
        } else {
            print(" ".repeat(8))
        }
        if (element.opcode != null) {
            val (opcode, input, output) = element.opcode!!
            print(opcode.name)
            if (opcode.isJump) {
                print(" ")
                print(toLocation(input[0], referenceToLocation, usedLocationName, false))
                print(" ")
                print(toLocation(input[1], referenceToLocation, usedLocationName, true))
            } else {
                if (input.isNotEmpty()) {
                    print(" ")
                    print(input.map { toLocation(it, referenceToLocation, usedLocationName, false) }.joinToString(" "))
                }
                if (output.isNotEmpty()) {
                    print(" -> ")
                    print(output.map { toLocation(it, referenceToLocation, usedLocationName, false) }.joinToString(" "))
                }
            }
            println();
        } else {
            println(element.data)
        }
    }
}

fun toLocation(p: Parameter, referenceToLocation: Map<Long, Long>, locationNames: Map<Long, String>, translateImmediate: Boolean): String {
    if (!translateImmediate && p.mode == ParameterMode.Immediate) {
        return p.toString()
    }
    if (!translateImmediate && p.mode == ParameterMode.Relative) {
        return p.toString()
    }
    val closestLocation = referenceToLocation[p.value]!!
    val distance = p.value - closestLocation
    val closestLocationName = locationNames[closestLocation]!!
    return when (p.mode) {
        ParameterMode.Immediate ->
            if (distance == 0L) {
                "$closestLocationName"
            } else if (distance < 4) {
                "$closestLocationName+$distance"
            } else {
                "${p.value}"
            }
        ParameterMode.Position ->
            if (distance == 0L) {
                "[$closestLocationName]"
            } else if (distance < 4) {
                "[$closestLocationName+$distance]"
            } else {
                "[${p.value}]"
            }
        ParameterMode.Relative ->
            if (distance == 0L) {
                "[offset+$closestLocationName]"
            } else if (distance > 0) {
                "[offset+$closestLocationName+$distance]"
            } else {
                "[offset+$closestLocationName-${-distance}]"
            }
    }
}

fun parse(program: List<Long>): List<OpcodeWithParametersOrData> {
    var i = 0
    val res = mutableListOf<OpcodeWithParametersOrData>()
    while (i < program.size) {
        val startI = i
        val codeVal = program[i++]
        val oldI = i
        try {
            val (opcode, modes) = parseCodeVal(codeVal)
            val inputParameters = (0 until opcode.numInput)
                    .map {
                        val parameter = program[i++]
                        val mode = getMode(modes, it)
                        when (mode) {
                            0 -> Parameter(parameter, ParameterMode.Position)
                            1 -> Parameter(parameter, ParameterMode.Immediate)
                            2 -> Parameter(parameter, ParameterMode.Relative)
                            else -> throw RuntimeException("Unknown mode $mode")
                        }
                    }
            val outputParameters = (0 until opcode.numOutput)
                    .map {
                        val parameter = program[i++]
                        val mode = getMode(modes, opcode.numInput + it)
                        when (mode) {
                            0 -> Parameter(parameter, ParameterMode.Position)
                            1 -> Parameter(parameter, ParameterMode.Immediate)
                            2 -> Parameter(parameter, ParameterMode.Relative)
                            else -> throw RuntimeException("Unknown mode $mode")
                        }
                    }
            res.add(OpcodeWithParametersOrData(startI.toLong(), OpcodeWithParameters(opcode, inputParameters, outputParameters), null))
        } catch (e: RuntimeException) {
            i = oldI
            res.add(OpcodeWithParametersOrData(startI.toLong(), null, codeVal))
        }
    }
    return res
}

data class OpcodeWithParameters(val opcode: Opcode, val inputParameters: List<Parameter>, val outputParameters: List<Parameter>)

data class OpcodeWithParametersOrData(val location: Long, val opcode: OpcodeWithParameters?, val data: Long?)

data class Parameter(val value: Long, val mode: ParameterMode) {
    override fun toString(): String {
        return when (mode) {
            ParameterMode.Position -> "[$value]"
            ParameterMode.Immediate -> "$value"
            ParameterMode.Relative -> when {
                value > 0L -> "[offset+$value]"
                value == 0L -> "[offset]"
                else -> "[offset-${-value}]"
            }
        }
    }
}

enum class ParameterMode {
    Position,
    Immediate,
    Relative
}