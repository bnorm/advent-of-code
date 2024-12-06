package aoc.day03

import aoc.input.downloadInput
import java.util.*

const val SAMPLE1 = """
xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))
"""

const val SAMPLE2 = """
xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))
"""

suspend fun main() {
    val input = downloadInput(2024, 3)

    val part1 = part1(SAMPLE1)
    require(part1 == "161") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "48") { part2 }
    println(part2(input))
}

private fun part1(input: String): String {
    val mulRegex = "mul\\((?<n1>\\d+),(?<n2>\\d+)\\)".toRegex()
    fun MatchResult.mul(): Int {
        val (n1, n2) = destructured
        return n1.toInt() * n2.toInt()
    }

    val result = mulRegex.findAll(input).map { it.mul() }.sum()
    return result.toString()
}

private fun part2(input: String): String {
    val mulRegex = "mul\\((?<n1>\\d+),(?<n2>\\d+)\\)".toRegex()
    val doRegex = "do\\(\\)".toRegex()
    val dontRegex = "don't\\(\\)".toRegex()

    val control = TreeMap<Int, Boolean>()
    doRegex.findAll(input).forEach { control[it.range.last] = true }
    dontRegex.findAll(input).forEach { control[it.range.last] = false }

    fun MatchResult.mul(): Int {
        if (control.lowerEntry(range.last)?.value == false) return 0
        val (n1, n2) = destructured
        return n1.toInt() * n2.toInt()
    }

    val result = mulRegex.findAll(input).map { it.mul() }.sum()
    return result.toString()
}
