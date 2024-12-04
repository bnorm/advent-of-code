package aoc.day01

import utils.readInput
import kotlin.math.abs

fun main() {
    val input = readInput("aoc/day01/input.txt")
    val sample1 = readInput("aoc/day01/sample1.txt")
    val sample2 = readInput("aoc/day01/sample2.txt")

    require(part1(sample1) == "11")
    println(part1(input))

    require(part2(sample2) == "31")
    println(part2(input))
}

private fun part1(input: List<String>): String {
    val list1 = mutableListOf<Int>()
    val list2 = mutableListOf<Int>()

    for (line in input) {
        val (n1, n2) = line.split("\\s+".toRegex())
        list1.add(n1.toInt())
        list2.add(n2.toInt())
    }

    list1.sort()
    list2.sort()

    val dist = list1.zip(list2).sumOf { (n1, n2) -> abs(n1 - n2) }

    return dist.toString()
}

private fun part2(input: List<String>): String {
    val list1 = mutableListOf<Int>()
    val list2 = mutableListOf<Int>()

    for (line in input) {
        val (n1, n2) = line.split("\\s+".toRegex())
        list1.add(n1.toInt())
        list2.add(n2.toInt())
    }

    val counts = list2.groupingBy { it }.eachCount()

    val similarity = list1.sumOf { it * counts.getOrDefault(it, 0) }

    return similarity.toString()
}
