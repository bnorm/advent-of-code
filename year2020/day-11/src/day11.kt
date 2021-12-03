fun main() {
    val sample = """
        L.LL.LL.LL
        LLLLLLL.LL
        L.L.L..L..
        LLLL.LL.LL
        L.LL.LL.LL
        L.LLLLL.LL
        ..L.L.....
        LLLLLLLLLL
        L.LLLLLL.L
        L.LLLLL.LL
    """.trimIndent()

//    val input = sample.trim()
    val input = readResourceText("input.txt").trim()

    val rowCount = input.count { it == '\n' } + 1
    val seats = input.toList().filter { it != '\n' }

    run {
        val part1 = generateSequence(seats) { prev ->
            prev.mapIndexed { i, seat ->
                if (seat == '#' && orthogonal(prev, i, rowCount).count { it == '#' } >= 4) {
                    'L'
                } else if (seat == 'L' && orthogonal(prev, i, rowCount).none { it == '#' }) {
                    '#'
                } else {
                    seat
                }
            }
        }
            .untilStable()
            .count { it == '#' }
        println("part1=$part1")
    }

    run {
        val part2 = generateSequence(seats) { prev ->
            prev.mapIndexed { i, seat ->
                if (seat == '#' && orthogonalSearch(prev, i, rowCount) { it != '.' }.count { it == '#' } >= 5) {
                    'L'
                } else if (seat == 'L' && orthogonalSearch(prev, i, rowCount) { it != '.' }.none { it == '#' }) {
                    '#'
                } else {
                    seat
                }
            }
        }
            .untilStable()
            .count { it == '#' }
        println("part2=$part2")
    }

}

@OptIn(ExperimentalStdlibApi::class)
fun <T> orthogonal(
    grid: List<T>,
    i: Int,
    rowCount: Int
): List<T> = buildList {
    val columnCount = grid.size / rowCount
    val startR = i / columnCount
    val startC = i % columnCount

    fun get(deltaR: Int, deltaC: Int): T? {
        val r = startR + deltaR
        val c = startC + deltaC
        if (r in 0 until rowCount && c in 0 until columnCount) {
            return grid[r * columnCount + c]
        }
        return null
    }

    add(get(-1, -1))
    add(get(-1, 0))
    add(get(-1, 1))
    add(get(0, -1))
    add(get(0, 1))
    add(get(1, -1))
    add(get(1, 0))
    add(get(1, 1))
}.filterNotNull()

@OptIn(ExperimentalStdlibApi::class)
fun <T> orthogonalSearch(
    grid: List<T>,
    i: Int,
    rowCount: Int,
    predicate: (T) -> Boolean
): List<T> = buildList {
    val columnCount = grid.size / rowCount
    val startR = i / columnCount
    val startC = i % columnCount

    fun search(deltaR: Int, deltaC: Int): T? {
        var r = startR + deltaR
        var c = startC + deltaC
        while (r in 0 until rowCount && c in 0 until columnCount) {
            val element = grid[r * columnCount + c]
            if (predicate(element)) {
                return element
            }
            r += deltaR
            c += deltaC
        }
        return null
    }

    add(search(-1, -1))
    add(search(-1, 0))
    add(search(-1, 1))
    add(search(0, -1))
    add(search(0, 1))
    add(search(1, -1))
    add(search(1, 0))
    add(search(1, 1))
}.filterNotNull()
