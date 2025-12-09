@file:Suppress("PackageDirectoryMismatch")

package aoc.day09

import aoc.input.downloadInput
import utils.grid2d.*
import utils.intersects
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

const val SAMPLE1 = """
7,1
11,1
11,7
9,7
9,5
2,5
2,3
7,3
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2025, 9)

    val part1 = part1(SAMPLE1)
    require(part1 == "50") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "24") { part2 }
    println(part2(input))
}

private fun part1(input: String): String {
    val tiles = input.trim().lines().map { line ->
        val (x, y) = line.split(",").map { it.toInt() }
        Point(x, y)
    }

    val rectangles = sequence {
        for (i in 0..<tiles.size) {
            for (j in i + 1..<tiles.size) {
                yield(tiles[i] to tiles[j])
            }
        }
    }

    val max = rectangles.maxOf { (t1, t2) ->
        (abs(t1.x - t2.x) + 1L) * (abs(t1.y - t2.y) + 1L)
    }

    return max.toString()
}

private fun part2(input: String): String {
    val tiles = input.trim().lines().map { line ->
        val (x, y) = line.split(",").map { it.toInt() }
        Point(x, y)
    }

    // Create sorted sets of vertical and horizontal edge lines.
    val lines = sequence {
        for (i in 0..<tiles.size) {
            yield(tiles[i] to tiles[(i + 1) % tiles.size])
        }
    }
    val (verticalUnsorted, horizontalUnsorted) = lines.partition { (t1, t2) -> t1.x == t2.x }
    val vertical = verticalUnsorted
        .map { (t1, t2) -> t1.x to min(t1.y, t2.y)..max(t1.y, t2.y) }
        .sortedBy { (x, _) -> x }

    val horizontal = horizontalUnsorted
        .map { (t1, t2) -> t1.y to min(t1.x, t2.x)..max(t1.x, t2.x) }
        .sortedBy { (y, _) -> y }

    val rectangles = sequence {
        // Create all possible rectangles.
        for (i in 0..<tiles.size) {
            for (j in i + 1..<tiles.size) {
                yield(tiles[i] to tiles[j])
            }
        }
    }.filter { (t1, t2) ->
        // If every edge tile is either green or red, the rectangle will contain all green tiles.
        val rangeX = min(t1.x, t2.x)..max(t1.x, t2.x)
        val rangeY = min(t1.y, t2.y)..max(t1.y, t2.y)

        // Check X start edge.
        val xStart = checkEdgeRayCast(
            crossEdges = horizontal,
            alignedEdges = vertical,
            edgeValue = rangeX.first,
            edgeRange = rangeY
        )
        if (!xStart) return@filter false

        // Check Y start edge.
        val yStart = checkEdgeRayCast(
            crossEdges = vertical,
            alignedEdges = horizontal,
            edgeValue = rangeY.first,
            edgeRange = rangeX
        )
        if (!yStart) return@filter false

        // Check X end edge.
        val xEnd = rangeX.first == rangeX.last || checkEdgeRayCast(
            crossEdges = horizontal,
            alignedEdges = vertical,
            edgeValue = rangeX.last,
            edgeRange = rangeY
        )
        if (!xEnd) return@filter false

        // Check Y end edge.
        val yEnd = rangeY.first == rangeY.last || checkEdgeRayCast(
            crossEdges = vertical,
            alignedEdges = horizontal,
            edgeValue = rangeY.last,
            edgeRange = rangeX
        )
        if (!yEnd) return@filter false

        true
    }

    val max = rectangles.maxOf { (t1, t2) ->
        (abs(t1.x - t2.x) + 1L) * (abs(t1.y - t2.y) + 1L)
    }

    return max.toString()
}

/**
 * Checks that all tiles along a specific edge are either red or green
 * by projecting a ray along a specific `x` or `y` ([edgeValue])
 * and checking each point along that ray.
 * This can efficiently be done by only considering edges that cross this ray ([crossEdges])
 * or are aligned with this ray ([alignedEdges]).
 */
private fun checkEdgeRayCast(
    crossEdges: List<Pair<Int, IntRange>>,
    alignedEdges: List<Pair<Int, IntRange>>,
    edgeValue: Int,
    edgeRange: IntRange
): Boolean {
    // Find all cross-edges that intersect with the edge value.
    val intersecting = crossEdges
        .filter { (_, range) -> edgeValue in range }
        .toMap()

    // Find all aligned edges that are along the edge value.
    val containing = alignedEdges
        .filter { (value, _) -> edgeValue == value }
        .mapTo(mutableSetOf()) { (_, range) -> range.first }

    // Add a last value to simplify loop logic.
    val iter = (intersecting.keys + Int.MAX_VALUE).sorted().iterator()
    var last = 0 // Start the ray from the beginning.
    var outside = true // Start the ray outside.

    while (iter.hasNext()) {
        // Check the range between the last point on the ray
        // and the current point on the ray.
        val curr = iter.next()
        if (outside && last + 1 < curr) {
            // If the ray is outside and intersects any portion of the edge range,
            // Then the edge is not completely red or green.
            // End points do not need to be considered
            // as they are always either green (cross edges) or red (aligned edges).
            val outsideRange = (last + 1)..(curr - 1)
            if (outsideRange intersects edgeRange) return false
        }

        if (curr in containing) {
            // Skip to the next point, since this section doesn't need to be checked.
            var next = iter.next()
            while (next in containing) next = iter.next()

            val start = intersecting.getValue(curr)
            val end = intersecting.getValue(next)
            outside = if (
                edgeValue == start.first && start.first == end.first ||
                edgeValue == start.last && start.last == end.last
            ) {
                // Touches rectangle edge and retreats in the same direction.
                // Therefore, it doesn't change the outside status of the ray.
                outside
            } else {
                // Touches rectangle edge and continues through.
                // This flips the outside status of the ray.
                !outside
            }
            // Either way, don't need to check this section of the edge.
            last = next
        } else {
            // Flip the outside status of the ray.
            outside = !outside
            last = curr
        }
    }

    return true
}
