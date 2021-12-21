fun main() {
    val sample = """
        ..##.......
        #...#...#..
        .#....#..#.
        ..#.#...#.#
        .#...##..#.
        ..#.##.....
        .#.#.#....#
        .#........#
        #.##...#...
        #...##....#
        .#..#...#.#
    """.trimIndent()

    val input = readResourceText("input.txt").splitToSequence("\n")
    val values = input.filter { it.isNotBlank() }.toList()

    val `1-1` = hitCount(values, 1, 1)
    val `3-1` = hitCount(values, 3, 1)
    val `5-1` = hitCount(values, 5, 1)
    val `7-1` = hitCount(values, 7, 1)
    val `1-2` = hitCount(values, 1, 2)

    println("`1-1` = ${`1-1`}")
    println("`3-1` = ${`3-1`}")
    println("`5-1` = ${`5-1`}")
    println("`7-1` = ${`7-1`}")
    println("`1-2` = ${`1-2`}")

    val ans = `1-1`.toLong() * `3-1` * `5-1` * `7-1` * `1-2`
    println("ans = ${ans}")
}

private fun hitCount(values: List<String>, columnSlope: Int, rowSlope: Int): Int {
    val width = values[0].length
    val length = values.size

    var row = 0
    var column = 0
    var hits = 0
    while (row < length) {
        if (values[row][column % width] == '#') {
            hits++
        }

        row += rowSlope
        column += columnSlope
    }

    return hits
}
