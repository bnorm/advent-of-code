package aoc.day11

import aoc.input.downloadInput

const val SAMPLE1 = """
125 17
"""

suspend fun main() {
    val input = downloadInput(2024, 11)

    val part1 = part1(SAMPLE1)
    require(part1 == "55312") { part1 }
    println(part1(input))
    println(part2(input))
}

private fun part1(input: String): String {
    return compute(input, 25)
}

private fun part2(input: String): String {
    return compute(input, 75)
}

private fun compute(input: String, blinks: Int): String {
    var stones = input.trim().split(" ").map { it.toLong() }
        .groupingBy { it }.eachCount().mapValues { it.value.toLong() }

    fun MutableMap<Long, Long>.increment(key: Long, count: Long) {
        put(key, count + getOrDefault(key, 0))
    }

    repeat(blinks) {
        stones = buildMap {
            for ((n, count) in stones) {
                when {
                    n == 0L -> increment(1, count)

                    n.toString().length % 2 == 0 -> {
                        val str = n.toString()
                        increment(str.substring(0, str.length / 2).toLong(), count)
                        increment(str.substring(str.length / 2, str.length).toLong(), count)
                    }

                    else -> increment(n * 2024L, count)
                }
            }
        }
    }

    return stones.values.sum().toString()
}
