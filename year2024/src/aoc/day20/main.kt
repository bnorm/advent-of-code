package aoc.day20

import aoc.input.downloadInput
import utils.grid2d.*
import kotlin.math.abs

const val SAMPLE1 = """
###############
#...#...#.....#
#.#.#.#.#.###.#
#S#...#.#.#...#
#######.#.#.###
#######.#.#...#
#######.#.###.#
###..E#...#...#
###.#######.###
#...###...#...#
#.#####.#.###.#
#.#...#.#.#...#
#.#.#.#.#.#.###
#...#...#...###
###############
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 20)

    val part1 = part1(SAMPLE1, 12)
    require(part1 == "8") { part1 }
    println(part1(input, 100))

    val part2 = part2(SAMPLE2, 72)
    require(part2 == "29") { part2 }
    println(part2(input, 100))
}

private fun part1(input: String, goal: Int): String {
    val grid = Grid(input)
    val start = grid.points.first { grid[it] == 'S' }
    val end = grid.points.first { grid[it] == 'E' }

    val path = findPath(grid, start, end)
    val cheats = path.keys
        .flatMap { findCheats(path, it, maxDistance = 2) }
        .groupBy(keySelector = { it.first }, valueTransform = { it.second })

    return cheats.entries
        .filter { it.key >= goal }
        .sumOf { it.value.size }
        .toString()
}

private fun part2(input: String, goal: Int): String {
    val grid = Grid(input)
    val start = grid.points.first { grid[it] == 'S' }
    val end = grid.points.first { grid[it] == 'E' }

    val path = findPath(grid, start, end)
    val cheats = path.keys
        .flatMap { findCheats(path, it, maxDistance = 20) }
        .groupBy(keySelector = { it.first }, valueTransform = { it.second })

    return cheats.entries
        .filter { it.key >= goal }
        .sumOf { it.value.size }
        .toString()
}

private fun findCheats(
    path: Map<Point, Int>,
    start: Point,
    maxDistance: Int,
): Sequence<Pair<Int, Pair<Point, Point>>> = sequence {
    val startValue = path[start] ?: return@sequence

    for (dx in -maxDistance..maxDistance) {
        for (dy in -maxDistance..maxDistance) {
            // Manhattan distance of 2..maxDistance.
            val distance = abs(dx) + abs(dy)
            if (distance !in 2..maxDistance) continue
            val end = start.move(dx, dy)

            val endValue = path[end] ?: continue

            val savings = startValue - endValue - distance
            if (savings > 0) {
                yield(savings to (start to end))
            }
        }
    }
}

private fun findPath(grid: Grid<Char>, start: Point, end: Point): MutableMap<Point, Int> {
    val path = mutableMapOf<Point, Int>()

    var current = end
    var distance = 0
    while (current != start) {
        path[current] = distance++
        current = grid.adjacent(current).single { it !in path && grid[it] != '#' }
    }
    path[start] = distance

    return path
}
