fun main() {
    val sample = """
        28
        33
        18
        42
        31
        14
        46
        20
        48
        47
        24
        23
        49
        45
        19
        38
        39
        11
        1
        32
        25
        35
        8
        17
        7
        9
        4
        2
        34
        10
        3
    """.trimIndent()

    assert(permutations(listOf(1, 2, 3)) == 2)
    assert(permutations(listOf(1, 2, 3, 4)) == 4)
    assert(permutations(listOf(1, 2, 3, 4, 5)) == 7)
    assert(permutations(listOf(1, 2, 3, 4, 5, 6)) == 13)
    assert(permutations(listOf(1, 2, 3, 4, 5, 6, 7)) == 24)

//    val input = sample.trim().splitToSequence("\n")
    val input = readResourceText("input.txt").trim().splitToSequence("\n")

    val values = sequence {
        val adapters = input.map { it.toInt() }.sorted()
        yield(0)
        yieldAll(adapters)
        yield(adapters.last() + 3)
    }.toList()

    val part1 = values
        .windowed(2) {
            val (prev, next) = it
            next - prev
        }
        .groupingBy { it }
        .eachCount()

    println("part1=${part1[1]!! * part1[3]!!}")

    println("part2=${dynamic(values)}")
}

private fun recursive(values: List<Int>): Long {
    var count = 1L
    var start = 0
    var end = 0
    while (end < values.size - 1) {
        end++
        if (values[end] - values[end - 1] >= 3) {
            // Extract ranges with adjacent numbers, these are the only ranges which can actually be changed
            // E.g., if you have the list [1,2,3,6,7,8], break it into [1,2,3] and [6,7,8] and calculate
            // permutations on each separately
            count *= permutations(values.subList(start, end))
            start = end
        }
    }
    if (end > 0 && values[end] - values[end - 1] < 3) {
        val sub = values.subList(start, end + 1)
        val permutations = permutations(sub)
        count *= permutations
    }
    return count
}

fun permutations(values: List<Int>): Int {
    var count = 1
    for (i in 1 until values.size - 1) {
        // add permutations without the i-1..i elements
        count += permutations(values.subList(i + 1, values.size))
    }
    for (i in 1 until values.size - 2) {
        // add permutations without the i-1..i+1 elements
        count += permutations(values.subList(i + 2, values.size))
    }
    return count
}

private fun dynamic(values: List<Int>): Long {
    // values = [ 0, 1, 2, 3, 6, 7, 8, 9,10,11,14]
    // memory = [ 1, 1, 2, 4, 4, 4, 8,16,28,52,52]
    // sum of previous 3 whose values are within at most 3

    return dynamic(values.size) { i, memory ->
        var sum = 0L
        for (j in maxOf(0, i - 3) until i) {
            if (values[i] - values[j] <= 3) {
                sum += memory[j]
            }
        }
        maxOf(sum, 1L)
    }
}
