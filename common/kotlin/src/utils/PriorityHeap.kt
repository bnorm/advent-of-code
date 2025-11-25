package utils

class PriorityHeap<T : Comparable<T>> {
    interface Entry<T> {
        var value: T
    }

    private var heap: Array<PriorityHeap<T>.Node?> = arrayOfNulls<PriorityHeap<T>.Node>(1024)
    var size = 0
        private set

    private fun grow() {
        heap = heap.copyOf(heap.size * 2)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getAt(i: Int): Node = heap[i]!!

    private fun setAt(i: Int, node: Node?) {
        heap[i] = node
        node?.index = i
    }

    fun add(value: T): Entry<T> {
        val node = Node(value, -1)
        val i = size
        if (i >= heap.size) grow()
        siftUp(i, node)
        size = i + 1
        return node
    }

    fun poll(): T {
        if (size == 0) throw NoSuchElementException()
        val result = getAt(0)

        val i = --size
        val last = getAt(i)
        setAt(i, null)
        if (i > 0) siftDown(0, last)
        return result.value
    }

    private fun removeAt(index: Int) {
        val last = --size
        if (last == index) // Removed last element.
            setAt(index, null)
        else {
            val node = getAt(last)
            setAt(last, null)
            sift(index, node)
        }
    }

    private fun sift(index: Int, node: Node) {
        siftDown(index, node)
        if (getAt(index) === node) {
            siftUp(index, node)
        }
    }

    private fun siftUp(index: Int, node: Node) {
        var i = index
        while (i > 0) {
            val parent = (i - 1) ushr 1
            val e = getAt(parent)
            if (node.value >= e.value) break
            setAt(i, e)
            i = parent
        }
        setAt(i, node)
    }

    private fun siftDown(index: Int, node: Node) {
        var i = index
        val half = size ushr 1 // Loop while a non-leaf.
        while (i < half) {
            var childIndex = (i shl 1) + 1 // Assume left child is least.
            var child = getAt(childIndex)

            val right = childIndex + 1
            if (right < size) {
                val rightChild = getAt(right)
                if (child.value > rightChild.value) {
                    child = rightChild
                    childIndex = right
                }
            }

            if (node.value <= child.value) break
            setAt(i, child)
            i = childIndex
        }

        setAt(i, node)
    }

    private inner class Node(
        value: T,
        var index: Int,
    ) : Entry<T> {
        override var value: T = value
            set(value) {
                field = value
                sift(index, this)
            }
    }
}
