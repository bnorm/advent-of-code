package aoc.day06

import aoc.input.downloadInput
import utils.grid2d.*

const val SAMPLE1 = """
....#.....
.........#
..........
..#.......
.......#..
..........
.#..^.....
........#.
#.........
......#...
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 6)

    val part1 = part1(SAMPLE1)
    require(part1 == "41") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "6") { part2 }
    println(part2(input))
}

private fun part1(input: String): String {
    val grid = Grid(input.trim().split("\n").reversed().map { it.toList() })
    val start = Vector(grid.points.single { grid[it] == '^' }, Direction.North)
    val path = followPath(grid, start)
    return path.mapTo(mutableSetOf()) { it.point }.size.toString()
}

private fun part2(input: String): String {
    val grid = MutableGrid(input.trim().split("\n").reversed().map { it.toList() })
    val start = Vector(grid.points.single { grid[it] == '^' }, Direction.North)

    var count = 0
    for (point in followPath(grid, start).mapTo(mutableSetOf()) { it.point }) {
        if (grid[point] != '.') continue // Ignore start.

        grid[point] = '#' // Add obstacle.
        val possibleLoop = followPath(grid, start)
        if (possibleLoop.last().move() in grid) count++
        grid[point] = '.' // Remove obstacle.
    }

    return count.toString()
}

private fun followPath(grid: Grid<Char>, start: Vector): List<Vector> {
    val path = LinkedHashSet<Vector>()
    var current = start
    while (true) {
        if (!path.add(current)) break // Looped.

        val next = current.move()
        current = when {
            next !in grid -> break // Walked off edge.
            grid[next] == '#' -> current.turnRight()
            else -> next
        }
    }

    return path.toList()
}
