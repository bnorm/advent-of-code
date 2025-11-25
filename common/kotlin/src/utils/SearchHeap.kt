package utils

class SearchHeap<T : Comparable<T>> {
    private val map = mutableMapOf<T, PriorityHeap.Entry<T>>()
    private var heap = PriorityHeap<T>()

    val size: Int get() = heap.size

    fun add(value: T): Boolean {
        val entry = map[value]
        if (entry == null) {
            map[value] = heap.add(value)
            return true
        } else {
            if (entry.value <= value) return false
            entry.value = value
            return true
        }
    }

    fun poll(): T {
        if (heap.size == 0) throw NoSuchElementException()
        val value = heap.poll()
        map.remove(value)
        return value
    }
}