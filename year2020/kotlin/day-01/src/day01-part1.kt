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
    val values = input.mapNotNull { it.toIntOrNull() }

    val map = mutableMapOf<Int, Int>()
    for ((_, value) in values.withIndex()) {
        val opposite = 2020 - value
        if (opposite in map) {
            println("$value * $opposite = ${value * opposite}")
            break
        }
        map[value] = value
    }
}
