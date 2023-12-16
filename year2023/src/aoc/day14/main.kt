package aoc.day14

import utils.grid2d.*
import utils.*

fun main() {
    val input = readInput("aoc/day14/input.txt")
    val sample1 = readInput("aoc/day14/sample1.txt")
    val sample2 = readInput("aoc/day14/sample2.txt")

    val part1 = part1(sample1)
    require(part1 == "136") { part1 }
    println(part1(input))

    val part2 = part2(sample2)
    require(part2 == "64") { part2 }
    println(part2(input))
}

fun part1(input: List<String>): String {
    val grid = MutableGrid(input.map { it.toList() }.reversed())
    for (x in grid.xSpan) slide(grid.column(x).asReverse()) // Slide North
    return grid.load().toString()
}

fun part2(input: List<String>): String {
    val targetCycles = 1000000000
    val grid = MutableGrid(input.map { it.toList() }.reversed())

    val cycles = mutableListOf<Long>()
    while (true) {
        cycle(grid)
        cycles.add(grid.load())

        if (cycles.size < 100) continue // Make sure we have a sufficient sample size.
        val cycleSize = findCycleSize(cycles) ?: continue
        val startSize = cycles.size % cycleSize
        val cycleCount = (targetCycles - startSize) % cycleSize
        return cycles[startSize + cycleCount - 1].toString()
    }
}

fun Grid<Char>.load(): Long {
    var result = 0L
    for (y in ySpan) {
        for (x in xSpan) {
            if (this[x, y] == 'O') result += y + 1
        }
    }
    return result
}

private fun cycle(grid: MutableGrid<Char>) {
    for (x in grid.xSpan) slide(grid.column(x).asReverse()) // North
    for (y in grid.ySpan) slide(grid.row(y)) // West
    for (x in grid.xSpan) slide(grid.column(x)) // South
    for (y in grid.ySpan) slide(grid.row(y).asReverse()) // East
}

private fun slide(row: MutableGrid.MutableSpan<Char>) {
    fun swap(a: Int, b: Int) {
        val temp = row[a]
        row[a] = row[b]
        row[b] = temp
    }

    var scan = 0
    var set = 0
    while (scan < row.size) {
        when (row[scan]) {
            '#' -> set = ++scan
            'O' -> swap(set++, scan++)
            '.' -> scan++
        }
    }
}
