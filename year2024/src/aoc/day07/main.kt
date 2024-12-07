package aoc.day07

import aoc.input.downloadInput

const val SAMPLE1 = """
190: 10 19
3267: 81 40 27
83: 17 5
156: 15 6
7290: 6 8 6 15
161011: 16 10 13
192: 17 8 14
21037: 9 7 18 13
292: 11 6 16 20
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 7)

    val part1 = part1(SAMPLE1)
    require(part1 == "3749") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "11387") { part2 }
    println(part2(input))
}

private fun part1(input: String): String {
    fun isValid(value: Long, equation: List<Long>): Boolean {
        fun recursion(carry: Long, i: Int): Boolean {
            return when (i) {
                equation.size -> carry == value
                else -> recursion(carry + equation[i], i + 1) ||
                        recursion(carry * equation[i], i + 1)
            }
        }

        return recursion(equation[0], 1)
    }

    var sum = 0L
    val lines = input.trim().lines()
    for (line in lines) {
        val (value, equation) = line.split(": ")
        if (isValid(value.toLong(), equation.split(" ").map { it.toLong() })) {
            sum += value.toLong()
        }
    }

    return sum.toString()
}

private fun part2(input: String): String {
    fun isValid(value: Long, equation: List<Long>): Boolean {
        fun recursion(carry: Long, i: Int): Boolean {
            return when (i) {
                equation.size -> carry == value
                else -> recursion(carry + equation[i], i + 1) ||
                        recursion(carry * equation[i], i + 1) ||
                        recursion((carry.toString() + equation[i].toString()).toLong(), i + 1)
            }
        }

        return recursion(equation[0], 1)
    }

    var sum = 0L
    val lines = input.trim().lines()
    for (line in lines) {
        val (value, equation) = line.split(": ")
        if (isValid(value.toLong(), equation.split(" ").map { it.toLong() })) {
            sum += value.toLong()
        }
    }

    return sum.toString()
}