package xyz.faber.adventofcode.year2019.day23

import adventofcode.year2019.intcode.Machine
import adventofcode.year2019.intcode.collectParams
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import xyz.faber.adventofcode.util.getInputLongsFromCsv

class Day23 {
    val input = getInputLongsFromCsv(2019, 23)

    fun part1() {
        val network = (0..49).map { i ->
            val m = Machine(input)
            m.id = i.toString()
            m.send(i)
            m
        }
        val idle = mutableMapOf<String, Boolean>()
        val channel255 = Channel<Long>(Channel.UNLIMITED)
        network.forEach { m ->
            m.receiveInput = {
                runBlocking {
                    m.channelIn.tryReceive().getOrNull() ?: -1
                }
            }
            m.sendOutput = collectParams { a, b, c ->
                //println("${m.id}: -> $a : $b $c")
                if (a == 255L) {
                    runBlocking {
                        channel255.send(b)
                        channel255.send(c)
                    }
                } else {
                    network[a.toInt()].send(b)
                    network[a.toInt()].send(c)
                }
            }
        }
        val finalPacket = mutableListOf<Long>()
        while (finalPacket.size < 2) {
            network.forEach { it.runSingleStep() }

            val res = runBlocking {
                val d = channel255.tryReceive().getOrNull() 
                if (d != null) {
                    finalPacket.add(d)
                }
            }
        }
        println(finalPacket[1])
    }


    fun part2() {
        val network = (0..49).map { i ->
            val m = Machine(input)
            m.id = i.toString()
            m.send(i)
            m
        }
        val lastWasInput = mutableMapOf<String, Int>()
        val idle = mutableMapOf<String, Boolean>()
        val channel255 = Channel<Long>(Channel.UNLIMITED)
        network.forEach { m ->
            m.receiveInput = {
                runBlocking {
                    val res = m.channelIn.tryReceive().getOrNull() 
                    if (res == null) {
                        val newidle = (lastWasInput[m.id] ?: 0) >= 5
                        /*if (idle[m.id] == false && newidle) {
                            println("${m.id}: idle")
                        }*/
                        idle[m.id] = newidle
                        lastWasInput[m.id] = (lastWasInput[m.id] ?: 0) + 1
                    } else {
                        /*if (idle[m.id] == true) {
                            println("${m.id}: no longer idle")
                        }*/
                        idle[m.id] = false
                    }
                    res ?: -1
                }
            }
            m.sendOutput = collectParams { a, b, c ->
                //println("${m.id}: -> $a : $b $c")
                if (a == 255L) {
                    runBlocking {
                        channel255.send(b)
                        channel255.send(c)
                    }
                } else {
                    network[a.toInt()].send(b)
                    network[a.toInt()].send(c)
                }
                lastWasInput[m.id] = 0
            }
        }
        var lastx: Long? = null
        var lasty: Long? = null
        var lastPacket = mutableListOf<Long>()
        var lastsenty: Long? = null
        var res: Long? = null
        while (res == null) {
            network.forEach { it.runSingleStep() }
            var d = channel255.tryReceive().getOrNull() 
            while (d != null) {
                lastPacket.add(d)
                if (lastPacket.size == 2) {
                    lastx = lastPacket[0]
                    lasty = lastPacket[1]
                    lastPacket.clear()
                }
                d = channel255.tryReceive().getOrNull() 
            }
            if (network.all { idle[it.id] == true && it.channelIn.isEmpty && it.channelOut.isEmpty }) {
                if (lastx != null && lasty != null) {
                    //println("NAT: 0 -> $lastx $lasty")
                    network[0].send(lastx)
                    network[0].send(lasty)
                    if (lastsenty != null && lasty == lastsenty) {
                        res = lasty
                    }
                    lastsenty = lasty
                } else {
                    //println("NAT: All idle but nothing to send")
                }
            }

        }
        println(res)
    }

}

fun main(args: Array<String>) {
    val d = Day23()
    d.part1()
    println("---")
    d.part2()
}
