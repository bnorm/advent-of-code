package aoc.day22

import aoc.input.downloadInput

const val SAMPLE1 = """
1
10
100
2024
"""

const val SAMPLE2 = """
1
2
3
2024
"""

suspend fun main() {
    val input = downloadInput(2024, 22)

    val part1 = part1(SAMPLE1)
    require(part1 == "37327623") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "23") { part2 }
    println(part2(input))
}

private fun part1(input: String): String {
    val secrets = input.trim().lines().map { it.toInt() }

    var sum = 0L
    for (secret in secrets) {
        var n = secret
        repeat(2000) {
            n = n.nextRandom()
        }
        sum += n
    }

    return sum.toString()
}

private fun part2(input: String): String {
    val secrets = input.trim().lines().map { it.toInt() }
    val patternLength = 4
    val patterns = mutableMapOf<Int, Int>()

    for (secret in secrets) {
        val prices = IntArray(2001)
        prices[0] = secret
        repeat(2000) { prices[it + 1] = prices[it].nextRandom() }
        for (i in 0..<prices.size) {
            prices[i] = prices[i] % 10
        }

        // TODO this could be accumulated rather than calculated...
        fun bitPattern(start: Int, length: Int): Int {
            var pattern = 0
            repeat(length) {
                pattern = pattern shl 5
                pattern += 10 + prices[start + it + 1] - prices[start + it]
            }
            return pattern
        }

        val visited = mutableSetOf<Int>()
        for (i in 0..<prices.size - patternLength) {
            val pattern = bitPattern(i, patternLength)
            if (visited.add(pattern)) {
                patterns[pattern] = (patterns[pattern] ?: 0) + prices[i + patternLength]
            }
        }
    }

    return patterns.values.max().toString()
}

private const val PRUNE = 16777216 - 1
fun Int.nextRandom(): Int {
    // 16777216 = 2^24
    // 2048 = 2^11
    // 64 = 2^6
    // 32 = 2^5

    // N xor 0 = N
    // N % 2^x = N and (2^x - 1)

    var secret = this
    secret = secret shl 6 xor secret and PRUNE
    secret = secret shr 5 xor secret and PRUNE
    secret = secret shl 11 xor secret and PRUNE
    return secret
}
