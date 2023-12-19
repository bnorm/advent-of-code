package aoc.day25

import utils.*

fun main() {
    val input = readInput("aoc/day25/input.txt")
    val sample1 = readInput("aoc/day25/sample1.txt")
    val sample2 = readInput("aoc/day25/sample2.txt")

    val part1 = part1(sample1)
    require(part1 == "") { part1 }
    println(part1(input))

    val part2 = part2(sample2)
    require(part2 == "") { part2 }
    println(part2(input))
}

fun part1(input: List<String>): String {
    TODO()
}

fun part2(input: List<String>): String {
    TODO()
}
