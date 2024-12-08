package aoc.day08

import aoc.input.downloadInput
import utils.grid2d.*
import utils.toPairs

const val SAMPLE1 = """
............
........0...
.....0......
.......0....
....0.......
......A.....
............
............
........A...
.........A..
............
............
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 8)

    val part1 = part1(SAMPLE1)
    require(part1 == "14") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "34") { part2 }
    println(part2(input))
}

private fun part1(input: String): String {
    val grid = Grid(input.trim().lines().asReversed().map { it.toList() })

    val antennae = grid.points
        .filter { p -> grid[p] != '.' }
        .groupBy(keySelector = { grid[it] }, valueTransform = { it })

    val antinodes = mutableSetOf<Point>()
    for ((_, points) in antennae) {
        for ((p1, p2) in points.toPairs()) {
            val a1 = p1 + (p1 - p2)
            if (a1 in grid) antinodes.add(a1)

            val a2 = p2 + (p2 - p1)
            if (a2 in grid) antinodes.add(a2)
        }
    }

    return antinodes.size.toString()
}

private fun part2(input: String): String {
    val grid = Grid(input.trim().lines().asReversed().map { it.toList() })

    val antennae = grid.points
        .filter { p -> grid[p] != '.' }
        .groupBy(keySelector = { grid[it] }, valueTransform = { it })

    val antinodes = mutableSetOf<Point>()
    for ((_, points) in antennae) {
        for ((p1, p2) in points.toPairs()) {
            val line = Line(p1, p2)
            if (line.isVertical) {
                for (y in grid.ySpan) {
                    antinodes.add(Point(p1.x, y))
                }
            } else if (line.isHorizontal) {
                for (x in grid.xSpan) {
                    antinodes.add(Point(x, p1.y))
                }
            } else {
                // TODO optimize to iterate out from x1 in both directions,
                //  and break when the first out-of-grid point is found
                for (x in grid.xSpan) {
                    val point = line.invoke(x) ?: continue
                    if (point in grid) antinodes.add(point)
                }
            }
        }
    }

    return antinodes.size.toString()
}
