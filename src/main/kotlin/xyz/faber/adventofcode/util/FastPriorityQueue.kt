package xyz.faber.adventofcode.util

import java.util.*

// Priority Queue with O(logN) remove
class FastPriorityQueue<T>(val priorityFunc: (T) -> Int) : Queue<T> {
    private val inner = TreeSet<Node<T>>(::nodeComparator)

    private fun node(v: T): Node<T> = Node(priorityFunc(v), v)

    private fun nodes(vs: Collection<T>): Collection<Node<T>> = vs.map(::node)

    override fun contains(element: T) = inner.contains(node(element))

    override fun addAll(elements: Collection<T>) = inner.addAll(nodes(elements))

    override fun clear() = inner.clear()

    override fun element() = inner.first().v

    override fun isEmpty() = inner.isEmpty()

    override fun remove(): T {
        val first = inner.first()
        inner.remove(first)
        return first.v
    }

    override val size: Int
        get() = inner.size

    override fun containsAll(elements: Collection<T>) = inner.containsAll(nodes(elements))

    override fun iterator(): MutableIterator<T> {
        val innerIterator = inner.iterator()
        return object : MutableIterator<T> {
            override fun hasNext(): Boolean = innerIterator.hasNext()

            override fun next(): T {
                val next = innerIterator.next()
                return next.v
            }

            override fun remove() = innerIterator.remove()

        }
    }

    override fun remove(element: T) = inner.remove(node(element))

    override fun removeAll(elements: Collection<T>) = inner.removeAll(nodes(elements))

    override fun add(element: T): Boolean = inner.add(node(element))

    override fun offer(e: T): Boolean = add(e)

    override fun retainAll(elements: Collection<T>) = inner.retainAll(nodes(elements))

    override fun peek(): T? {
        if (isNullOrEmpty()) {
            return null
        }
        val res = inner.first()
        return res.v
    }

    override fun poll(): T? {
        if (isNullOrEmpty()) {
            return null
        }
        val res = inner.first()
        inner.remove(res)
        return res.v
    }

    private val uniqueIds = mutableMapOf<T, Int>()
    private var nextUniqueId = 0
    private fun getUniqueId(v: T): Int = uniqueIds.computeIfAbsent(v, {nextUniqueId++})


    private fun nodeComparator(n1: Node<T>, n2: Node<T>): Int {
        val c1 = Integer.compare(n1.priority, n2.priority)
        if (c1 != 0) {
            return c1
        }
        if (n1.v == n2.v) {
            return 0
        }
        /*val c2 = Integer.compare(n1.v.hashCode(), n2.v.hashCode())
        if (c2 != 0) {
            return c2
        }
        if (n1.v is Comparable<*> && n2.v is Comparable<*>) {
            return n1.v.compareTo(n2.v as Nothing)
        }*/
        return Integer.compare(getUniqueId(n1.v), getUniqueId(n2.v))
    }
}

private data class Node<T>(val priority: Int, val v: T)
