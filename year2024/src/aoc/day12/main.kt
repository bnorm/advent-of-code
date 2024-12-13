package aoc.day12

import aoc.input.downloadInput
import utils.grid2d.*

const val SAMPLE1 = """
RRRRIICCFF
RRRRIICCCF
VVRRRCCFFF
VVRCCCJFFF
VVVVCJJCFE
VVIVCCJJEE
VVIIICJJEE
MIIIIIJJEE
MIIISIJEEE
MMMISSJEEE
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 12)

    val part1 = part1(SAMPLE1)
    require(part1 == "1930") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "1206") { part2 }
    println(part2(input))
}

private fun part1(input: String): String {
    val grid = Grid(input)

    var price = 0L
    val remaining = ArrayDeque<Point>()
    val visited = mutableSetOf<Point>()

    remaining.addAll(grid.points)
    while (remaining.isNotEmpty()) {
        val point = remaining.removeFirst()
        if (!visited.add(point)) continue

        val region = findRegion(grid, point)
        visited.addAll(region)

        val perimeter = region.sumOf { point ->
            Point.ADJACENT.map { point + it }.count { it !in region }
        }
        price += region.size * perimeter
    }

    return price.toString()
}

private fun part2(input: String): String {
    val grid = Grid(input)

    var price = 0L
    val remaining = ArrayDeque<Point>()
    val visited = mutableSetOf<Point>()

    remaining.addAll(grid.points)
    while (remaining.isNotEmpty()) {
        val point = remaining.removeFirst()
        if (!visited.add(point)) continue

        val region = findRegion(grid, point)
        visited.addAll(region)

        val sides = countSides(region)
        price += region.size * sides
    }

    return price.toString()
}

private fun <T> findRegion(grid: Grid<T>, start: Point): Set<Point> {
    val region = mutableSetOf<Point>()
    val value = grid[start]

    val queue = ArrayDeque<Point>()
    queue.add(start)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (!region.add(current)) continue

        for (adjacent in grid.adjacent(current)) {
            if (adjacent in grid && grid[adjacent] == value) {
                queue.addLast(adjacent)
            }
        }
    }

    return region
}

private fun countSides(region: Set<Point>): Long {
    when (region.size) {
        // Short-cut for regions with very small count.
        1, 2 -> return 4
    }

    val edges = mutableMapOf<Direction, MutableSet<Point>>()
    for (point in region) {
        for ((direction, adjacent) in Direction.entries.map { it to point.move(it) }) {
            if (adjacent !in region) {
                edges.getOrPut(direction) { mutableSetOf() }.add(point)
            }
        }
    }

    var sides = 0L
    for ((direction, edges) in edges) {
        val remaining = ArrayDeque<Point>()
        remaining.addAll(edges)
        val visited = mutableSetOf<Point>()
        while (remaining.isNotEmpty()) {
            val point = remaining.removeFirst()
            if (!visited.add(point)) continue

            visited.addAll(findSide(edges, point, direction))
            sides++
        }
    }

    return sides
}

private fun findSide(edges: Set<Point>, start: Point, direction: Direction): Set<Point> {
    val side = mutableSetOf<Point>()
    side.add(start)

    when (direction) {
        Direction.East,
        Direction.West,
            -> {
            var current = start
            while (true) {
                val next = current.move(Direction.North)
                if (next !in edges) break
                side.add(next)
                current = next
            }

            current = start
            while (true) {
                val next = current.move(Direction.South)
                if (next !in edges) break
                side.add(next)
                current = next
            }
        }

        Direction.North,
        Direction.South,
            -> {
            var current = start
            while (true) {
                val next = current.move(Direction.East)
                if (next !in edges) break
                side.add(next)
                current = next
            }

            current = start
            while (true) {
                val next = current.move(Direction.West)
                if (next !in edges) break
                side.add(next)
                current = next
            }
        }
    }

    return side
}
