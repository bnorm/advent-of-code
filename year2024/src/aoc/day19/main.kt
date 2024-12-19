package aoc.day19

import aoc.input.downloadInput

const val SAMPLE1 = """
r, wr, b, g, bwu, rb, gb, br

brwrr
bggr
gbbr
rrbgbr
ubwu
bwurrg
brgr
bbrgwb
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 19)

    val part1 = part1(SAMPLE1)
    require(part1 == "6") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "16") { part2 }
    println(part2(input))
}

private fun part1(input: String): String {
    val (towelsInput, patternsInput) = input.trim().split("\n\n")
    val towels = towelsInput.split(", ")
    val patterns = patternsInput.lines()

    val memory = mutableMapOf<String, Boolean>()
    for (towel in towels) {
        memory[towel] = true
    }

    fun isPossible(pattern: String): Boolean {
        if (pattern.isEmpty()) return true
        return memory.getOrPut(pattern) {
            for (t in towels) {
                if (pattern.startsWith(t) && isPossible(pattern.substring(startIndex = t.length))) {
                    return@getOrPut true
                }
            }
            false
        }
    }

    return patterns.count { isPossible(it) }.toString()
}

private fun part2(input: String): String {
    val (towelsInput, patternsInput) = input.trim().split("\n\n")
    val towels = towelsInput.split(", ")
    val patterns = patternsInput.lines()

    val memory = mutableMapOf<String, Long>()
    fun permutations(pattern: String): Long {
        if (pattern.isEmpty()) return 1L
        return memory.getOrPut(pattern) {
            var sum = 0L
            for (t in towels) {
                if (pattern.startsWith(t)) {
                    sum += permutations(pattern.substring(startIndex = t.length))
                }
            }
            sum
        }
    }

    return patterns.sumOf { permutations(it) }.toString()
}
