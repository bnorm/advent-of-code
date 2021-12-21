fun main() {
    val sample = """
        1721
        979
        366
        299
        675
        1456
    """.trimIndent()

    val input = readResourceText("input.txt").splitToSequence("\n")
    val values = input.mapNotNull { it.toIntOrNull() }.toList()

    val map = mutableMapOf<Int, Int>()
    for ((index, first) in values.withIndex()) {
        for (second in values.subList(index + 1, values.size)) {
            val opposite = 2020 - first - second
            if (opposite in map) {
                println("$opposite * $first * $second = ${opposite * first * second}")
                break
            }
        }
        map[first] = first
    }
}
