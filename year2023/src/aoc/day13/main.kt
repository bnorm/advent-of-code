package aoc.day13

import utils.grid2d.Grid
import utils.*

fun main() {
    val input = readInput("aoc/day13/input.txt")
    val sample1 = readInput("aoc/day13/sample1.txt")
    val sample2 = readInput("aoc/day13/sample2.txt")

    val part1 = part1(sample1)
    require(part1 == "405") { part1 }
    println(part1(input))

    val part2 = part2(sample2)
    require(part2 == "400") { part2 }
    println(part2(input))
}

fun part1(input: List<String>): String {
    val grids = input.separateBy { it.isEmpty() }
        .map { grid -> Grid(grid.map { it.toList() }) }
    return grids.sumOf { findReflection(it) }.toString()
}

fun part2(input: List<String>): String {
    val grids = input.separateBy { it.isEmpty() }
        .map { grid -> Grid(grid.map { it.toList() }) }
    return grids.sumOf { findDirtyReflection(it) }.toString()
}

fun findReflection(grid: Grid<Char>): Int {
    fun find(spans: List<Grid.Span<Char>>): Int? {
        rows@ for (index in 1..<spans.size) {
            var j = index - 1
            var k = index
            while (j >= 0 && k < spans.size) {
                if (spans[j] != spans[k]) continue@rows
                k++
                j--
            }
            return index
        }
        return null
    }

    return find(grid.ySpan.map { grid.row(it) })?.let { 100 * it }
        ?: find(grid.xSpan.map { grid.column(it) })
        ?: error("!")
}

fun findDirtyReflection(grid: Grid<Char>): Int {
    fun diff(first: Grid.Span<Char>, second: Grid.Span<Char>): Int =
        first.zip(second) { a, b -> if (a == b) 0 else 1 }.sum()

    fun find(spans: List<Grid.Span<Char>>): Int? {
        rows@ for (index in 1..<spans.size) {
            var j = index - 1
            var k = index
            var difference = 0
            while (j >= 0 && k < spans.size) {
                difference += diff(spans[j], spans[k])
                if (difference > 1) continue@rows
                k++
                j--
            }
            if (difference == 1) return index
        }
        return null
    }

    return find(grid.ySpan.map { grid.row(it) })?.let { 100 * it }
        ?: find(grid.xSpan.map { grid.column(it) })
        ?: error("!")
}
