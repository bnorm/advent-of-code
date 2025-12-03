@file:Suppress("PackageDirectoryMismatch")

package aoc.day03

import aoc.input.downloadInput

const val SAMPLE1 = """
987654321111111
811111111111119
234234234234278
818181911112111
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2025, 3)

    val part1 = part1(SAMPLE1)
    require(part1 == "357") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "3121910778619") { part2 }
    println(part2(input))
}

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
