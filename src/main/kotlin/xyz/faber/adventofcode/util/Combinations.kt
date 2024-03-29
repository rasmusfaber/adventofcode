package xyz.faber.adventofcode.util

import com.google.common.collect.Sets

fun <T> Collection<T>.combinations(combinationSize: Int): Set<Set<T>> = when {
    combinationSize < 0 -> throw Error("combinationSize cannot be smaller then 0. It is equal to $combinationSize")
    combinationSize == 0 -> setOf(setOf())
    combinationSize == 1 -> this.map { setOf(it) }.toSet()
    combinationSize >= size -> setOf(toSet())
    else -> Sets.combinations(this.toSet(), combinationSize)
}

fun <T> Collection<T>.powerset(): Set<Set<T>> = powerset(this, setOf(setOf()))

private tailrec fun <T> powerset(left: Collection<T>, acc: Set<Set<T>>): Set<Set<T>> = when {
    left.isEmpty() -> acc
    else -> powerset(left.drop(1), acc + acc.map { it + left.first() })
}

fun <T> Collection<T>.powercombo(combinationSize: Int): Set<List<T>> = when {
    combinationSize < 0 -> throw Error("combinationSize cannot be smaller then 0. It is equal to $combinationSize")
    combinationSize == 0 -> setOf(listOf())
    else -> powercombo(combinationSize - 1)
            .flatMap(fun(combo: List<T>): List<List<T>> {
                return this.map { listOf(it) + combo }
            })
            .toSet()
}
