@file:Suppress("PackageDirectoryMismatch")

package aoc.year2025.day12

import aoc.run
import utils.grid2d.*

const val SAMPLE1 = """
0:
###
##.
##.

1:
###
##.
.##

2:
.##
###
##.

3:
##.
###
##.

4:
###
#..
###

5:
###
.#.
###

4x4: 0 0 0 0 2 0
12x5: 1 0 1 0 2 2
12x5: 1 0 1 0 3 2
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() = run(
    part1 = ::part1, sample1 = SAMPLE1, expected1 = "2",
    part2 = ::part2, sample2 = SAMPLE2, expected2 = "",
)

private fun part1(input: String): String {
    val sections = input.trim().split("\n\n")

    val blocks = sections.dropLast(1).map { block ->
        Grid(block.trim().lines().drop(1).map { it.toList() })
    }

    val regions = sections.last().lines().map { line ->
        val (dimensions, counts) = line.split(": ")
        val (x, y) = dimensions.split("x")
        Point(x.toInt(), y.toInt()) to counts.split(" ").map { it.toInt() }
    }

    var count = 0
    for ((size, counts) in regions) {
        if (solvePart1(size, counts, blocks)) count++
    }

    return count.toString()
}

private fun part2(input: String): String = input.substring(IntRange.EMPTY)

private fun solvePart1(size: Point, counts: List<Int>, blocks: List<Grid<Char>>): Boolean {
    // Run some precondition checks before running the search.
    run {
        val blockSpaces = blocks.map { block -> block.ySpan.flatMap { block.row(it) }.count { it == '#' } }

        val available = size.x * size.y
        val min = counts.mapIndexed { i, c -> blockSpaces[i] * c }.sum()

        // Check that there is enough space to support the requested blocks.
        if (available < min) return false

        // Check if there is enough space to not worry about actual placement.
        if ((size.x / 3) * (size.y / 3) >= counts.sumOf { it }) return true
    }

    // Build all unique transformations of each block.
    val transformations = blocks.map { block ->
        buildSet {
            for (r in 0..3) {
                for (f in 0..1) {
                    // y reverse with rotation r is the same as x reverse with rotation r + 2
                    add(block.reverse(xReverse = f == 1).rotate(r).toBlock())
                }
            }
        }
    }

    val space = LongArray(size.y)
    fun rowLength(row: Int) = 64 - space[row].countTrailingZeroBits()

    fun overlaps(row: Int, col: Int, block: Block): Boolean {
        if ((space[row] and (block.top shl col)) != 0L) return true
        if ((space[row + 1] and (block.middle shl col)) != 0L) return true
        if ((space[row + 2] and (block.bottom shl col)) != 0L) return true
        return false
    }

    fun set(row: Int, col: Int, block: Block) {
        space[row] = space[row] or (block.top shl col)
        space[row + 1] = space[row + 1] or (block.middle shl col)
        space[row + 2] = space[row + 2] or (block.bottom shl col)
    }

    fun clear(row: Int, col: Int, block: Block) {
        space[row] = space[row] and (block.top shl col).inv()
        space[row + 1] = space[row + 1] and (block.middle shl col).inv()
        space[row + 2] = space[row + 2] and (block.bottom shl col).inv()
    }

    // DFS of all possible combinations...
    fun recurse(remaining: List<Int>): Boolean {
        if (remaining.isEmpty()) return true

        for (index in remaining.toSet()) {
            val newRemaining = remaining.toMutableList().also { it.remove(index) }

            for (block in transformations[index]) {
                // TODO can we start somewhere other than 0? first non-zero bit?
                for (r in 0..<space.size - 2) {
                    for (c in 0..<minOf(rowLength(r) + 1, size.x - 2)) {
                        if (overlaps(r, c, block)) continue
                        set(r, c, block)
                        if (recurse(newRemaining)) return true
                        clear(r, c, block)
                    }
                    if (space[r] == 0L) break // This was the last good row to check.
                }
            }
        }

        return false
    }

    return recurse(counts.flatMapIndexed { index, count -> List(count) { index } })
}

private data class Block(
    val top: Long,
    val middle: Long,
    val bottom: Long,
)

private fun Grid<Char>.toBlock(): Block {
    require(xSpan.last == 2 && ySpan.last == 2)

    var top = 0L
    if (this[0, 0] == '#') top = top or 1L
    if (this[1, 0] == '#') top = top or 2L
    if (this[2, 0] == '#') top = top or 4L

    var middle = 0L
    if (this[0, 1] == '#') middle = middle or 1L
    if (this[1, 1] == '#') middle = middle or 2L
    if (this[2, 1] == '#') middle = middle or 4L

    var bottom = 0L
    if (this[0, 2] == '#') bottom = bottom or 1L
    if (this[1, 2] == '#') bottom = bottom or 2L
    if (this[2, 2] == '#') bottom = bottom or 4L

    return Block(top, middle, bottom)
}
