@file:Suppress("PackageDirectoryMismatch")

package aoc.year2025.day07

import aoc.run
import utils.grid2d.*

const val SAMPLE1 = """
.......S.......
...............
.......^.......
...............
......^.^......
...............
.....^.^.^.....
...............
....^.^...^....
...............
...^.^...^.^...
...............
..^...^.....^..
...............
.^.^.^.^.^...^.
...............
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() = run(
    part1 = ::part1, sample1 = SAMPLE1, expected1 = "21",
    part2 = ::part2, sample2 = SAMPLE2, expected2 = "40",
)

private fun part1(input: String): String {
    val grid = Grid(input, reversed = false)

    var count = 0
    var beams = setOf(grid.points.find { grid[it] == 'S' }!!.x)
    for (y in grid.ySpan) {
        beams = buildSet {
            val row = grid.row(y)
            for (x in beams) {
                if (row[x] == '^') {
                    count++
                    add(x - 1)
                    add(x + 1)
                } else {
                    add(x)
                }
            }
        }
    }

    return count.toString()
}

private fun part2(input: String): String {
    val grid = Grid(input, reversed = false)

    var beams = mapOf(grid.points.find { grid[it] == 'S' }!!.x to 1L)
    for (y in grid.ySpan) {
        beams = buildMap {
            val row = grid.row(y)
            for ((x, timelines) in beams) {
                if (row[x] == '^') {
                    merge(x - 1, timelines) { a, b -> a + b }
                    merge(x + 1, timelines) { a, b -> a + b }
                } else {
                    merge(x, timelines) { a, b -> a + b }
                }
            }
        }
    }

    return beams.values.sum().toString()
}
