fun main() {
    val sample = """
        BFFFBBFRRR
    """.trimIndent()

//    val input = sample.splitToSequence("\n")
    val input = readResourceText("input.txt").splitToSequence("\n")
    val values = input.filter { it.isNotBlank() }.toList()

    val seatIds = values.map { code ->
        val row = rowDecode(code.substring(0, 7))
        val seat = seatDecode(code.substring(7))
        row * 8 + seat
    }.sorted()

    println("part1 = ${seatIds.last()}")

    val seatId = seatIds.windowed(2).filter { (l, r) -> l + 2 == r }.map { (l, _) -> l + 1 }.single()
    println("part2 = ${seatId}")
}

fun rowDecode(code: String, size: Int = 128): Int {
    var min = 0
    var max = size
    for (c in code) {
        when (c) {
            'B' -> min = (min + max) / 2
            'F' -> max = (min + max) / 2
        }
    }
    return min
}

fun seatDecode(code: String, size: Int = 8): Int {
    var min = 0
    var max = size
    for (c in code) {
        when (c) {
            'R' -> min = (min + max) / 2
            'L' -> max = (min + max) / 2
        }
    }
    return min
}
