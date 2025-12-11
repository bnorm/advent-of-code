@file:Suppress("PackageDirectoryMismatch")

package aoc.day00

import aoc.input.downloadInput
import kotlin.time.measureTimedValue

const val SAMPLE1 = """
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2025, 0)

    val part1 = part1(SAMPLE1)
    require(part1 == "") { part1 }
    println(measureTimedValue { part1(input) })

    val part2 = part2(SAMPLE2)
    require(part2 == "") { part2 }
    println(measureTimedValue { part2(input) })
}

private fun part1(input: String): String {
    TODO()
}

private fun part2(input: String): String {
    TODO()
}
