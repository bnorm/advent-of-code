package grid2d

import kotlin.math.abs

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
    fun move(xDelta: Int = 0, yDelta: Int = 0): Point = Point(x + xDelta, y + yDelta)

    override fun toString(): String = "($x, $y)"

    companion object {
        // Directly adjacent points, not including diagonally.
        val ADJACENT = listOf(
            Point(1, 0), // Right
            Point(0, -1), // Down
            Point(-1, 0), // Left
            Point(0, 1), // Up
        )

        // Directly adjacent points, including diagonally.
        val NEIGHBORS = listOf(
            Point(1, 0), // Right
            Point(1, -1), // Down Right
            Point(0, -1), // Down
            Point(-1, -1), // Down Left
            Point(-1, 0), // Left
            Point(-1, 1), // Up Left
            Point(0, 1), // Up
            Point(1, 1), // Up Right
        )
    }
}

fun manhattan(first: Point, second: Point): Long {
    return abs(first.x.toLong() - second.x.toLong()) + abs(first.y.toLong() - second.y.toLong())
}


class Grid<T>(
    private val rows: List<List<T>>,
) {
    init {
        val rowSizes = rows.map { it.size }
        require(rowSizes.isNotEmpty()) { "Must have at least 1 row." }
        require(rowSizes.toSet().size == 1) { "All rows must have the same length." }
    }

    val xSpan = rows[0].indices
    val ySpan = rows.indices

    fun contains(x: Int, y: Int): Boolean = x in xSpan && y in ySpan
    private fun checkBounds(x: Int, y: Int): Unit = require(contains(x, y)) { "($x, $y) !in ($xSpan, $ySpan)" }

    operator fun get(x: Int, y: Int): T {
        checkBounds(x, y)
        return rows[y][x]
    }
}

operator fun <T> Grid<T>.get(point: Point): T = get(point.x, point.y)
operator fun <T> Grid<T>.contains(point: Point): Boolean = point.x in xSpan && point.y in ySpan

fun <T> Grid<T>.findAll(predicate: (T) -> Boolean): Sequence<Point> = sequence {
    val grid = this@findAll
    for (x in xSpan) {
        for (y in ySpan) {
            if (predicate(grid[x, y])) yield(Point(x, y))
        }
    }
}

/** Returns all [adjacent][Point.ADJACENT] points ]to the specified point. */
fun <T> Grid<T>.adjacent(point: Point): Sequence<Point> = sequence {
    // Manually yield each adjacent point to avoid extra instance instantiation.

    val right = point.x + 1 < xSpan.last
    val down = point.y - 1 > 0
    val left = point.x - 1 > 0
    val up = point.y + 1 < ySpan.last

    if (right) yield(point.move(xDelta = 1, yDelta = 0))
    if (down) yield(point.move(xDelta = 0, yDelta = -1))
    if (left) yield(point.move(xDelta = -1, yDelta = 0))
    if (up) yield(point.move(xDelta = 0, yDelta = +1))
}

/** Returns all [neighbor][Point.NEIGHBORS] points to the specified point. */
fun <T> Grid<T>.neighbors(point: Point): Sequence<Point> = sequence {
    // Manually yield each neighbor point to avoid extra instance instantiation.

    val right = point.x + 1 < xSpan.last
    val down = point.y - 1 > 0
    val left = point.x - 1 > 0
    val up = point.y + 1 < ySpan.last

    if (right) yield(point.move(xDelta = 1, yDelta = 0))
    if (down && right) yield(point.move(xDelta = 1, yDelta = -1))
    if (down) yield(point.move(xDelta = 0, yDelta = -1))
    if (down && left) yield(point.move(xDelta = -1, yDelta = -1))
    if (left) yield(point.move(xDelta = -1, yDelta = 0))
    if (up && left) yield(point.move(xDelta = -1, yDelta = 1))
    if (up) yield(point.move(xDelta = 0, yDelta = 1))
    if (up && right) yield(point.move(xDelta = 1, yDelta = 1))
}
