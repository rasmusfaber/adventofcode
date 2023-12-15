package xyz.faber.adventofcode.util

import org.hexworks.zircon.api.util.Function


fun <T> List<T>.findCycle(): Pair<Int, Int> {
  var p1 = this.size - 1
  var p2 = p1 - 1
  var maxLength = 0
  var maxCycleLength = 0
  var cycleLength = 1
  while (p2 >= p1 / 2) {
    var i = 0
    while (p2 >= i && this[p1 - i] == this[p2 - i]) {
      i++
    }
    if (i >= cycleLength) {
      if (i > maxLength) {
        maxLength = (i / cycleLength) * cycleLength
        maxCycleLength = cycleLength
      }
    }
    p2--
    cycleLength++
  }
  return Pair(this.size - maxLength - maxCycleLength, maxCycleLength)
}

// Brent's algorithm
fun <T> findCycle(x0: T, f: (T) -> T, memo: Boolean = false): Pair<Int, Int> {
  return if (memo) {
    findCycleMemo(x0, f)
  } else {
    findCycleNoMemo(x0, f)
  }
}

fun <T> findCycleNoMemo(x0: T, f: (T) -> T): Pair<Int, Int> {
  var tortoise = x0
  var hare = f(x0)
  var power = 1
  var lam = 1
  while (tortoise != hare) {
    if (power == lam) {
      tortoise = hare
      power *= 2
      lam = 0
    }
    hare = f(hare)
    lam++
  }
  tortoise = x0
  hare = x0
  for (i in 0 until lam) {
    hare = f(hare)
  }

  var mu = 0
  while (tortoise != hare) {
    tortoise = f(tortoise)
    hare = f(hare)
    mu++
  }
  return lam to mu
}

private fun <T> expandMemo(memo: MutableList<T>, f: (T) -> T, newSize: Int) {
  val oldSize = memo.size
  (oldSize until newSize).forEach { memo += f(memo[it - 1]) }
}

fun <T> findCycleMemo(x0: T, f: (T) -> T): Pair<Int, Int> {
  val memo = mutableListOf(x0, f(x0))
  var tortoise = 0
  var hare = 1
  var power = 1
  var lam = 1
  while (memo[tortoise] != memo[hare]) {
    if (power == lam) {
      tortoise = hare
      power *= 2
      lam = 0
      expandMemo(memo, f, 2 * power)
    }
    hare++
    lam++
  }
  tortoise = 0
  hare = lam

  var mu = 0
  while (memo[tortoise] != memo[hare]) {
    tortoise++
    hare++
    mu++
  }
  return lam to mu
}



