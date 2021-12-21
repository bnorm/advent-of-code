fun main() {
    val sample = """
        BFFFBBFRRR
    """.trimIndent()

//    val input = sample.splitToSequence("\n")
    val input = readResourceText("input.txt").splitToSequence("\n")
    val values = input.filter { it.isNotBlank() }.toList()

    val seats = values.map { it.translate("BFRL", "1010").toInt(2) }.sorted()
    println("part1 = ${seats.last()}")

    val seat = seats.windowed(2).filter { (l, r) -> l + 2 == r }.map { (l, _) -> l + 1 }.single()
    println("part2 = $seat")
}
