package aoc.day02

import utils.readInput

fun main() {
    val input = readInput("aoc/day02/input.txt")
    val sample1 = readInput("aoc/day02/sample1.txt")
    val sample2 = readInput("aoc/day02/sample2.txt")

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
