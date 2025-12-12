@file:Suppress("PackageDirectoryMismatch")

package aoc.year2025.day04

import aoc.run
import utils.grid2d.*

const val SAMPLE1 = """
..@@.@@@@.
@@@.@.@.@@
@@@@@.@.@@
@.@@@@..@.
@@.@@@@.@@
.@@@@@@@.@
.@.@.@.@@@
@.@@@.@@@@
.@@@@@@@@.
@.@.@@@.@.
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() = run(
    part1 = ::part1, sample1 = SAMPLE1, expected1 = "13",
    part2 = ::part2, sample2 = SAMPLE2, expected2 = "43",
)

private fun part1(input: String): String {
    val grid = Grid(input)
    return grid.points
        .filter { grid[it] == '@' }
        .count { p -> grid.neighbors(p).count { grid[it] == '@' } < 4 }
        .toString()
}

private fun part2(input: String): String {
    val grid = MutableGrid(input)

    var removed = 0
    while (true) {
        val removable = grid.points
            .filter { grid[it] == '@' }
            .filter { p -> grid.neighbors(p).count { grid[it] == '@' } < 4 }
            .toList()

        if (removable.isEmpty()) break
        removed += removable.size

        for (p in removable) {
            grid[p] = 'x'
        }
    }

    return removed.toString()
}
