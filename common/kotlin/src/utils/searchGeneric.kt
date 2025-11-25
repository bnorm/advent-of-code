package utils

import java.util.*

fun <T> search(
    start: T,
    distance: (T) -> Long,
    neighbors: (T) -> List<Pair<T, Long>>,
): List<T> {
    class SearchNode(val value: T, val cost: Long) : Comparable<SearchNode> {
        val distance: Long = distance(value)

        override fun compareTo(other: SearchNode): Int =
            compareValues(this.cost + this.distance, other.cost + other.distance)
    }


    val visited = mutableMapOf<T, MutableSet<SearchNode>>()
    val queue = PriorityQueue<SearchNode>()
    queue.add(SearchNode(start, 0))

    val best = mutableListOf<SearchNode>()
    while (queue.isNotEmpty()) {
        val current = queue.poll()
        if (current.distance == 0L) {
            if (best.isNotEmpty() && best.first() < current) break
            best.add(current)
            continue
        }

        for ((next, cost) in neighbors(current.value)) {
            val node = SearchNode(next, current.cost + cost)

            val existing = visited.getOrPut(next) { mutableSetOf() }
            if (existing.isNotEmpty()) {
                val first = existing.first()
                if (node > first) continue
                if (node < first) {
                    queue.removeAll(existing)
                    existing.clear()
                }
            }

            existing.add(node)
            queue.add(node)
        }
    }

    require(best.isNotEmpty()) { "!" }
    return best.map { it.value }
}
