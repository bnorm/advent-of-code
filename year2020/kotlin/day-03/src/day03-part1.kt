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
    val width = values[0].length
    val length = values.size

    var row = 0
    var column = 0
    var hits = 0
    while (row < length) {
        if (values[row][column % width] == '#') {
            hits++
        }

        row += 1
        column += 3
    }

    println("hits = ${hits}")
}
