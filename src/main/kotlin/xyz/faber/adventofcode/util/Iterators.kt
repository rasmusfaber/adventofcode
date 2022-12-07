package xyz.faber.adventofcode.util

inline fun <T> Iterable<T>.splitOn(predicate: (T) -> Boolean): List<List<T>> {
    val res = mutableListOf<List<T>>()
    var current = mutableListOf<T>()
    for (t in this) {
        if (predicate(t)) {
            res += current
            current = mutableListOf<T>()
        } else {
            current += t
        }
    }
    res += current
    return res
}

fun Iterable<String>.splitOnEmpty() = this.splitOn { it.isEmpty() }

fun <T> T.recursiveFlatten(getChildren: (T) -> Sequence<T>): Sequence<T> = sequence {
    yield(this@recursiveFlatten)
    for (child in getChildren(this@recursiveFlatten)) {
        yieldAll(child.recursiveFlatten(getChildren))
    }
}