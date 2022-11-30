package xyz.faber.adventofcode.year2021.day24

import com.microsoft.z3.BitVecExpr
import com.microsoft.z3.Context
import xyz.faber.adventofcode.util.getInputFromLines
import java.util.*

val _1To9 = setOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
val zeroAndOne = setOf(0, 1)

val inputs = mutableMapOf<Int, Int>()


class Day24 {
    val input = getInputFromLines(2021, 24)

    fun runProgram(input: List<String>, number: Queue<Int>): Int {
        val program = input.map { it.split(" ") }
        var registers = mutableMapOf<String, Int>()
        registers["w"] = 0
        registers["x"] = 0
        registers["y"] = 0
        registers["z"] = 0
        for (ins in program) {
            val op = ins[0]
            val reg1 = ins[1]
            val reg2 = if (ins.size == 3) ins[2] else null
            val value = if (reg2 == null) null else if (reg2.toIntOrNull() != null) reg2.toInt() else registers[reg2]
            //print("$op $reg1 ${reg2?:""} ${registers[reg1]} ${registers[reg2]?:""}")
            //if(op=="inp") println("$op $reg1")
            when (op) {
                "inp" -> registers[reg1] = number.remove()
                "add" -> registers[reg1] = registers[reg1]!! + value!!
                "mul" -> registers[reg1] = registers[reg1]!! * value!!
                "div" -> registers[reg1] = registers[reg1]!! / value!!
                "mod" -> registers[reg1] = registers[reg1]!! % value!!
                "eql" -> if (registers[reg1] == value) registers[reg1] = 1 else registers[reg1] = 0
            }
            //println(" -> ${registers[reg1]}")
            //if(op=="inp")println(registers["z"])
        }
        return registers["z"]!!
    }

    abstract class Value {
        open operator fun plus(other: Value): Value {
            if (other is SimpleValue) return other + this
            return simplify(PlusValue(this, other))
        }

        open operator fun times(other: Value): Value {
            if (other is SimpleValue) return other * this
            //if(other is PlusValue) return (this*other.left)+(this*other.right)
            return simplify(TimesValue(this, other))
        }

        open operator fun div(other: Value): Value {
            if (other is SimpleValue && other.value == 1) return this
            return simplify(DivValue(this, other))
        }

        open operator fun rem(other: Value): Value {
            return simplify(RemValue(this, other))
        }

        open infix fun eql(other: Value): Value {
            if (other is SimpleValue) return other eql this
            return simplify(EqlValue(this, other))
        }

        fun simplify(v: Value): Value {
            /*val possibleValues = v.possibleValues
            if (possibleValues?.size == 1) {
                return SimpleValue(possibleValues.first())
            }*/
            return v
        }

        abstract val possibleValues: Set<Int>?
    }

    data class SimpleValue(val value: Int) : Value() {
        override operator fun plus(other: Value): Value {
            if (value == 0) return other
            if (other is SimpleValue) return SimpleValue(value + other.value)
            return super.plus(other)
        }

        override operator fun times(other: Value): Value {
            if (value == 0) return SimpleValue(0)
            if (value == 1) return other
            if (other is SimpleValue) return SimpleValue(value * other.value)
            if (other is TimesValue && other.left is SimpleValue) return TimesValue(SimpleValue(value * other.left.value), other.right)
            if (other is TimesValue && other.right is SimpleValue) return TimesValue(SimpleValue(value * other.right.value), other.left)
            return super.times(other)
        }

        override operator fun div(other: Value): Value {
            if (value == 1) return other
            if (other is SimpleValue) return SimpleValue(value / other.value)
            return super.div(other)
        }

        override operator fun rem(other: Value): Value {
            if (value == 1) return other
            if (other is SimpleValue) return SimpleValue(value / other.value)
            return super.rem(other)
        }

        override infix fun eql(other: Value): Value {
            if (other is SimpleValue) return SimpleValue(if (value == other.value) 1 else 0)

            return super.eql(other)
        }

        override val possibleValues: Set<Int>
            get() = setOf(value)

        override fun toString(): String {
            return "$value"
        }
    }


    data class InputValue(val index: Int) : Value() {
        override val possibleValues: Set<Int>
            get() = inputs[index]?.let { setOf(it) } ?: _1To9

        override fun toString(): String {
            return "I$index"
        }
    }

    data class PlusValue(val left: Value, val right: Value) : Value() {
        override val possibleValues: Set<Int>? by lazy {
            //if(left.possibleValues==null || left.possibleValues!!.size>30 || right.possibleValues==null || right.possibleValues!!.size>30) null
            left.possibleValues!!.flatMap { l -> right.possibleValues!!.map { r -> l + r } }.toSet()
        }

        override fun toString(): String {
            return "($left + $right)"
        }
    }

    data class TimesValue(val left: Value, val right: Value) : Value() {
        override val possibleValues: Set<Int>? by lazy {
            //if(left.possibleValues==null || left.possibleValues!!.size>30 || right.possibleValues==null || right.possibleValues!!.size>30) null
            left.possibleValues!!.flatMap { l -> right.possibleValues!!.map { r -> l * r } }.toSet()
        }

        override fun toString(): String {
            return "($left * $right)"
        }
    }

    data class DivValue(val left: Value, val right: Value) : Value() {
        override val possibleValues: Set<Int>? by lazy {
            //if(left.possibleValues==null || left.possibleValues!!.size>30 || right.possibleValues==null || right.possibleValues!!.size>30) null
            left.possibleValues!!.flatMap { l -> right.possibleValues!!.map { r -> l / r } }.toSet()
        }

        override fun toString(): String {
            return "($left / $right)"
        }
    }

    data class RemValue(val left: Value, val right: Value) : Value() {
        override val possibleValues: Set<Int>? by lazy {
            //if(left.possibleValues==null || left.possibleValues!!.size>30 || right.possibleValues==null || right.possibleValues!!.size>30) null
            left.possibleValues!!.flatMap { l -> right.possibleValues!!.map { r -> l % r } }.toSet()
        }


        override fun toString(): String {
            return "($left % $right)"
        }
    }

    data class EqlValue(val left: Value, val right: Value) : Value() {
        override val possibleValues: Set<Int>? by lazy {
            //if(left.possibleValues==null || left.possibleValues!!.size>30 || right.possibleValues==null || right.possibleValues!!.size>30) null //zeroAndOne
            left.possibleValues!!.flatMap { l -> right.possibleValues!!.map { r -> if (l == r) 1 else 0 } }.toSet()
        }

        override fun toString(): String {
            return "($left == $right)"
        }
    }

    fun runProgramx(input: List<String>): Value {
        val program = input.map { it.split(" ") }
        var registers = mutableMapOf<String, Value>()
        registers["w"] = SimpleValue(0)
        registers["x"] = SimpleValue(0)
        registers["y"] = SimpleValue(0)
        registers["z"] = SimpleValue(0)
        var inpCount = 0
        var i = 0
        for (ins in program) {
            //println(i)
            val op = ins[0]
            val reg1 = ins[1]
            val reg2 = if (ins.size == 3) ins[2] else null
            val value = if (reg2 == null) null else if (reg2.toIntOrNull() != null) SimpleValue(reg2.toInt()) else registers[reg2]
            //print("$op $reg1 ${reg2?:""} ${registers[reg1]} ${registers[reg2]?:""}")
            //if(op=="inp") println("$op $reg1")
            when (op) {
                "inp" -> registers[reg1] = InputValue(inpCount++)
                "add" -> registers[reg1] = registers[reg1]!! + value!!
                "mul" -> registers[reg1] = registers[reg1]!! * value!!
                "div" -> registers[reg1] = registers[reg1]!! / value!!
                "mod" -> registers[reg1] = registers[reg1]!! % value!!
                "eql" -> registers[reg1] = registers[reg1]!! eql value!!
            }
            //println(" -> ${registers[reg1]}")
            //if (op == "inp") println(registers["z"])
            i++
        }
        return registers["z"]!!
    }

    fun runProgram(input: List<String>, number: String): Int {
        return runProgram(input, LinkedList(number.toList().map { it.toString().toInt() }))
    }

    fun solve(start: String): String? {
        for (i in start.indices) {
            inputs[i] = start[i].toString().toInt()
        }
        for (i in start.length..13) {
            inputs.remove(i)
        }
        if (!start.isEmpty()) {
            val z = runProgramx(input)
            if (!z.possibleValues!!.contains(0)) return null
        }
        if (start.length == 14) return start
        println(start)
        for (j in 9 downTo 1) {
            val res = solve(start + j)
            if (res != null) {
                return res
            }
        }
        return null
    }

    fun solve2(start: String): String? {
        for (i in start.indices) {
            inputs[i] = start[i].toString().toInt()
        }
        for (i in start.length..13) {
            inputs.remove(i)
        }
        if (!start.isEmpty()) {
            val z = runProgramx(input)
            if (!z.possibleValues!!.contains(0)) return null
        }
        if (start.length == 14) return start
        println(start)
        for (j in 1..9) {
            val res = solve2(start + j)
            if (res != null) {
                return res
            }
        }
        return null
    }

    fun part1() {
        val res = solve("")
        println(res)
    }


    fun part2() {
        val res = solve2("")
        println(res)
    }

    fun part1b() {
        val context = Context(mapOf("proof" to "true", "model" to "true"))
        val solver = context.mkOptimize()
        val solver2 = context.mkSolver()
        val zero = context.mkBV(0, 64)
        val one = context.mkBV(1, 64)
        val nine = context.mkBV(9, 64)
        val digits = (0..13).map {
            val d = context.mkBVConst("d$it", 64)
            solver.Add(context.mkBVSLE(one, d))
            solver2.add(context.mkBVSLE(one, d))
            solver.Add(context.mkBVSLE(d, nine))
            solver2.add(context.mkBVSLE(d, nine))
            d
        }
        val digitInput = digits.iterator()
        val registers = "wxyz".map { it to zero }.toMap<Char, BitVecExpr>().toMutableMap()
        for ((i, inst) in input.map { it.split(' ') }.withIndex()) {
            if (inst[0] == "inp") {
                registers[inst[1][0]] = digitInput.next()
                continue
            }
            val a = registers[inst[1][0]]!!
            val b = registers[inst[2][0]]?:context.mkBV(inst[2].toInt(), 64)
            val c = context.mkBVConst("v_$i", 64)
            when (inst[0]) {
                "add" -> {
                    solver.Add(context.mkEq(c, context.mkBVAdd(a, b)))
                    solver2.add(context.mkEq(c, context.mkBVAdd(a, b)))
                }
                "mul" -> {
                    solver.Add(context.mkEq(c, context.mkBVMul(a, b)))
                    solver2.add(context.mkEq(c, context.mkBVMul(a, b)))
                }
                "div" -> {
                    solver.Add(context.mkNot(context.mkEq(b, zero)))
                    solver2.add(context.mkNot(context.mkEq(b, zero)))
                    solver.Add(context.mkEq(c, context.mkBVSDiv(a, b)))
                    solver2.add(context.mkEq(c, context.mkBVSDiv(a, b)))
                }
                "mod" -> {
                    solver.Add(context.mkBVSGE(a, zero))
                    solver2.add(context.mkBVSGE(a, zero))
                    solver.Add(context.mkBVSGE(b, zero))
                    solver2.add(context.mkBVSGE(b, zero))
                    solver.Add(context.mkEq(c, context.mkBVSRem(a, b)))
                    solver2.add(context.mkEq(c, context.mkBVSRem(a, b)))
                }
                "eql" -> {
                    solver.Add(context.mkEq(c, context.mkITE(context.mkEq(a, b), one, zero)))
                    solver2.add(context.mkEq(c, context.mkITE(context.mkEq(a, b), one, zero)))
                }
            }
            registers[inst[1][0]] = c
        }

        solver.Add(context.mkEq(registers['z'], zero))
        solver2.add(context.mkEq(registers['z'], zero))
        println(solver)
        var toMax: BitVecExpr = zero
        for(i in 0..13){
            val d = digits[i]!!
            val expLong = Math.pow(10.0, 13.0 - i).toLong()
            val exp = context.mkBV(expLong, 64)!!
            val dfactor = context.mkBVMul(d, exp)
            toMax = context.mkBVAdd(toMax, dfactor)
        }
        val solution = solver.MkMaximize(toMax)
        //println(solver.Check())
        //println(solver2.check())
        //println(solver2.proof)
        /*for (expr in solver2.unsatCore) {
            println(expr)
        }*/
        println(solution.getValue())


    }
}

fun main(args: Array<String>) {
    val d = Day24()

    //d.part1()
    //d.part2()
    d.part1b()
}
