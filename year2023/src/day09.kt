package day09

import utils.*

fun main() {
    val sample1 = readInput("day09.sample1.txt")
    val sample2 = readInput("day09.sample2.txt")
    val input = readInput("day09.txt")

    require(part1(sample1) == "114")
    println(part1(input))

    require(part2(sample2) == "2")
    println(part2(input))
}

fun part1(input: List<String>): String {
    var result = 0L
    for (line in input) {
        result += extrapolateForwards(line.split(" ").map { it.toLong() })
    }
    return result.toString()
}

fun part2(input: List<String>): String {
    var result = 0L
    for (line in input) {
        result += extrapolateBackwards(line.split(" ").map { it.toLong() })
    }
    return result.toString()
}

fun List<Long>.deltas() = windowed(2) { (a, b) -> b - a }

fun extrapolateForwards(values: List<Long>): Long {
    return when {
        values.all { it == 0L } -> 0
        else -> values.last() + extrapolateForwards(values.deltas())
    }
}

fun extrapolateBackwards(values: List<Long>): Long {
    return when {
        values.all { it == 0L } -> 0
        else -> values.first() - extrapolateBackwards(values.deltas())
    }
}
