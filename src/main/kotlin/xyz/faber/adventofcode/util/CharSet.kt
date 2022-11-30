package xyz.faber.adventofcode.util

class CharSet internal constructor(internal val bits: Long) : AbstractSet<Char>() {
    constructor() : this(0L)
    constructor(charSet: CharSet) : this(charSet.bits)
    constructor(mutableCharSet: MutableCharSet) : this(mutableCharSet.bits)

    override val size: Int
        get() = java.lang.Long.bitCount(bits)

    override fun contains(element: Char): Boolean {
        return (bits and element.asMask) != 0L
    }

    override fun containsAll(elements: Collection<Char>): Boolean {
        val mask = elements.asMask
        return (bits and mask) == mask
    }

    override fun isEmpty(): Boolean {
        return bits == 0L
    }

    override fun iterator(): Iterator<Char> {
        return object : Iterator<Char> {
            private var i = nextBit(bits, 0)

            override fun hasNext(): Boolean {
                return i < java.lang.Long.SIZE
            }

            override fun next(): Char {
                if (i >= java.lang.Long.SIZE) {
                    throw NoSuchElementException("No more elements")
                }
                val res = i.charValue
                i = nextBit(bits, i + 1)
                return res
            }

        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other is CharSet) {
            return this.bits == other.bits
        }
        if (other is MutableCharSet) {
            return this.bits == other.bits
        }
        return super.equals(other)
    }

    override fun toString(): String = this.toSet().toString()

    operator fun plus(c: Char) = CharSet(bits or c.asMask)

    operator fun minus(c: Char) = CharSet(bits and c.asMask.inv())

    operator fun plus(elements: Iterable<Char>) = CharSet(bits or elements.asMask)

    operator fun minus(elements: Iterable<Char>) = CharSet(bits and elements.asMask.inv())
}

class MutableCharSet internal constructor(internal var bits: Long) : AbstractMutableSet<Char>() {
    constructor() : this(0L)
    constructor(charSet: CharSet) : this(charSet.bits)
    constructor(mutableCharSet: MutableCharSet) : this(mutableCharSet.bits)

    override val size: Int
        get() = java.lang.Long.bitCount(bits)

    override fun add(c: Char): Boolean {
        val mask = c.asMask
        val res = (bits and mask) == 0L
        bits = bits or mask
        return res
    }

    override fun addAll(elements: Collection<Char>): Boolean {
        val mask = elements.asMask
        val newbits = bits or elements.asMask
        val res = newbits == bits
        bits = newbits
        return res
    }

    override fun clear() {
        bits = 0
    }

    override fun iterator(): MutableIterator<Char> {
        return object : MutableIterator<Char> {
            private var i = nextBit(bits, 0)
            private var last = -1

            override fun hasNext(): Boolean {
                return i < java.lang.Long.SIZE
            }

            override fun next(): Char {
                if (i >= java.lang.Long.SIZE) {
                    throw NoSuchElementException("No more elements")
                }
                val res = i.charValue
                last = i
                i = nextBit(bits, i + 1)
                return res
            }

            override fun remove() {
                if (i == -1) {
                    throw NoSuchElementException("No element to remove")
                }
                bits = bits and i.asMask.inv()
            }
        }
    }

    override fun remove(c: Char): Boolean {
        val mask = c.asMask
        val res = (bits and mask) == 1L
        bits = bits and mask.inv()
        return res
    }

    override fun removeAll(elements: Collection<Char>): Boolean {
        val mask = elements.asMask
        val newbits = bits and elements.asMask.inv()
        val res = newbits == bits
        bits = newbits
        return res
    }

    override fun retainAll(elements: Collection<Char>): Boolean {
        val mask = elements.asMask
        val newbits = bits and elements.asMask
        val res = newbits == bits
        bits = newbits
        return res
    }

    override fun contains(element: Char): Boolean {
        return (bits and element.asMask) != 0L
    }

    override fun containsAll(elements: Collection<Char>): Boolean {
        val mask = elements.asMask
        return (bits and mask) == mask
    }

    override fun isEmpty(): Boolean {
        return bits == 0L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other is CharSet) {
            return this.bits == other.bits
        }
        if (other is MutableCharSet) {
            return this.bits == other.bits
        }
        return super.equals(other)
    }

    override fun toString(): String = this.toSet().toString()

    operator fun plus(c: Char) = CharSet(bits or c.asMask)

    operator fun minus(c: Char) = CharSet(bits and c.asMask.inv())

    operator fun plus(elements: Iterable<Char>) = CharSet(bits or elements.asMask)

    operator fun minus(elements: Iterable<Char>) = CharSet(bits and elements.asMask.inv())


}

private val Int.charValue: Char
    get() = when (this) {
        in 0..9 -> '0' + this
        in 10..35 -> 'a' + this - 10
        in 36..61 -> 'A' + this - 36
        else -> throw IllegalArgumentException("Only 0..61. Was $this")
    }

private val Char.asMask: Long
    get() = when (this) {
        in '0'..'9' -> 0x1L shl (this - '0')
        in 'a'..'z' -> 0x1L shl (this - 'a' + 10)
        in 'A'..'Z' -> 0x1L shl (this - 'A' + 36)
        else -> throw IllegalArgumentException("Only 0-9, a-z and A-Z. Was $this")
    }

private val Iterable<Char>.asMask: Long
    get() = this.map { it.asMask }.fold(0, Long::or)

private val CharArray.asMask: Long
    get() = this.map { it.asMask }.fold(0, Long::or)

private val Int.asMask: Long
    get() = 0x1L shl this

private fun nextBit(v: Long, i: Int): Int {
    var res = i
    while ((v and res.asMask) == 0L && res < java.lang.Long.SIZE) res++
    return res
}

inline fun charSetOf() = CharSet()

fun charSetOf(vararg elements: Char) =
        if (elements.isEmpty()) CharSet() else CharSet(elements.asMask)

inline fun mutableCharSetOf() = MutableCharSet()

fun mutableCharSetOf(vararg elements: Char) =
        if (elements.isEmpty()) MutableCharSet() else MutableCharSet(elements.asMask)
