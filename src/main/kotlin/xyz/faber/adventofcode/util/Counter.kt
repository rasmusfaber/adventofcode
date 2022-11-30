package xyz.faber.adventofcode.util

abstract class Counter<T, N : Number>(private val default: N) : HashMap<T, N>() {
    override fun get(key: T): N {
        return super.get(key) ?: default
    }

    protected abstract fun add(a: N, b: N): N

    fun update(other: Counter<T, N>) {
        other.forEach {
            this.merge(it.key, it.value, this::add)
        }
    }
}

class IntCounter<T> : Counter<T, Int>(0) {
    override fun add(a: Int, b: Int): Int = a + b
}

class LongCounter<T> : Counter<T, Long>(0L) {
    override fun add(a: Long, b: Long): Long = a + b
}