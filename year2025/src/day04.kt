@file:Suppress("PackageDirectoryMismatch")

package aoc.day04

import aoc.input.downloadInput
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

suspend fun main() {
    val input = downloadInput(2025, 4)

    val part1 = part1(SAMPLE1)
    require(part1 == "13") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "43") { part2 }
    println(part2(input))
}

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
