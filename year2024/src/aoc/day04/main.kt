package aoc.day04

import aoc.input.downloadInput
import utils.grid2d.*

const val SAMPLE1 = """
MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 4)

    val part1 = part1(SAMPLE1)
    require(part1 == "18") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "9") { part2 }
    println(part2(input))
}

private fun part1(input: String): String {
    val search = "XMAS"
    val grid = Grid(input.trim().lines().map { it.toList() })

    var count = 0
    for (p in grid.points) {
        for (d in Point.NEIGHBORS) {
            if (
                generateSequence(p) { it + d }.zip(search.asSequence())
                    .all { (p, c) -> p in grid && grid[p] == c }
            ) {
                count++
            }
        }
    }

    return count.toString()
}

private fun part2(input: String): String {
    val grid = Grid(input.trim().lines().map { it.toList() })

    var count = 0
    for (p in grid.points) {
        if (grid[p] != 'A') continue

        val p1 = p + Point(1, 1)
        val p2 = p + Point(-1, -1)
        if (p1 !in grid || p2 !in grid) continue
        if ((grid[p1] != 'M' || grid[p2] != 'S') && (grid[p1] != 'S' || grid[p2] != 'M')) continue

        val p3 = p + Point(1, -1)
        val p4 = p + Point(-1, 1)
        if (p3 !in grid || p4 !in grid) continue
        if ((grid[p3] != 'M' || grid[p4] != 'S') && (grid[p3] != 'S' || grid[p4] != 'M')) continue

        count++
    }

    return count.toString()
}
