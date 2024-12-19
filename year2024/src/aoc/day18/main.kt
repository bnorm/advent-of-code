package aoc.day18

import aoc.input.downloadInput
import utils.grid2d.*
import java.util.*

const val SAMPLE1 = """
5,4
4,2
4,5
3,0
2,1
6,3
2,4
1,5
0,6
3,3
2,6
5,1
1,2
5,5
2,5
6,5
1,4
0,4
6,4
1,1
6,1
1,0
0,5
1,6
2,0
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 18)

    val part1 = part1(SAMPLE1, 7, 12)
    require(part1 == "22") { part1 }
    println(part1(input, 71, 1024))

    val part2 = part2(SAMPLE2, 7)
    require(part2 == "6,1") { part2 }
    println(part2(input, 71))
}

private fun part1(input: String, size: Int, time: Int): String {
    val corruption = input.trim().lines().map { it.split(',') }.map { (x, y) -> Point(x.toInt(), y.toInt()) }
    val grid = MutableGrid<Char>(List(size) { List(size) { '.' } })
    val start = Point(0, 0)
    val exit = Point(size - 1, size - 1)

    for (i in 0..<time) {
        grid[corruption[i]] = '#'
    }

    return (search(grid, start, exit)!!.size - 1).toString()
}

private fun part2(input: String, size: Int): String {
    val corruption = input.trim().lines().map { it.split(',') }.map { (x, y) -> Point(x.toInt(), y.toInt()) }
    val grid = MutableGrid<Char>(List(size) { List(size) { '.' } })
    val start = Point(0, 0)
    val exit = Point(size - 1, size - 1)

    var time = 0
    while (true) {
        val path = search(grid, start, exit)?.toSet() ?: break
        time = corruption.indexOfFirst { it in path }
        for (i in 0..time) {
            grid[corruption[i]] = '#'
        }
    }

    return corruption[time].let { (x, y) -> "$x,$y" }
}

fun search(grid: Grid<Char>, start: Point, exit: Point): List<Point>? {
    data class SearchNode(
        val location: Point,
        val path: List<Point>,
    ) : Comparable<SearchNode> {
        val distance = Point.manhattan(location, exit)
        override fun compareTo(other: SearchNode): Int =
            compareValues(path.size + distance, other.path.size + distance)
    }

    fun next(from: SearchNode): List<SearchNode> =
        grid.adjacent(from.location)
            .filter { grid[it] != '#' }
            .map { SearchNode(it, from.path + it) }
            .toList()

    val visited = mutableMapOf<Point, SearchNode>()
    val queue = PriorityQueue<SearchNode>()
    queue.add(SearchNode(start, listOf(start)))
    while (queue.isNotEmpty()) {
        val current = queue.poll()
        if (current.location == exit) return current.path

        for (next in next(current)) {
            val existing = visited[next.location]
            if (existing != null) {
                if (next >= existing) continue
                queue.remove(existing)
            }

            visited[next.location] = next
            queue.add(next)
        }
    }

    return null
}
