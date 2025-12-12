@file:Suppress("PackageDirectoryMismatch")

package aoc.year2025.day05

import aoc.run

const val SAMPLE1 = """
3-5
10-14
16-20
12-18

1
5
8
11
17
32
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() = run(
    part1 = ::part1, sample1 = SAMPLE1, expected1 = "3",
    part2 = ::part2, sample2 = SAMPLE2, expected2 = "14",
)

private fun part1(input: String): String {
    val (rangesInput, ingredientInput) = input.trim().split("\n\n")
    val ranges = rangesInput.trim().lines().map { line ->
        val (startStr, endStr) = line.split('-')
        startStr.toLong()..endStr.toLong()
    }
    val ingredients = ingredientInput.trim().lines().map { it.toLong() }

    return ingredients.count { id -> ranges.any { id in it } }.toString()
}

private fun part2(input: String): String {
    val (rangesInput, _) = input.trim().split("\n\n")
    val ranges = rangesInput.trim().lines().map { line ->
        val (startStr, endStr) = line.split('-')
        startStr.toLong()..endStr.toLong()
    }
        // Sort ranges by start ID
        .sortedBy { it.first }

    val merged = mutableListOf<LongRange>()
    var current = ranges[0]
    for (range in ranges) {
        if (range.first in current) {
            current = current.first..maxOf(current.last, range.last)
        } else {
            merged += current
            current = range
        }
    }
    merged += current // Remember the last range!

    return merged.sumOf { it.last - it.first + 1 }.toString()
}
