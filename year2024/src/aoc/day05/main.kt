package aoc.day05

import aoc.input.downloadInput

const val SAMPLE1 = """
47|53
97|13
97|61
97|47
75|29
61|13
75|53
29|13
97|29
53|29
61|53
97|53
61|29
47|13
75|47
97|75
47|61
75|61
47|29
75|13
53|13

75,47,61,53,29
97,61,53,29,13
75,29,13
75,97,47,61,53
61,13,29
97,13,75,29,47
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 5)

    val part1 = part1(SAMPLE1)
    require(part1 == "143") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "123") { part2 }
    println(part2(input))
}

private fun part1(input: String): String {
    val (rulesInput, orderingsInput) = input.split("\n\n")

    val rules = rulesInput.trim().lines()
        .map { line -> line.split("|").map { it.toInt() } }
        .groupBy(keySelector = { it[0] }, valueTransform = { it[1] })
        .mapValues { (_, v) -> v.toSet() }

    val orderings = orderingsInput.trim().lines()
        .map { lines -> lines.split(",").map { it.toInt() } }

    var sum = 0
    orderings@ for (ordering in orderings) {
        for (i in ordering.indices) {
            val mustBeAfter = rules[ordering[i]] ?: continue
            val previous = ordering.subList(fromIndex = 0, toIndex = i)
            if (previous.any { it in mustBeAfter }) {
                continue@orderings
            }
        }

        sum += ordering[ordering.size / 2]
    }

    return sum.toString()
}

private fun part2(input: String): String {
    val (rulesInput, orderingsInput) = input.split("\n\n")

    val rules = rulesInput.trim().lines()
        .map { line -> line.split("|").map { it.toInt() } }
        .groupBy(keySelector = { it[0] }, valueTransform = { it[1] })
        .mapValues { (_, v) -> v.toSet() }

    val orderings = orderingsInput.trim().lines()
        .map { lines -> lines.split(",").map { it.toInt() } }

    val incorrect = mutableListOf<MutableList<Int>>()
    orderings@ for (ordering in orderings) {
        for (i in ordering.indices) {
            val mustBeAfter = rules[ordering[i]] ?: continue
            val previous = ordering.subList(fromIndex = 0, toIndex = i)
            if (previous.any { it in mustBeAfter }) {
                incorrect.add(ordering.toMutableList())
                continue@orderings
            }
        }
    }

    for (ordering in incorrect) {
        ordering.sortWith { n1, n2 -> if (n1 in rules[n2].orEmpty()) -1 else 0 }
    }

    val sum = incorrect.sumOf { it[it.size / 2] }
    return sum.toString()
}
