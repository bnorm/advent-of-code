package aoc.day25

import aoc.input.downloadInput
import utils.grid2d.*

const val SAMPLE1 = """
#####
.####
.####
.####
.#.#.
.#...
.....

#####
##.##
.#.##
...##
...#.
...#.
.....

.....
#....
#....
#...#
#.#.#
#.###
#####

.....
.....
#.#..
###..
###.#
###.#
#####

.....
.....
.....
#....
#.#..
#.#.#
#####
"""

suspend fun main() {
    val input = downloadInput(2024, 25)

    val part1 = part1(SAMPLE1)
    require(part1 == "3") { part1 }
    println(part1(input))
}

private fun part1(input: String): String {
    val locks = mutableListOf<IntArray>()
    val keys = mutableListOf<IntArray>()
    input.trim().split("\n\n").forEach {
        val grid = Grid(it)
        val array = IntArray(grid.xSpan.endInclusive + 1)
        for (x in grid.xSpan) {
            array[x] = grid.column(x).count { it == '#' }
        }

        if (grid[0, 0] == '#') {
            locks.add(array)
        } else {
            keys.add(array)
        }
    }

    var combinations = 0
    for (lock in locks) {
        for (key in keys) {
            var overlap = false
            for (i in lock.indices) {
                if (lock[i] + key[i] > 7) {
                    overlap = true
                    break
                }
            }

            if (!overlap) {
                combinations++
            }
        }
    }

    return combinations.toString()
}
