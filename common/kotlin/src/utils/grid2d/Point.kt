@file:Suppress("unused")

package utils.grid2d

import kotlin.math.abs
import kotlin.math.sqrt

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
    operator fun minus(other: Point): Point = Point(x - other.x, y - other.y)
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

        fun manhattan(first: Point, second: Point): Long {
            val dx = first.x.toLong() - second.x.toLong()
            val dy = first.y.toLong() - second.y.toLong()
            return abs(dx) + abs(dy)
        }

        fun distanceSquared(first: Point, second: Point): Long {
            val dx = first.x.toLong() - second.x.toLong()
            val dy = first.y.toLong() - second.y.toLong()
            return dx * dx + dy * dy
        }

        fun distance(first: Point, second: Point): Double {
            return sqrt(distanceSquared(first, second).toDouble())
        }
    }
}

// TODO deprecate
fun manhattan(first: Point, second: Point): Long {
    return Point.manhattan(first, second)
}

// ===== DIRECTION ===== //

fun Point.move(direction: Direction): Point {
    return when (direction) {
        Direction.East -> this + Point.ADJACENT[0]
        Direction.South -> this + Point.ADJACENT[1]
        Direction.West -> this + Point.ADJACENT[2]
        Direction.North -> this + Point.ADJACENT[3]
    }
}

// ===== GRID ===== //

operator fun <T> Grid<T>.contains(point: Point): Boolean = point.x in xSpan && point.y in ySpan
operator fun <T> Grid<T>.get(point: Point): T = get(point.x, point.y)
operator fun <T> MutableGrid<T>.set(point: Point, value: T) = set(point.x, point.y, value)
fun <T> Grid<T>.ref(point: Point) = ref(point.x, point.y)
fun <T> MutableGrid<T>.ref(point: Point) = ref(point.x, point.y)

val <T> Grid<T>.points: Sequence<Point>
    get() = sequence {
        for (y in ySpan) {
            for (x in xSpan) {
                yield(Point(x, y))
            }
        }
    }

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

    val right = point.x + 1 <= xSpan.last
    val down = point.y - 1 >= 0
    val left = point.x - 1 >= 0
    val up = point.y + 1 <= ySpan.last

    if (right) yield(point.move(xDelta = 1, yDelta = 0))
    if (down) yield(point.move(xDelta = 0, yDelta = -1))
    if (left) yield(point.move(xDelta = -1, yDelta = 0))
    if (up) yield(point.move(xDelta = 0, yDelta = +1))
}

/** Returns all [neighbor][Point.NEIGHBORS] points to the specified point. */
fun <T> Grid<T>.neighbors(point: Point): Sequence<Point> = sequence {
    // Manually yield each neighbor point to avoid extra instance instantiation.

    val right = point.x + 1 <= xSpan.last
    val down = point.y - 1 <= 0
    val left = point.x - 1 >= 0
    val up = point.y + 1 <= ySpan.last

    if (right) yield(point.move(xDelta = 1, yDelta = 0))
    if (down && right) yield(point.move(xDelta = 1, yDelta = -1))
    if (down) yield(point.move(xDelta = 0, yDelta = -1))
    if (down && left) yield(point.move(xDelta = -1, yDelta = -1))
    if (left) yield(point.move(xDelta = -1, yDelta = 0))
    if (up && left) yield(point.move(xDelta = -1, yDelta = 1))
    if (up) yield(point.move(xDelta = 0, yDelta = 1))
    if (up && right) yield(point.move(xDelta = 1, yDelta = 1))
}
