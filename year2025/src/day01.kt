@file:Suppress("PackageDirectoryMismatch")

package aoc.day01

import aoc.input.downloadInput

const val SAMPLE1 = """
L68
L30
R48
L5
R60
L55
L1
L99
R14
L82
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2025, 1)

    val part1 = part1(SAMPLE1)
    require(part1 == "3") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "6") { part2 }
    println(part2(input))
}

private fun part1(input: String): String {
    val directions = input.trim().lines()
        .map { line -> line[0] to line.substring(1).toInt() }

    var dial = 50L
    var count = 0
    for ((direction, rotation) in directions) {
        dial += when (direction) {
            'R' -> rotation
            'L' -> -rotation
            else -> error("!")
        }
        if (dial % 100L == 0L) count++
    }

    return count.toString()
}

private fun part2(input: String): String {
    val directions = input.trim().lines()
        .map { line -> line[0] to line.substring(1).toInt() }

    var dial = 50L
    var count = 0
    for ((direction, rotation) in directions) {
        val click = when (direction) {
            'R' -> 1
            'L' -> -1
            else -> error("!")
        }
        repeat(rotation) {
            dial += click
            if (dial % 100L == 0L) count++
        }
    }

    return count.toString()
}
