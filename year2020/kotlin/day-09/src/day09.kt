fun main() {
    val sample = """
        35
        20
        15
        25
        47
        40
        62
        55
        65
        95
        102
        117
        150
        182
        127
        219
        299
        277
        309
        576
    """.trimIndent()

//    val input = sample.trim().splitToSequence("\n")
    val input = readResourceText("input.txt").trim().splitToSequence("\n")

    val values = input.map { it.toLong() }.toList()
    val result = values
        .windowed(26)
        .filter { window ->
            val target = window.last()
            indexPairs(window.size - 1) { i, j ->
                if (window[i] != window[j] && window[i] + window[j] == target) {
                    return@filter false
                }
            }
            true
        }
        .map { it.last() }
        .single()

    println("part1=$result")

    var start = 0
    var end = 0
    var tally = 0L
    while (end < values.size && start <= end) {
        if (tally < result) {
            tally += values[end++]
        } else if (tally > result) {
            tally -= values[start++]
        } else { // tally==result
            break
        }
    }
    val sub = values.subList(start, end)
    val min = sub.minOrNull()!!
    val max = sub.maxOrNull()!!
    println("part2=${min + max}")
}
