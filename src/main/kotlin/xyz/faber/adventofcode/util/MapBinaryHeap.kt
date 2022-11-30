package xyz.faber.adventofcode.util

import com.google.common.collect.Iterators


private const val TOP = 0 // the index of the top of the heap

class HeapPriorityQueue<T> : Collection<T> {
    private val heap = ArrayList<T>() // holds the heap as an implicit binary tree
    private val objectIds = HashMap<T, Int>() // maps each object to an id
    private val objectIndices = HashMap<Int, Int>() // maps each id to its index in the heap
    private val reverseObjectIndices = HashMap<Int, Int>() // maps each index to its id
    private val priorities = ArrayList<Int>() // priorities with same indices as in heap
    private var nextId = 0

    override fun isEmpty() = heap.isEmpty()

    fun clear() {
        priorities.clear()
        objectIds.clear()
        objectIndices.clear()
        reverseObjectIndices.clear()
        heap.clear()
    }

    fun add(o: T, priority: Int) {
        val lastIndex = heap.size
        priorities.add(priority)
        heap.add(o)
        val id = nextId++
        if (nextId == Int.MAX_VALUE) {
            throw RuntimeException("Too many values in priority queue")
        }
        objectIds[o] = id
        percolateUp(lastIndex, o, id, priority)
    }

    fun peek(): T? {
        return if (heap.size > 0) {
            heap[TOP]
        } else {
            null
        }
    }

    override val size: Int
        get() = heap.size


    fun addOrUpdate(o: T, priority: Int) {
        val id = objectIds[o]
        if (id == null) {
            add(o, priority)
        } else {
            val cur = objectIndices[id]!!
            val oldpriority = priorities[cur]
            if (oldpriority > priority) {
                percolateUp(cur, o, id, priority)
            } else {
                priorities[cur] = priority
                percolateDown(cur)
            }
        }
    }

    override operator fun contains(o: T) = objectIds.containsKey(o)

    override fun containsAll(elements: Collection<T>) = elements.all { this.contains(it) }

    private tailrec fun percolateDown(cur: Int) {
        val left = lChild(cur)
        val right = rChild(cur)
        var smallest: Int

        if (left < heap.size && priorities[left] < priorities[cur]) {
            smallest = left
        } else {
            smallest = cur
        }

        if (right < heap.size && priorities[right] < priorities[smallest]) {
            smallest = right
        }

        if (cur != smallest) {
            swap(cur, smallest)
            percolateDown(smallest)
        }
    }

    /**
     * Moves the element `o` at position `cur` as high as it can go in the heap.
     */
    private fun percolateUp(cur: Int, o: T, id: Int, priority: Int) {
        var i = cur
        var parenti = parent(i)

        while (i > TOP && priorities[parenti] > priority) {
            val parent = heap[parenti]
            val parentid = reverseObjectIndices[parenti]!!
            heap[i] = parent
            priorities[i] = priorities[parenti]
            objectIndices[parentid] = i // reset index to i (new location)
            reverseObjectIndices[i] = parentid
            i = parenti
            parenti = parent(i)
        }

        // place object in heap at appropriate place
        objectIndices[id] = i
        reverseObjectIndices[i] = id
        heap[i] = o
        priorities[i] = priority
    }

    /** Returns the index of the left child of the element at index `i` of the heap.  */
    private fun lChild(i: Int): Int {
        return (i shl 1) + 1
    }

    /** Returns the index of the right child of the element at index `i` of the heap.  */
    private fun rChild(i: Int): Int {
        return (i shl 1) + 2
    }

    /** Returns the index of the parent of the element at index `i` of the heap.  */
    private fun parent(i: Int): Int {
        return i - 1 shr 1
    }

    /**
     * Swaps the positions of the elements at indices `i` and `j` of the heap.
     */
    private fun swap(i: Int, j: Int) {
        val iElt = heap[i]
        val iPri = priorities[i]
        val iId = reverseObjectIndices[i]!!
        val jElt = heap[j]
        val jPri = priorities[j]
        val jId = reverseObjectIndices[j]!!

        heap[i] = jElt
        priorities[i] = jPri
        objectIndices[jId] = i
        reverseObjectIndices[i] = jId

        heap[j] = iElt
        priorities[j] = iPri
        objectIndices[iId] = j
        reverseObjectIndices[j] = iId
    }

    /** Returns an `Iterator` that does not support modification of the heap.  */
    override fun iterator(): MutableIterator<T> {
        return Iterators.unmodifiableIterator(heap.iterator())
    }

    fun element(): T = peek() ?: throw NoSuchElementException()

    fun offer(o: T, priority: Int) = add(o, priority)

    fun poll(): T? {
        val top = this.peek()
        if (top != null) {
            val lastIndex = heap.size - 1
            val bottomElt = heap[lastIndex]
            val bottomId = reverseObjectIndices[lastIndex]!!
            val topId = reverseObjectIndices[TOP]!!
            heap[TOP] = bottomElt
            priorities[TOP] = priorities[lastIndex]
            objectIndices[bottomId] = TOP
            reverseObjectIndices[TOP] = bottomId

            heap.removeAt(lastIndex) // remove the last element
            priorities.removeAt(lastIndex)
            if (heap.size > 1) {
                percolateDown(TOP)
            }

            objectIds.remove(top)
            objectIndices.remove(topId)
            reverseObjectIndices.remove(lastIndex)
        }
        return top
    }

    fun remove(): T = poll() ?: throw NoSuchElementException()
}