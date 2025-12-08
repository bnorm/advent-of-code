@file:Suppress("PackageDirectoryMismatch")

package aoc.day07

import aoc.input.downloadInput
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

suspend fun main() {
    val input = downloadInput(2025, 7)

    val part1 = part1(SAMPLE1)
    require(part1 == "21") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "40") { part2 }
    println(part2(input))
}

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
