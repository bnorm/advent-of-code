fun main() {
    val sample = """
        sesenwnenenewseeswwswswwnenewsewsw
        neeenesenwnwwswnenewnwwsewnenwseswesw
        seswneswswsenwwnwse
        nwnwneseeswswnenewneswwnewseswneseene
        swweswneswnenwsewnwneneseenw
        eesenwseswswnenwswnwnwsewwnwsene
        sewnenenenesenwsewnenwwwse
        wenwwweseeeweswwwnwwe
        wsweesenenewnwwnwsenewsenwwsesesenwne
        neeswseenwwswnwswswnw
        nenwswwsewswnenenewsenwsenwnesesenew
        enewnwewneswsewnwswenweswnenwsenwsw
        sweneswneswneneenwnewenewwneswswnese
        swwesenesewenwneswnwwneseswwne
        enesenwswwswneneswsenwnewswseenwsese
        wnwnesenesenenwwnenwsewesewsesesew
        nenewswnwewswnenesenwnesewesw
        eneswnwswnwsenenwnwnwwseeswneewsenese
        neswnwewnwnwseenwseesewsenwsweewe
        wseweeenwnesenwwwswnew
    """.trimIndent()

//    val input = sample.trim().splitToSequence("\n")
    val input = readResourceText("input.txt").trim().splitToSequence("\n")

    val paths = input.map { line ->
        val instructions = mutableListOf<String>()
        var remaining = line
        while (remaining.isNotEmpty()) {
            val (instruction, rest) = remaining.extract("([ns]?[ew])(.*)")!!
            instructions.add(instruction)
            remaining = rest
        }
        Tile(instructions.groupingBy { it }.eachCount())
    }
        .map { tile -> tile.reduce() }
        .groupingBy { it }
        .eachCount()

    val blackTiles = paths.filterValues { it % 2 == 1 }

    val part1 = blackTiles.size
    println("part1=$part1")

    var blackTilesIter = blackTiles.keys.toSet()
    repeat(100) {
//        println("Day $it: ${blackTilesIter.size} = $blackTilesIter")
        blackTilesIter = next(blackTilesIter)
    }
//    println("Day 100: ${blackTilesIter.size} = $blackTilesIter")

    val part2 = blackTilesIter.size
    println("part2=$part2")
}

data class Tile(
    val path: Map<String, Int>
) {
    override fun toString(): String {
        return path.entries.joinToString("") { (k, v) -> "$k$v" }
    }
}

fun Tile.adjacent(): List<Tile> = sequenceOf(
    path + ("e" to ((path["e"] ?: 0) + 1)),
    path + ("ne" to ((path["ne"] ?: 0) + 1)),
    path + ("se" to ((path["se"] ?: 0) + 1)),
    path + ("w" to ((path["w"] ?: 0) + 1)),
    path + ("nw" to ((path["nw"] ?: 0) + 1)),
    path + ("sw" to ((path["sw"] ?: 0) + 1)),
).map { Tile(it).reduce() }.toList()

fun next(paths: Set<Tile>): Set<Tile> {
    val blackAdjacentCount = mutableMapOf<Tile, Int>()

    for (path in paths) {
        for (adjacent in path.adjacent()) {
            blackAdjacentCount[adjacent] = (blackAdjacentCount[adjacent] ?: 0) + 1
        }
    }

    return (
            paths.filter { path -> blackAdjacentCount[path] in 1..2 } +
                    blackAdjacentCount.filter { (k, v) -> k !in paths && v == 2 }.keys
            ).toSet()
}

private fun Tile.reduce(): Tile {
    val reduced = path.toMutableMap().withDefault { 0 }

    fun substitute(i1: String, vararg iR: String) {
        val count = reduced.getValue(i1)
        reduced[i1] = 0
        for (i in iR) {
            reduced[i] = reduced.getValue(i) + count
        }
    }

    fun cancelPairs(i1: String, i2: String) {
        val i1Count = reduced.getValue(i1)
        val i2Count = reduced.getValue(i2)
        if (i1Count > 0 && i2Count > 0) {
            val count = minOf(i1Count, i2Count)
            reduced[i1] = i1Count - count
            reduced[i2] = i2Count - count
        }
    }

    substitute("w", "nw", "sw")
    substitute("e", "ne", "se")

    cancelPairs("se", "nw")
    cancelPairs("ne", "sw")


    return Tile(reduced.filterValues { it > 0 }.toSortedMap())
}
