@file:Suppress("PackageDirectoryMismatch")

package aoc.year2025.day03

import aoc.run

const val SAMPLE1 = """
987654321111111
811111111111119
234234234234278
818181911112111
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() = run(
    part1 = ::part1, sample1 = SAMPLE1, expected1 = "357",
    part2 = ::part2, sample2 = SAMPLE2, expected2 = "3121910778619",
)

private fun part1(input: String): String {
    val banks = input.trim().lines()
        .map { line -> line.map { b -> b.toString().toInt() } }

    var sum = 0L
    for (bank in banks) {
        val first = bank.subList(0, bank.size - 1).maxOrNull()!!
        val second = bank.subList(bank.indexOf(first) + 1, bank.size).maxOrNull()!!
        sum += first * 10 + second
    }

    return sum.toString()
}

private fun part2(input: String): String {
    val banks = input.trim().lines()
        .map { line -> line.map { b -> b.toString().toInt() } }

    var sum = 0L
    for (bank in banks) {
        var joltage = 0L
        var index = 0
        for (n in 11 downTo 0) {
            val sub = bank.subList(index, bank.size - n)
            val value = sub.maxOrNull()!!
            index += sub.indexOf(value) + 1

            joltage *= 10L
            joltage += value
        }
        sum += joltage
    }

    return sum.toString()
}
