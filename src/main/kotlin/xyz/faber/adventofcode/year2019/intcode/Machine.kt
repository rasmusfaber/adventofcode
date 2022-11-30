package adventofcode.year2019.intcode

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.math.max

class Machine(program: List<Long>) {
    var id: String = ""
    val mem = program.toMutableList()
    val extramem = mutableMapOf<Long, Long>()
    var baseOffset = 0L
    var ip = 0L
    var done = false
    var lastOutput = 0L
    var channelIn = Channel<Long>(Channel.UNLIMITED)
    var channelOut = Channel<Long>(Channel.UNLIMITED)
    var printOutput = false
    var printOutputAsAscii = false
    private val idleWait = AtomicBoolean(false)
    var receiveInput: suspend () -> Long = {
        var res = channelIn.tryReceive().getOrNull() 
        if (res == null) {
            idleWait.set(true)
            res = channelIn.receive()
            idleWait.set(false)
        }
        res
    }
    var sendOutput: suspend (Long) -> Unit = { channelOut.send(it) }

    val idle: Boolean
        get() = runBlocking { idleWait.get() && channelIn.isEmpty && channelOut.isEmpty }

    fun next(): Long {
        val res = mem[ip.toInt()]
        ip++
        return res
    }

    fun getValue(parameter: Long, modes: Long, parameterIndex: Int): Long {
        val mode = getMode(modes, parameterIndex)
        return when (mode) {
            0 -> getMem(parameter)
            1 -> parameter
            2 -> getMem(parameter + baseOffset)
            else -> throw RuntimeException("Unknown mode $mode")
        }
    }

    fun setValue(parameter: Long, modes: Long, parameterIndex: Int, value: Long) {
        val mode = getMode(modes, parameterIndex)
        when (mode) {
            0 -> setMem(parameter, value)
            1 -> throw RuntimeException("Cannot set in mode $mode")
            2 -> setMem(parameter + baseOffset, value)
            else -> throw RuntimeException("Unknown mode $mode")
        }
    }

    fun getMem(address: Long): Long {
        //if(address==1552L) return 0
        if (address in 0 until mem.size) {
            return mem[address.toInt()]
        } else {
            return extramem[address] ?: 0
        }
    }

    fun setMem(address: Long, value: Long) {
        if(address==1552L){
            //println("*")
        }
        if (address in 0 until mem.size) {
            mem[address.toInt()] = value
        } else {
            extramem[address] = value
        }
    }

    fun maxMemAddress(): Long {
        val maxExtraMem = extramem.keys.maxOrNull() ?: -1
        return max(mem.size.toLong(), maxExtraMem)
    }

    fun runAsync(): Deferred<Long> {
        return GlobalScope.async {
            runInner()
            lastOutput
        }
    }

    fun runThread() {
        thread(start = true) {
            runBlocking {
                runInner()
            }
        }
    }

    fun runAsyncSwallowException(): Deferred<Long> {
        return GlobalScope.async {
            try {
                runInner()
            } catch (e: Exception) {
            }
            lastOutput
        }
    }

    private suspend fun runInner() {
        do {
            step()
        } while (!done)
        if (done) {
            channelOut.close()
        }
    }

    private suspend fun step() {
        val oldIp = ip
        try {
            val codeVal = next()
            val (opcode, modes) = parseCodeVal(codeVal)
            opcode.exec(this, modes)
        }catch(e: Exception){
            ip = oldIp
            throw e
        }
    }

    fun run(input: List<Long>) {
        runBlocking {
            input.forEach { channelIn.send(it) }
            runInner()
        }
    }

    fun run(input: Long) {
        run(listOf(input))
    }

    fun run() {
        run(emptyList())
    }

    fun runSingleStep() {
        runBlocking {
            step()
        }
    }

    fun runUntilOutput(vararg input: Long): Long? {
        return runBlocking {
            input.forEach { channelIn.send(it) }
            do {
                step()
            } while (!done && channelOut.isEmpty)
            val res = channelOut.tryReceive().getOrNull() 
            if (done) {
                channelOut.close()
            }
            res
        }
    }

    fun runUntilInput() {
        this.receiveInput = {
            channelIn.tryReceive().getOrNull()  ?: throw NoInputException()
        }
        runBlocking {
            try {
                do {
                    step()
                } while (!done)
            }catch(e:NoInputException){
                // OK
            }
        }
    }

    fun runUntilInputAndReturnAscii(): String {
        val sb = StringBuilder()
        this.sendOutput = {
            sb.append(it.toInt().toChar())
        }
        runUntilInput()
        return sb.toString()
    }

    fun receive(): Long {
        return runBlocking { channelOut.receive() }
    }

    fun receiveAll(): List<Long> {
        return runBlocking {
            val res = mutableListOf<Long>()
            var o = channelOut.tryReceive().getOrNull() 
            while (o != null) {
                res.add(o)
                o = channelOut.tryReceive().getOrNull() 
            }
            res
        }
    }

    fun outputIsEmpty(): Boolean {
        return runBlocking { channelOut.isEmpty }
    }

    fun send(v: Long) {
        runBlocking { channelIn.send(v) }
    }

    fun send(v: Int) {
        runBlocking { channelIn.send(v.toLong()) }
    }

    fun sendAsciiString(s: String) {
        s.chars().forEach { send(it.toLong()) }
    }
}

fun getMode(modes: Long, index: Int): Int {
    var shifted = modes
    for (i in 0 until index) {
        shifted /= 10
    }
    return (shifted % 10).toInt()
}

fun collectParams(function: (Long) -> Unit): suspend (Long) -> Unit = { function(it) }
fun collectParams(function: (Long, Long) -> Unit): suspend (Long) -> Unit = collectParams(2) { function(it[0], it[1]) }
fun collectParams(function: (Long, Long, Long) -> Unit): suspend (Long) -> Unit = collectParams(3) { function(it[0], it[1], it[2]) }
fun collectParams(function: (Long, Long, Long, Long) -> Unit): suspend (Long) -> Unit = collectParams(4) { function(it[0], it[1], it[2], it[3]) }
fun collectParams(function: (Long, Long, Long, Long, Long) -> Unit): suspend (Long) -> Unit = collectParams(5) { function(it[0], it[1], it[2], it[3], it[4]) }

fun collectIntParams(function: (Int) -> Unit): suspend (Long) -> Unit = { function(it.toInt()) }
fun collectIntParams(function: (Int, Int) -> Unit): suspend (Long) -> Unit = collectIntParams(2) { function(it[0], it[1]) }
fun collectIntParams(function: (Int, Int, Int) -> Unit): suspend (Long) -> Unit = collectIntParams(3) { function(it[0], it[1], it[2]) }
fun collectIntParams(function: (Int, Int, Int, Int) -> Unit): suspend (Long) -> Unit = collectIntParams(4) { function(it[0], it[1], it[2], it[3]) }
fun collectIntParams(function: (Int, Int, Int, Int, Int) -> Unit): suspend (Long) -> Unit = collectIntParams(5) { function(it[0], it[1], it[2], it[3], it[4]) }

fun collectIntParams(numParams: Int, function: (List<Int>) -> Unit): suspend (Long) -> Unit = collectParams(numParams) { function(it.map(Long::toInt)) }

fun collectParams(numParams: Int, function: (List<Long>) -> Unit): suspend (Long) -> Unit {
    val params = mutableListOf<Long>()
    return { param ->
        params += param
        if (params.size == numParams) {
            function(params)
            params.clear()
        }
    }
}

fun runMachine(program: List<Long>, vararg input: Int): List<Long> {
    val machine = Machine(program)
    input.forEach { machine.send(it) }
    machine.run()
    return machine.receiveAll()
}

fun runMachine(program: List<Long>, input: List<Long>): List<Long> {
    val machine = Machine(program)
    input.forEach { machine.send(it) }
    machine.run()
    return machine.receiveAll()
}

fun runMachine(program: List<Long>, vararg input: Long): List<Long> {
    val machine = Machine(program)
    input.forEach { machine.send(it) }
    machine.run()
    return machine.receiveAll()
}

fun runMachine(program: List<Long>, input: String): List<Long> = runMachine(program, input.convertToInput())

fun runMachine(program: List<Long>, input: Collection<String>): List<Long> = runMachine(program, input.joinToString("\n", postfix = "\n"))

fun List<Long>.convertToString() = this.map { it.toInt().toChar() }.joinToString("")

fun String.convertToInput(): List<Long> = this.toCharArray().toList().map { it.toLong() }

fun runMachineOnFileContinuously(program: List<Long>, file: File) {
    while (true) {
        var lastModified = file.lastModified()
        var input = file.readText().replace("\r\n", "\n")
        input = input.lines().filter { it.isNotEmpty() && it[0]!='#' }.joinToString ( "\n" )+"\n"
        val machine = Machine(program)
        machine.printOutputAsAscii = true
        machine.sendAsciiString(input)
        val deferred = machine.runAsyncSwallowException()
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

class NoInputException: RuntimeException("No input")
