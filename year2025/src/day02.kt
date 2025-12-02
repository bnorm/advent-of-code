@file:Suppress("PackageDirectoryMismatch")

package aoc.day02

import aoc.input.downloadInput

const val SAMPLE1 = """
11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2025, 2)

    val part1 = part1(SAMPLE1)
    require(part1 == "1227775554") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "4174379265") { part2 }
    println(part2(input))
}

private fun part1(input: String): String {
    val ranges = input.trim().split(',')
        .map { range -> range.split('-').let { values -> values[0] to values[1] } }

    fun MutableSet<Long>.guess(value: String, max: Long) {
        if (value.length % 2 != 0) return

        var prefix = value.substring(0, value.length / 2).toLong()
        while (true) {
            val guess = prefix.toString().repeat(2).toLong()
            if (guess > max) break
            add(guess)
            prefix += 1
        }
    }

    var sum = 0L
    for ((start, end) in ranges) {
        val range = start.toLong()..end.toLong()

        val guesses = buildSet {
            guess(start, range.last)
            guess(end, range.last)
        }

        for (guess in guesses) {
            if (guess in range) sum += guess
        }
    }

    return sum.toString()
}

private fun part2(input: String): String {
    val ranges = input.trim().split(',')
        .map { range -> range.split('-').let { values -> values[0] to values[1] } }

    fun MutableSet<Long>.guess(value: String, max: Long) {
        for (length in 1..value.length / 2) {
            var prefix = value.substring(0, length).toLong()
            while (true) {
                val guess = prefix.toString().repeat(value.length / length).toLong()
                if (guess > max) break
                add(guess)
                prefix += 1
            }
        }
    }

    var sum = 0L
    for ((start, end) in ranges) {
        val range = start.toLong()..end.toLong()

        val guesses = buildSet {
            guess(start, range.last)
            guess(end, range.last)
        }

        for (guess in guesses) {
            if (guess in range) sum += guess
        }
    }

    return sum.toString()
}
