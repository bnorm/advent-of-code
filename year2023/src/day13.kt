package day13

import utils.*

fun main() {
    val sample1 = readInput("day13.sample1.txt")
    val sample2 = readInput("day13.sample2.txt")
    val input = readInput("day13.txt")

    // 23572
    val part1 = part1(sample1)
    require(part1 == "405") { part1 }
    println(part1(input))

    val part2 = part2(sample2)
    require(part2 == "400") { part2 }
    println(part2(input))
}

fun part1(input: List<String>): String {
    val groups = input.separateBy { it.isEmpty() }
    return groups.sumOf { findReflection(it) }.toString()
}

fun part2(input: List<String>): String {
    val groups = input.separateBy { it.isEmpty() }
    return groups.sumOf { findDirtyReflection(it) }.toString()
}

fun findReflection(grid: List<String>): Int {
    fun find(rows: List<String>): Int? {
        rows@ for (index in 1..<rows.size) {
            var j = index - 1
            var k = index
            while (j >= 0 && k < rows.size) {
                if (rows[j] != rows[k]) continue@rows
                k++
                j--
            }
            return index
        }
        return null
    }

    find(grid)?.let { return 100 * it }
    find(grid.transpose())?.let { return it }
    error("!")
}

fun findDirtyReflection(grid: List<String>): Int {
    fun diff(first: String, second: String): Int =
        first.zip(second) { a, b -> if (a == b) 0 else 1 }.sum()

    fun find(rows: List<String>): Int? {
        rows@ for (index in 1..<rows.size) {
            var j = index - 1
            var k = index
            var difference = 0
            while (j >= 0 && k < rows.size) {
                difference += diff(rows[j], rows[k])
                if (difference > 1) continue@rows
                k++
                j--
            }
            if (difference == 1) return index
        }
        return null
    }

    find(grid)?.let { return 100 * it }
    find(grid.transpose())?.let { return it }
    error("!")
}

fun List<String>.transpose(): List<String> {
    val columns = mutableListOf<String>()

    for (c in 0..<this[0].length) {
        columns.add(this.map { it[c] }.joinToString(""))
    }

    return columns
}
