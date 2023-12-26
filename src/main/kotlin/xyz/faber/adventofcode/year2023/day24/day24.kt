package xyz.faber.adventofcode.year2023.day24

import com.microsoft.z3.BitVecNum
import com.microsoft.z3.Context
import com.microsoft.z3.IntExpr
import com.microsoft.z3.IntNum
import com.microsoft.z3.Status
import org.jetbrains.kotlinx.multik.api.linalg.solve
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.combinations

class HailStone(val px: Long, val py: Long, val pz: Long, val vx: Long, val vy: Long, val vz: Long) {
  override fun toString(): String {
    return "$px, $py, $pz @ $vx, $vy, $vz"
  }
}

fun parseHailStone(s: String): HailStone {
  return s.split(" @ ").map { it.split(", ").map { it.trim().toLong() } }
    .let { (p, v) -> HailStone(p[0], p[1], p[2], v[0], v[1], v[2]) }
}

fun intersects2D(a: HailStone, b: HailStone): Boolean {
  val A = mk.ndarray(mk[mk[a.vx, -b.vx], mk[a.vy, -b.vy]])
  val p = mk.ndarray(mk[mk[b.px - a.px], mk[b.py - a.py]])
  val t: NDArray<Double, D2>
  try {
    t = mk.linalg.solve(A, p)
  } catch (e: Exception) {
    return false
  }
  val t1 = t[0, 0]
  val t2 = t[1, 0]
  if (t1 < 0 || t2 < 0) {
    return false
  }

  val x = a.px + t1 * a.vx
  val y = a.py + t1 * a.vy
  return x in 2e14..4e14 && y in 2e14..4e14
}

class Day24 : AdventSolution<Long>() {
  override fun part1(input: List<String>): Long {
    val hailStones = input.map { parseHailStone(it) }
    return hailStones.combinations(2).map { it.toList() }.count { (a, b) -> intersects2D(a, b) }.toLong()
  }

  override fun part2(input: List<String>): Long {
    return solveWithInt(input)
  }

  private fun solveWithBitvec(input: List<String>): Long {
    val hailStones = input.map { parseHailStone(it) }
    val context = Context(mapOf("proof" to "true", "model" to "true"))
    val solver = context.mkSolver()
    val x = context.mkBVConst("x", 64)
    val y = context.mkBVConst("y", 64)
    val z = context.mkBVConst("z", 64)
    val vx = context.mkBVConst("vx", 64)
    val vy = context.mkBVConst("vy", 64)
    val vz = context.mkBVConst("vz", 64)
    val zero = context.mkBV(0, 64)
    for ((i, h) in hailStones.withIndex().take(3)) {
      val ti = context.mkBVConst("t$i", 64)
      solver.add(context.mkBVSGE(ti, zero))
      val xi = context.mkBV(h.px, 64)
      val vxi = context.mkBV(h.vx, 64)
      solver.add(
        context.mkEq(
          context.mkBVAdd(x, context.mkBVMul(ti, vx)),
          context.mkBVAdd(xi, context.mkBVMul(ti, vxi))
        )
      )
      val yi = context.mkBV(h.py, 64)
      val vyi = context.mkBV(h.vy, 64)
      solver.add(
        context.mkEq(
          context.mkBVAdd(y, context.mkBVMul(ti, vy)),
          context.mkBVAdd(yi, context.mkBVMul(ti, vyi))
        )
      )
      val zi = context.mkBV(h.pz, 64)
      val vzi = context.mkBV(h.vz, 64)
      solver.add(
        context.mkEq(
          context.mkBVAdd(z, context.mkBVMul(ti, vz)),
          context.mkBVAdd(zi, context.mkBVMul(ti, vzi))
        )
      )
      }

    val status = solver.check()
    if (status != Status.SATISFIABLE) {
      println("Not satisfiable")
      throw IllegalArgumentException("Not satisfiable")
      }
    val model = solver.model

    val xsolution = (model.evaluate(x, false) as BitVecNum).getLong()
    val ysolution = (model.evaluate(y, false) as BitVecNum).getLong()
    val zsolution = (model.evaluate(z, false) as BitVecNum).getLong()
    return xsolution + ysolution + zsolution
  }

  private fun solveWithInt(input: List<String>): Long {
    val hailStones = input.map { parseHailStone(it) }
    val context = Context(mapOf("proof" to "true", "model" to "true"))
    val solver = context.mkSolver()
    val x = context.mkIntConst("x")
    val y = context.mkIntConst("y")
    val z = context.mkIntConst("z")
    val vx = context.mkIntConst("vx")
    val vy = context.mkIntConst("vy")
    val vz = context.mkIntConst("vz")
    val zero = context.mkInt(0)
    for ((i, h) in hailStones.withIndex().take(3)) {
      val ti = context.mkIntConst("t$i")
      solver.add(context.mkGe(ti, zero))
      val xi = context.mkInt(h.px)
      val vxi = context.mkInt(h.vx)
      solver.add(
        context.mkEq(
          context.mkAdd(x, context.mkMul(ti, vx)),
          context.mkAdd(xi, context.mkMul(ti, vxi))
        )
      )
      val yi = context.mkInt(h.py)
      val vyi = context.mkInt(h.vy)
      solver.add(
        context.mkEq(
          context.mkAdd(y, context.mkMul(ti, vy)),
          context.mkAdd(yi, context.mkMul(ti, vyi))
        )
      )
      val zi = context.mkInt(h.pz)
      val vzi = context.mkInt(h.vz)
      solver.add(
        context.mkEq(
          context.mkAdd(z, context.mkMul(ti, vz)),
          context.mkAdd(zi, context.mkMul(ti, vzi))
        )
      )
    }

    val status = solver.check()
    if (status != Status.SATISFIABLE) {
      println("Not satisfiable")
      throw IllegalArgumentException("Not satisfiable")
    }
    val model = solver.model

    val xsolution = (model.evaluate(x, false) as IntNum).int64
    val ysolution = (model.evaluate(y, false) as IntNum).int64
    val zsolution = (model.evaluate(z, false) as IntNum).int64
    return xsolution + ysolution + zsolution
  }


}

fun main(args: Array<String>) {
  AdventRunner(2023, 24, Day24()).run()

}
