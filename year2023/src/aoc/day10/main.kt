package aoc.day10

import utils.*

fun main() {
    val input = readInput("aoc/day10/input.txt")
    val sample1 = readInput("aoc/day10/sample1.txt")
    val sample2 = readInput("aoc/day10/sample2.txt")

    require(part1(sample1) == "8")
    println(part1(input))

    require(part2(sample2) == "8")
    println(part2(input))
}

fun part1(input: List<String>): String {
    val island = Island.parse(input)

    var count = 0
    val (left, right) = island.follow()
    while (left.hasNext() && right.hasNext()) {
        count++
        if (left.next() == right.next()) break
    }

    return count.toString()
}

fun part2(input: List<String>): String {
    val island = Island.parse(input)

    val path = mutableSetOf(island.starting)
    run {
        val (left, right) = island.follow()
        while (left.hasNext()) {
            val l = left.next()
            val r = right.next()

            path.add(l)
            path.add(r)
            if (l == r) break
        }
    }

    val area = MutableList(island.rows) { r ->
        MutableList(island.columns) { c ->
            InnerOuter(Location(r, c))
        }
    }

    val queue = ArrayDeque<InnerOuter>()
    for (r in area.indices) {
        for (c in area[r].indices) {
            val innerOuter = area[r][c]

            if (r == 0) innerOuter.apply {
                topLeft = false
                topRight = false
                queue.add(this)
            }

            if (r == island.rows - 1) innerOuter.apply {
                bottomLeft = false
                bottomRight = false
                queue.add(this)
            }

            if (c == 0) innerOuter.apply {
                topLeft = false
                bottomLeft = false
                queue.add(this)
            }

            if (c == island.columns - 1) innerOuter.apply {
                topRight = false
                bottomRight = false
                queue.add(this)
            }
        }
    }

    while (queue.isNotEmpty()) {
        val next = queue.removeFirst()
        val pipe = island[next.location].takeIf { next.location in path }
        next.propagate(pipe)

        val top = next.location + Location(-1, 0)
        if (top in island) next.apply {
            val neighbor = area[top.r][top.c]
            if (!topLeft && neighbor.bottomLeft) {
                neighbor.bottomLeft = false
                queue.addLast(neighbor)
            }
            if (!topRight && neighbor.bottomRight) {
                neighbor.bottomRight = false
                queue.addLast(neighbor)
            }
        }

        val bottom = next.location + Location(1, 0)
        if (bottom in island) next.apply {
            val neighbor = area[bottom.r][bottom.c]
            if (!bottomLeft && neighbor.topLeft) {
                neighbor.topLeft = false
                queue.addLast(neighbor)
            }
            if (!bottomRight && neighbor.topRight) {
                neighbor.topRight = false
                queue.addLast(neighbor)
            }
        }

        val left = next.location + Location(0, -1)
        if (left in island) next.apply {
            val neighbor = area[left.r][left.c]
            if (!topLeft && neighbor.topRight) {
                neighbor.topRight = false
                queue.addLast(neighbor)
            }
            if (!bottomLeft && neighbor.bottomRight) {
                neighbor.bottomRight = false
                queue.addLast(neighbor)
            }
        }

        val right = next.location + Location(0, 1)
        if (right in island) next.apply {
            val neighbor = area[right.r][right.c]
            if (!topRight && neighbor.topLeft) {
                neighbor.topLeft = false
                queue.addLast(neighbor)
            }
            if (!bottomRight && neighbor.bottomLeft) {
                neighbor.bottomLeft = false
                queue.addLast(neighbor)
            }
        }
    }

    return area.flatten().count { it.inner }.toString()
}

data class Location(val r: Int, val c: Int) {
    operator fun plus(delta: Location) = Location(r + delta.r, c + delta.c)

    companion object {
        val NEIGHBORS = listOf(Location(-1, 0), Location(0, 1), Location(1, 0), Location(0, -1))
    }
}

class Island(
    private val grid: List<List<Pipe?>>,
    val starting: Location,
) {
    val rows = grid.size
    val columns = grid[0].size
    operator fun contains(location: Location): Boolean {
        return location.r in 0..<rows && location.c in 0..<columns
    }

    operator fun get(location: Location): Pipe? {
        require(location in this)
        return grid[location.r][location.c]
    }

    fun follow(): Pair<Iterator<Location>, Iterator<Location>> {
        val (left, right) = neighbors(starting) ?: error("!")
        return follow(starting, left) to follow(starting, right)
    }

    private fun neighbors(location: Location): Pair<Location, Location>? {
        val pipe = this[location] ?: return null
        return location + pipe
    }

    private fun follow(previous: Location, next: Location) = iterator {
        var previous = previous
        var next = next
        yield(next)

        while (true) {
            val neighbors = neighbors(next)
            when {
                neighbors == null -> error("!")

                neighbors.first == previous -> {
                    previous = next
                    next = neighbors.second
                }

                neighbors.second == previous -> {
                    previous = next
                    next = neighbors.first
                }
            }

            yield(next)
        }
    }

    companion object {
        fun parse(input: List<String>): Island {
            var starting: Location? = null
            rows@ for ((r, row) in input.withIndex()) {
                for ((c, value) in row.withIndex()) {
                    if (value == 'S') {
                        starting = Location(r, c)
                        break@rows
                    }
                }
            }
            require(starting != null)

            val grid = input.map { row -> row.map { Pipe.fromDisplay(it) } }
            require(grid.mapTo(mutableSetOf()) { it.size }.size == 1)

            val tempIsland = Island(grid, starting)
            val startingNeighbors = Location.NEIGHBORS
                .filter { (starting + it) in tempIsland }
                .filter { tempIsland.neighbors((starting + it))?.toList()?.any { it == starting } == true }
                .toSet()

            val startingPipe = Pipe.entries.find { it.deltas.toList().toSet() == startingNeighbors } ?: error("$startingNeighbors")
            return Island(
                grid = grid.mapIndexed { r, row ->
                    row.mapIndexed { c, it ->
                        if (r == starting.r && c == starting.c) startingPipe else it
                    }
                },
                starting = starting,
            )
        }
    }
}

enum class Pipe {
    NorthSouth {
        override val display = '|'
        override val deltas = Pair(Location(-1, 0), Location(1, 0))
    },
    EastWest {
        override val display = '-'
        override val deltas = Pair(Location(0, -1), Location(0, 1))
    },
    NorthEast {
        override val display = 'L'
        override val deltas = Pair(Location(-1, 0), Location(0, 1))
    },
    NorthWest {
        override val display = 'J'
        override val deltas = Pair(Location(-1, 0), Location(0, -1))
    },
    SouthEast {
        override val display = 'F'
        override val deltas = Pair(Location(1, 0), Location(0, 1))
    },
    SouthWest {
        override val display = '7'
        override val deltas = Pair(Location(1, 0), Location(0, -1))
    },
    ;

    abstract val display: Char
    abstract val deltas: Pair<Location, Location>

    companion object {
        private val displayMap = entries.associateBy { it.display }
        fun fromDisplay(display: Char) = displayMap[display]
    }
}

operator fun Location.plus(pipe: Pipe): Pair<Location, Location> =
    this + pipe.deltas.first to this + pipe.deltas.second

data class InnerOuter(
    val location: Location,
    var topLeft: Boolean = true,
    var topRight: Boolean = true,
    var bottomLeft: Boolean = true,
    var bottomRight: Boolean = true,
) {
    val inner get() = topLeft && topRight && bottomLeft && bottomRight
}

private fun InnerOuter.propagate(pipe: Pipe?): InnerOuter {
    when (pipe) {
        Pipe.NorthSouth -> {
            if (!topLeft || !bottomLeft) {
                topLeft = false
                bottomLeft = false
            }
            if (!topRight || !bottomRight) {
                topRight = false
                bottomRight = false
            }
        }

        Pipe.EastWest -> {
            if (!topLeft || !topRight) {
                topLeft = false
                topRight = false
            }
            if (!bottomLeft || !bottomRight) {
                bottomLeft = false
                bottomRight = false
            }
        }

        Pipe.NorthEast -> {
            if (!topLeft || !bottomLeft || !bottomRight) {
                topLeft = false
                bottomLeft = false
                bottomRight = false
            }
        }

        Pipe.NorthWest -> {
            if (!topRight || !bottomLeft || !bottomRight) {
                topRight = false
                bottomLeft = false
                bottomRight = false
            }
        }

        Pipe.SouthEast -> {
            if (!topLeft || !topRight || !bottomLeft) {
                topLeft = false
                topRight = false
                bottomLeft = false
            }
        }

        Pipe.SouthWest -> {
            if (!topLeft || !topRight || !bottomRight) {
                topLeft = false
                topRight = false
                bottomRight = false
            }
        }

        null -> {
            if (!topLeft || !topRight || !bottomLeft || !bottomRight) {
                topLeft = false
                topRight = false
                bottomLeft = false
                bottomRight = false
            }
        }
    }

    return this
}
