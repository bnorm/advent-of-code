package aoc.day01

import aoc.input.downloadInput
import kotlin.math.abs

private const val SAMPLE1 = """
3   4
4   3
2   5
1   3
3   9
3   3
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 1).trim().lines()
    val sample1 = SAMPLE1.trim().lines()
    val sample2 = SAMPLE2.trim().lines()

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
