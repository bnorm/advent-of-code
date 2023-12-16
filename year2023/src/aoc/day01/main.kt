package aoc.day01

import utils.*

fun main() {
    val input = readInput("aoc/day01/input.txt")
    val sample1 = readInput("aoc/day01/sample1.txt")
    val sample2 = readInput("aoc/day01/sample2.txt")

    require(part1(sample1) == "142")
    println(part1(input))

    require(part2(sample2) == "281")
    println(part2(input))
}

private fun part1(input: List<String>): String {
    var result = 0
    for (line in input) {
        val first = line.firstNotNullOf { it.digitToIntOrNull() }
        val last = line.reversed().firstNotNullOf { it.digitToIntOrNull() }
        result += first * 10 + last
    }
    return result.toString()
}

private val numbers = listOf(
    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
    "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
)

private fun part2(input: List<String>): String {
    var result = 0
    for (line in input) {
        val first = line.findAnyOf(numbers)!!.let { (_, value) -> numbers.indexOf(value) }
        val last = line.findLastAnyOf(numbers)!!.let { (_, value) -> numbers.indexOf(value) }
        result += (first % 10) * 10 + (last % 10)
    }
    return result.toString()
}
