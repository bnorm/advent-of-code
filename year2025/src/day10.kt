@file:Suppress("PackageDirectoryMismatch")

package aoc.day10

import aoc.math.*
import aoc.run
import utils.gcd
import kotlin.math.min

const val SAMPLE1 = """
[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() = run(
    year = 2025, day = 10,
    part1 = ::part1, sample1 = SAMPLE1, expected1 = "7",
    part2 = ::part2, sample2 = SAMPLE2, expected2 = "33",
)

private fun part1(input: String): String {
    val machines = input.trim().lines().map { Machine.parse(it) }
    return machines.sumOf { it.activate().size.toLong() }.toString()
}

private fun part2(input: String): String {
    val machines = input.trim().lines().map { Machine.parse(it) }

    var sum = 0L
    for (machine in machines) {
        sum += machine.joltage()
    }

    return sum.toString()
}

@JvmInline
value class Bitmask(val mask: Long) {
    constructor() : this(0)

    operator fun get(position: Int): Boolean {
        return (mask and (1L shl position)) > 0
    }

    fun xor(other: Bitmask): Bitmask {
        return Bitmask(mask xor other.mask)
    }

    fun cardinality(): Int {
        return mask.countOneBits()
    }

    override fun toString(): String {
        return mask.toString(2)
    }
}

private data class Machine(
    private val indicatorCount: Int,
    val indicators: Bitmask,
    val buttons: List<Bitmask>,
    val requirements: List<Int>,
) {
    companion object {
        private val pattern =
            """\[(?<indicators>[.#]+)] (?<buttons>(?:\([\d,]+\) )+)\{(?<requirements>[\d,]+)}""".toRegex()

        fun parse(input: String): Machine {
            val (indicatorsStr, buttonsStr, requirementsStr) = pattern.matchEntire(input)!!.destructured

            var indicators = 0L
            for (i in indicatorsStr.indices) {
                if (indicatorsStr[i] == '#') indicators = indicators or (1L shl i)
            }

            val buttons = buttonsStr.trim().split(" ").map { buttonStr ->
                var button = 0L
                for (i in buttonStr.drop(1).dropLast(1).split(",").map { it.toInt() }) {
                    button = button or (1L shl i)
                }
                Bitmask(button)
            }

            val requirements = requirementsStr.split(",").map { it.toInt() }

            return Machine(indicatorsStr.length, Bitmask(indicators), buttons, requirements)
        }
    }

    fun activate(): List<Bitmask> {
        val steps = search(
            start = Bitmask(),
            distance = { node -> if (node.xor(indicators).cardinality() == 0) 0L else 1L },
            neighbors = { _ -> buttons.map { it to 1L } },
            next = { node, edge -> node.xor(edge) }
        )
        return steps
    }

    fun joltage(): Int {
        // Reduce buttons and requirements with linear algebra.
        val m = Matrix(
            values = requirements.indices.map { r ->
                buttons.map { if (it[r]) 1 else 0 }.toTypedArray()
            }.toTypedArray()
        )
        val b = Vector(requirements.toTypedArray())
        reduce(m, b)

        // Sort buttons by the first non-zero index and then by the value at index.
        val comparator = Comparator<Vector<Int>> { a, b ->
            val firstA = a.indexOfFirst { it > 0 }
            val firstB = b.indexOfFirst { it > 0 }
            when {
                firstA != firstB -> compareValues(firstA, firstB)
                firstA == -1 -> 0
                else -> compareValues(a[firstA], b[firstB])
            }
        }

        val queue = ArrayDeque(m.columns.map { m.column(it) }.sortedWith(comparator))
        val remaining = b.toList().toIntArray()

        // DFS reduced buttons and requirements.
        fun recurse(total: Int): Int {
            // End condition.
            if (remaining.all { it == 0 }) return total

            val next = queue.removeFirst()
            try {
                var minPresses = -1
                for (i in remaining.indices) {
                    if (next[i] == 0) continue

                    // Check if this button is the last possible button to meet any requirements.
                    if (queue.all { it[i] == 0 }) {
                        // Check the requirement is evenly dividable by the button.
                        if (remaining[i] % next[i] != 0) return -1 // impossible

                        val min = remaining[i] / next[i]

                        // Check min presses is equal to other exclusive requirements.
                        if (minPresses != -1 && minPresses != min) return -1 // impossible
                        minPresses = min
                    }
                }
                if (minPresses == -1) minPresses = 0

                var maxPresses = Int.MAX_VALUE
                for (i in remaining.indices) {
                    val m = next[i]
                    if (m == 0) continue
                    maxPresses = min(maxPresses, remaining[i] / m)
                }

                var minimumSolution = Int.MAX_VALUE
                for (presses in minPresses..maxPresses) {
                    for (i in remaining.indices) {
                        remaining[i] -= next[i] * presses
                    }

                    // TODO can we early exit somehow?
                    val solution = recurse(total + presses)
                    if (solution != -1) minimumSolution = min(solution, minimumSolution)

                    for (i in remaining.indices) {
                        remaining[i] += next[i] * presses
                    }
                }

                return if (minimumSolution == Int.MAX_VALUE) -1 else minimumSolution
            } finally {
                queue.addFirst(next)
            }
        }

        val solution = recurse(0)
        if (solution == -1) error("! unsolvable !\n$this")
        return solution
    }

    private fun Bitmask.asIndicator(): String = buildString {
        val bitmask = this@asIndicator
        append("[")
        for (i in 0..<indicatorCount) {
            when (bitmask[i]) {
                true -> append("#")
                false -> append(".")
            }
        }
        append("]")
    }

    private fun Bitmask.asButton(): String =
        (0..<indicatorCount).filter { this[it] }.joinToString(",", "(", ")")

    override fun toString(): String = buildString {
        append(indicators.asIndicator())
        append(" ")
        append(buttons.joinToString(" ") { it.asButton() })
        append(" ")
        append(requirements.joinToString(",", "{", "}"))
    }
}

// TODO can optimize for this particular day...
fun <N, E> search(
    start: N,
    distance: (N) -> Long,
    neighbors: (N) -> List<Pair<E, Long>>,
    next: (N, E) -> N?,
): List<E> {
    class SearchNode(
        val node: N,
        val path: List<E>,
        val cost: Long,
    ) : Comparable<SearchNode> {
        val distance: Long = distance(node)
        override fun compareTo(other: SearchNode): Int =
            compareValues(this.cost + this.distance, other.cost + other.distance)
    }

    val queue = java.util.PriorityQueue<SearchNode>()
    queue.add(SearchNode(start, listOf(), 0L))

    val visited = mutableMapOf<N, SearchNode>()
    while (queue.isNotEmpty()) {
        val current = queue.poll()
        if (current.distance == 0L) return current.path

        val neighbors = neighbors(current.node).mapNotNull { (edge, cost) ->
            val next = next(current.node, edge) ?: return@mapNotNull null
            SearchNode(next, current.path + edge, current.cost + cost)
        }
        for (next in neighbors) {
            val existing = visited[next.node]
            if (existing != null) {
                if (next.cost >= existing.cost) continue
                queue.remove(existing)
            }

            visited[next.node] = next
            queue.add(next)
        }
    }

    error("!")
}

private fun reduce(m: Matrix<Int>, b: Vector<Int>) {
    require(m.rows.last + 1 == b.size)

    // Calculate and divide by row GCD
    fun normalize(r: Int, c: Int) {
        var gcd = b[r]
        for (c in m.columns) {
            gcd = gcd(gcd, m[r, c])
        }
        if (gcd == 0) return // cannot normalize a row of all 0s.

        // Attempt to normalize gcd-sign to the diagonal value-sign.
        if (m.contains(r, c) && (m[r, c] < 0 && gcd > 0 || m[r, c] > 0 && gcd < 0)) {
            gcd = -gcd
        }

        if (gcd != 1) {
            b[r] /= gcd
            for (c in m.columns) {
                m[r, c] /= gcd
            }
        }
    }

    // Check if a row swap is required because of a zero-value in the current diagonal.
    fun checkSwap(r: Int, c: Int): Boolean {
        if (m[r, c] == 0) {
            if (r == m.rows.last) return false // error("! unsolvable t=$t !")

            // Swap rows with a later, non-zero, row.
            for (t in r + 1..m.rows.last) {
                if (m[t, c] != 0) {
                    b.swap(r, t)
                    m.row(r).swap(m.row(t))
                    return true
                }
            }

            return false
        }
        return true
    }

    // Subtract row 'row' from row 't' using GCD at column 'c'.
    fun subtract(row: Int, t: Int, c: Int) {
        val gcd = gcd(m[row, c], m[t, c])
        val u = m[row, c] / gcd
        val v = m[t, c] / gcd

        b[t] = b[t] * u - b[row] * v
        for (c in m.columns) {
            m[t, c] = m[t, c] * u - m[row, c] * v
        }
    }

    run {
        var row = 0
        var column = 0
        while (column in m.columns && row in m.rows) {
            if (checkSwap(row, column)) {
                normalize(row, row)
                for (t in m.rows) {
                    if (t != row) {
                        subtract(row, t, column)
                        normalize(t, t)
                    }
                }
                row++
            }
            column++
        }
    }

    // Remove negative numbers.
    for (c in m.columns) {
        for (r in m.rows) {
            if (m[r, c] < 0) {
                run {
                    for (t in m.rows) {
                        if (m[t, c] > 0) {
                            subtract(t, r, c)
                            normalize(r, r)
                            return@run
                        }
                    }
                    normalize(r, c)
                }
            }
        }
    }

    // Double-check for negative numbers.
    for (r in m.rows) {
        for (c in m.columns) {
            if (m[r, c] < 0) {
                println(m)
                error("! precondition !")
            }
        }
    }
}

private fun <T> Vector<T>.swap(a: Int, b: Int) {
    val tmp = this[a]
    this[a] = this[b]
    this[b] = tmp
}

private fun <T> Vector<T>.swap(v: Vector<T>) {
    require(size == v.size)
    for (i in 0..<size) {
        val tmp = this[i]
        this[i] = v[i]
        v[i] = tmp
    }
}
