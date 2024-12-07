package aoc.day02

import aoc.input.downloadInput

private const val SAMPLE1 = """
7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 2).trim().lines()
    val sample1 = SAMPLE1.trim().lines()
    val sample2 = SAMPLE2.trim().lines()

    require(part1(sample1) == "2")
    println(part1(input))

    require(part2(sample2) == "4")
    println(part2(input))
}

private fun part1(input: List<String>): String {
    var count = 0

    fun isSafe(levels: List<Int>): Boolean {
        if (levels.size < 2 || levels[0] == levels[1]) return false

        val increasing = levels[1] > levels[0]
        return levels.windowed(2).all { (n1, n2) ->
            when (increasing) {
                true -> n2 > n1 && n2 - n1 >= 1 && n2 - n1 <= 3
                false -> n2 < n1 && n1 - n2 >= 1 && n1 - n2 <= 3
            }
        }
    }

    for (line in input) {
        val levels = line.split(" ").map { it.toInt() }
        if (isSafe(levels)) count++
    }

    return count.toString()
}

private fun part2(input: List<String>): String {
    var count = 0

    fun isSafe(levels: List<Int>): Boolean {
        if (levels.size < 2 || levels[0] == levels[1]) return false

        val increasing = levels[1] > levels[0]
        return levels.windowed(2).all { (n1, n2) ->
            when (increasing) {
                true -> n2 > n1 && n2 - n1 >= 1 && n2 - n1 <= 3
                false -> n2 < n1 && n1 - n2 >= 1 && n1 - n2 <= 3
            }
        }
    }

    for (line in input) {
        val levels = line.split(" ").map { it.toInt() }

        if (isSafe(levels) ||
            levels.indices.any { i -> isSafe(levels.toMutableList().apply { removeAt(i) }) }
        ) {
            count++
        }
    }

    return count.toString()
}
