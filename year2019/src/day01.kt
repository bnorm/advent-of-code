fun main() {
    val test = false
    val sample = """
        12
    """.trimIndent()

    val input = (if (test) sample else readResourceText("input-day01.txt"))
        .splitToSequence("\n")
    val values = input.filter { it.isNotBlank() }
        .map { it.toInt() }

    val part1 = values.map { weight -> weight / 3 - 2.toLong() }.sum()
    println("part1 = $part1")

    val part2 = values.map { weight ->
        generateSequence(weight / 3 - 2.toLong()) { it / 3 - 2.toLong() }.takeWhile { it >= 0 }.sum()
    }.sum()
    println("part2 = $part2")
}
