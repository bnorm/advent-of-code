package aoc.day18

import utils.*
import utils.grid2d.*
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.abs

fun main() {
    val input = readInput("aoc/day18/input.txt")
    val sample1 = readInput("aoc/day18/sample1.txt")
    val sample2 = readInput("aoc/day18/sample2.txt")

    val part1 = part1(sample1)
    require(part1 == "62") { part1 }
    println(part1(input))

    val part2 = part2(sample2)
    //                      952407877433
    require(part2 == "952408144115") { part2 }
    println(part2(input))
}

fun part1(input: List<String>): String {
    val paths = input.map {
        val (direction, distance, _) =
            "(?<direction>[RDLU]) (?<distance>\\d+) \\(#(?<color>.+)\\)".toRegex()
                .matchEntire(it)!!.destructured
        Path(direction.toDirection(), distance.toInt())
    }

    val sparse = buildGrid(paths)
    sparse.digTrench(paths)
    sparse.grid.digInterior()
    return sparse.calculateArea().toString()
}

fun part2(input: List<String>): String {
    val paths = input.map {
        val (_, _, color) =
            "(?<direction>[RDLU]) (?<distance>\\d+) \\(#(?<color>.+)\\)".toRegex()
                .matchEntire(it)!!.destructured
        val distance = color.substring(0..<5).toInt(16)
        val direction = when (color[5].digitToInt()) {
            0 -> Direction.East
            1 -> Direction.South
            2 -> Direction.West
            3 -> Direction.North
            else -> error("!")
        }
        Path(direction, distance)
    }

    val sparse = buildGrid(paths)
    sparse.digTrench(paths)
    sparse.grid.digInterior()
    return sparse.calculateArea().toString()
}

data class Path(
    val direction: Direction,
    val distance: Int,
)

private fun String.toDirection(): Direction = when (this) {
    "R" -> Direction.East
    "D" -> Direction.South
    "L" -> Direction.West
    "U" -> Direction.North
    else -> error("!")
}

class SparseGrid(
    // Sorted list of unique coordinates along both axis.
    val coordinates: List<Long>,
    val grid: MutableGrid<Char>,
) {
    val start: Point = coordinates.binarySearch(0L).let { Point(it, it) }
}

private fun buildGrid(paths: List<Path>): SparseGrid {
    val coordinates = TreeSet<Long>()
    coordinates.add(0L)

    var x = 0L
    var y = 0L
    for (path in paths) {
        when (path.direction) {
            Direction.North -> y += path.distance
            Direction.East -> x += path.distance
            Direction.South -> y -= path.distance
            Direction.West -> x -= path.distance
        }
        coordinates.add(x)
        coordinates.add(y)
    }

    for ((a, b) in coordinates.zipWithNext()) {
        coordinates.add((a + b) / 2)
    }

    return SparseGrid(
        coordinates = coordinates.toList(),
        grid = MutableGrid<Char>(List(coordinates.size) { List(coordinates.size) { '.' } })
    )
}

private fun MutableGrid<Char>.digInterior() {
    val queue = ArrayDeque<Point>()
    for (y in ySpan) {
        queue.add(Point(0, y))
        queue.add(Point(xSpan.last, y))
    }
    for (x in xSpan) {
        queue.add(Point(x, 0))
        queue.add(Point(x, ySpan.last))
    }

    while (queue.isNotEmpty()) {
        val point = queue.removeFirst()
        when (this[point]) {
            '.' -> {
                this[point] = 'E'
                queue.addAll(Point.NEIGHBORS.map { point + it }.filter { it in this })
            }
        }
    }

    for (y in ySpan) {
        for (x in xSpan) {
            when (this[x, y]) {
                '.' -> this[x, y] = '#'
                'E' -> this[x, y] = '.'
            }
        }
    }
}

private fun SparseGrid.digTrench(paths: List<Path>) {
    var (x, y) = start
    for (path in paths) {
        when (path.direction) {
            Direction.North -> {
                val finalY = coordinates.binarySearch(coordinates[y] + path.distance)
                for (i in y..finalY) grid[x, i] = '#'
                y = finalY
            }

            Direction.East -> {
                val finalX = coordinates.binarySearch(coordinates[x] + path.distance)
                for (i in x..finalX) grid[i, y] = '#'
                x = finalX
            }

            Direction.South -> {
                val finalY = coordinates.binarySearch(coordinates[y] - path.distance)
                for (i in y downTo finalY) grid[x, i] = '#'
                y = finalY
            }

            Direction.West -> {
                val finalX = coordinates.binarySearch(coordinates[x] - path.distance)
                for (i in x downTo finalX) grid[i, y] = '#'
                x = finalX
            }
        }
    }
}

private fun SparseGrid.calculateArea(): Long {
    var result = 0L
    for (y in 0..grid.ySpan.last) {
        for (x in 0..grid.xSpan.last) {
            if (grid[x, y] == '#') {
                result += 1

                val xProjection = if (x + 1 in grid.xSpan && grid[x + 1, y] == '#')
                    abs(coordinates[x + 1] - coordinates[x] - 1) else 0
                result += xProjection

                val yProjection = if (y + 1 in grid.ySpan && grid[x, y + 1] == '#')
                    abs(coordinates[y + 1] - coordinates[y] - 1) else 0
                result += yProjection

                if (xProjection != 0L && yProjection != 0L && grid[x + 1, y + 1] == '#') {
                    result += xProjection * yProjection
                }
            }
        }
    }
    return result
}
