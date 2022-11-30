package xyz.faber.adventofcode.year2019.day7

import adventofcode.year2019.intcode.Machine
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import xyz.faber.adventofcode.util.getProgram
import xyz.faber.adventofcode.util.permutations
import kotlin.math.max


fun part1(input: List<Long>) {
    val permutations = (0L..4L).toList().permutations()
    var best = 0L
    for (permutation in permutations) {
        var res = 0L
        for(i in 0..4) {
            val program = input.toMutableList()
            runBlocking {
                val channelIn = Channel<Long>(5)
                val channelOut = Channel<Long>(5)
                channelIn.send(permutation[i])
                channelIn.send(res)
                val machine = Machine(program.map{it.toLong()})
                machine.channelIn = channelIn
                machine.channelOut = channelOut
                machine.run()
                res = channelOut.receive()
            }
        }
        best = max(best, res)
    }
    println(best)
}

fun part2(input: List<Long>) {
    val permutations = (5L..9L).toList().permutations()
    var best = 0L
    var bestFrom = listOf<Long>()
    for (permutation in permutations) {
        val channels = (0..4).map{Channel<Long>(1000)}
        for (i in 0..4) {
            val signal = permutation[i]
            val channelIn = channels[i]
            runBlocking {
                channelIn.send(signal)
                if (i == 0) {
                    channelIn.send(0)
                }
            }
        }
        val machines = mutableListOf<Machine>()
        var results = mutableListOf<Deferred<Long>>()
        for (i in 0..4) {
            val program = input.toMutableList()//input.toMutableList()
            val channelIn = channels[i]
            val channelOut = channels[(i+1)%5]
            val machine = Machine(program)
            machine.channelIn = channelIn
            machine.channelOut = channelOut
            machine.id= "$permutation - $i"
            machines.add(machine)
            results.add(machine.runAsync())
        }
        runBlocking {
            val v = results[4].await()
            if(v>best){
                bestFrom = permutation
            }
            best = max(best, v)
        }
    }
    println(bestFrom)
    println(best)
}


fun main(args: Array<String>) {
    val program = getProgram(2019, 7)
    part1(program)
    println("---")
    part2(program)
    println("---")
    //decompile(program)
}
