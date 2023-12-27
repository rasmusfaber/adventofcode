package xyz.faber.adventofcode.year2023.day24

import com.microsoft.z3.Context
import com.microsoft.z3.IntNum
import com.microsoft.z3.Status
import org.jetbrains.kotlinx.multik.api.linalg.solve
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import xyz.faber.adventofcode.util.AdventRunner
import xyz.faber.adventofcode.util.AdventSolution
import xyz.faber.adventofcode.util.combinations
import kotlin.math.roundToLong

class HailStone(val px: Long, val py: Long, val pz: Long, val vx: Long, val vy: Long, val vz: Long) {
  override fun toString(): String {
    return "$px, $py, $pz @ $vx, $vy, $vz"
  }

  val pvector
    get() = mk.ndarray(mk[mk[px], mk[py], mk[pz]])

  val vvector
    get() = mk.ndarray(mk[mk[vx], mk[vy], mk[vz]])
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

fun crossproduct(a: NDArray<Long, D2>, b: NDArray<Long, D2>): NDArray<Long, D2> {
  return mk.ndarray(mk[mk[a[1][0] * b[2][0] - a[2][0] * b[1][0]], mk[a[2][0] * b[0][0] - a[0][0] * b[2][0]], mk[a[0][0] * b[1][0] - a[1][0] * b[0][0]]])
}

fun crossproductmatrix(a: NDArray<Long, D2>) = mk.ndarray(
  mk[
    mk[0L, -a[2][0], a[1][0]],
    mk[a[2][0], 0L, -a[0][0]],
    mk[-a[1][0], a[0][0], 0L]
  ]
)

class Day24 : AdventSolution<Long>() {
  override fun part1(input: List<String>): Long {
    val hailStones = input.map { parseHailStone(it) }
    return hailStones.combinations(2).map { it.toList() }.count { (a, b) -> intersects2D(a, b) }.toLong()
  }

  override fun part2(input: List<String>): Long {
    val hailStones = input.map { parseHailStone(it) }
    return solveWithLinAlg(hailStones)
  }

  private fun solveWithLinAlg(hailStones: List<HailStone>): Long {
    val h0 = hailStones[0]
    val h1 = hailStones[1]
    val h2 = hailStones[2]

    val b = (crossproduct(h0.pvector, h0.vvector) - crossproduct(h1.pvector, h1.vvector)).cat(
      crossproduct(h0.pvector, h0.vvector) - crossproduct(h2.pvector, h2.vvector)
    )

    val A00 = crossproductmatrix(h1.vvector - h0.vvector)
    val A10 = crossproductmatrix(h0.pvector - h1.pvector)
    val A01 = crossproductmatrix(h2.vvector - h0.vvector)
    val A11 = crossproductmatrix(h0.pvector - h2.pvector)
    val A = A00.cat(A10, 1).cat(A01.cat(A11, 1))
    val res = mk.linalg.solve(A, b)
    return res[0][0].roundToLong() + res[1][0].roundToLong() + res[2][0].roundToLong()
  }

  private fun solveWithZ3(hailStones: List<HailStone>): Long {
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
