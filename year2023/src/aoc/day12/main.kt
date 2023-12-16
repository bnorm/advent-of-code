package aoc.day12

import utils.*

fun main() {
    val input = readInput("aoc/day12/input.txt")
    val sample1 = readInput("aoc/day12/sample1.txt")
    val sample2 = readInput("aoc/day12/sample2.txt")

    val part1 = part1(sample1)
    require(part1 == "21") { part1 }
    println(part1(input))

    val part2 = part2(sample2)
    require(part2 == "525152") { part2 }
    println(part2(input))
}

fun part1(input: List<String>): String {
    val records = input.map { Record.parse(it) }
    return records.sumOf { it.arrangements() }.toString()
}

fun part2(input: List<String>): String {
    val records = input.map { Record.parseFolded(it) }
    return records.sumOf { it.arrangements() }.toString()
}

data class Record(
    private val springs: List<Char>,
    private val groups: List<Int>,
) {
    private val groupsTotal = groups.sum()
    private val damageTotal = springs.count { it == '#' }
    private val unknownTotal = springs.count { it == '?' }

    fun arrangements(): Long {
        if (groupsTotal < damageTotal) return 0
        if (groupsTotal == 0 && damageTotal == 0) return 1
        if (groupsTotal > damageTotal + unknownTotal) return 0

        val index = groups.size / 2
        val value = groups[index]

        var sum = 0L
        for (i in 0..springs.size - value) {
            // Check adjacent to other groups.
            if (i > 0 && springs[i - 1] == '#') continue
            if (i + value < springs.size && springs[i + value] == '#') continue

            if (springs.subList(i, i + value).all { it == '#' || it == '?' }) {
                val left = Record(
                    springs = springs.subList(0, maxOf(0, i - 1)),
                    groups = groups.subList(0, index),
                )
                if (left.damageTotal > left.groupsTotal)
                    break // Scanned too far and exhausted possible matches on left side.

                val right = Record(
                    springs = springs.subList(minOf(i + value + 1, springs.size), springs.size),
                    groups = groups.subList(index + 1, groups.size)
                )
                if (right.damageTotal + right.unknownTotal < right.groupsTotal)
                    break // Scanned too far and exhausted possible matches on right side.

                val leftArrangements = left.arrangements()
                if (leftArrangements > 0) {
                    sum += leftArrangements * right.arrangements()
                }
            }
        }
        return sum
    }

    companion object {
        fun parse(line: String): Record {
            val (springs, groups) = line.split(" ")
            return Record(springs.toList(), groups.split(",").map { it.toInt() })
        }

        fun parseFolded(line: String): Record {
            val (springs, groups) = line.split(" ")
            val unfoldedSprings = List(5) { springs }.joinToString("?")
            val unfoldedGroups = List(5) { groups }.joinToString(",")
            return Record(unfoldedSprings.toList(), unfoldedGroups.split(",").map { it.toInt() })
        }
    }
}
