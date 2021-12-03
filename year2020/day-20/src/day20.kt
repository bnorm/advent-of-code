import kotlin.math.sqrt

fun main() {
    val sample = """
        Tile 2311:
        ..##.#..#.
        ##..#.....
        #...##..#.
        ####.#...#
        ##.##.###.
        ##...#.###
        .#.#.#..##
        ..#....#..
        ###...#.#.
        ..###..###

        Tile 1951:
        #.##...##.
        #.####...#
        .....#..##
        #...######
        .##.#....#
        .###.#####
        ###.##.##.
        .###....#.
        ..#.#..#.#
        #...##.#..

        Tile 1171:
        ####...##.
        #..##.#..#
        ##.#..#.#.
        .###.####.
        ..###.####
        .##....##.
        .#...####.
        #.##.####.
        ####..#...
        .....##...

        Tile 1427:
        ###.##.#..
        .#..#.##..
        .#.##.#..#
        #.#.#.##.#
        ....#...##
        ...##..##.
        ...#.#####
        .#.####.#.
        ..#..###.#
        ..##.#..#.

        Tile 1489:
        ##.#.#....
        ..##...#..
        .##..##...
        ..#...#...
        #####...#.
        #..#.#.#.#
        ...#.#.#..
        ##.#...##.
        ..##.##.##
        ###.##.#..

        Tile 2473:
        #....####.
        #..#.##...
        #.##..#...
        ######.#.#
        .#...#.#.#
        .#########
        .###.#..#.
        ########.#
        ##...##.#.
        ..###.#.#.

        Tile 2971:
        ..#.#....#
        #...###...
        #.#.###...
        ##.##..#..
        .#####..##
        .#..####.#
        #..#.#..#.
        ..####.###
        ..#.#.###.
        ...#.#.#.#

        Tile 2729:
        ...#.#.#.#
        ####.#....
        ..#.#.....
        ....#..#.#
        .##..##.#.
        .#.####...
        ####.#.#..
        ##.####...
        ##..#.##..
        #.##...##.

        Tile 3079:
        #.#.#####.
        .#..######
        ..#.......
        ######....
        ####.#..#.
        .#...#.##.
        #.#####.##
        ..#.###...
        ..#.......
        ..#.###...
    """.trimIndent()

//    val input = sample.trim().splitToSequence("\n\n")
    val input = readResourceText("input.txt").trim().splitToSequence("\n\n")

    val tiles = input.map { block ->
        val (id) = block.substringBefore('\n').extract("Tile (\\d+):")!!
        val image = block.substringAfter('\n').trim()
        Tile(id.toInt(), image.split("\n"))
    }.associateBy { it.id }

//    test(tiles)
//    println("tiles=$tiles")

    val sideLength = sqrt(tiles.size.toDouble()).toInt()
    val match = findMatch(tiles)
    println(match)

    val matchIds = match.map { it.id.toLong() }
    val corners = listOf(
        matchIds[0],
        matchIds[sideLength - 1],
        matchIds[matchIds.size - sideLength],
        matchIds[match.size - 1]
    )

    println(corners)
    println("part1=${corners.reduce { acc, n -> acc * n }}")

    val tileSideLength = match[0].size
    val image = buildString {
        repeat(sideLength) { mR ->
            repeat(tileSideLength) { tR ->
                if (tR in 1 until tileSideLength - 1) {
                    repeat(sideLength) { mC ->
                        val tile = match[sideLength * mR + mC]
                        repeat(tileSideLength) { tC ->
                            if (tC in 1 until tileSideLength - 1)
                                append(tile[tR, tC])
                        }
                    }
                    appendLine()
                }
            }
        }
    }.trim()

    val fullTile = Tile(0, image.split("\n"))
    val monster = """
        |                  # 
        |#    ##    ##    ###
        | #  #  #  #  #  #   
    """.trimMargin()

    for (flipped in listOf(false, true)) {
        for (rotation in 0 until 4) {
            val replacement = replace(fullTile.copy(rotation = rotation, flipped = flipped).content, monster)
            if (replacement.contains("0")) {
                println("part2=${replacement.count { it == '#' }}")
            }
        }
    }
}

fun replace(content: String, monster: String): String {
    val contentLines = content.split("\n")
    val monsterLines = monster.split("\n")

    var replace = contentLines
    repeat(contentLines.size - monsterLines.size) { r ->
        repeat(contentLines[0].length - monsterLines[0].length) { c ->
            replace = replace(r, c, replace, monsterLines)
        }
    }
    return replace.joinToString("\n")
}

fun replace(r: Int, c: Int, content: List<String>, monster: List<String>): List<String> {
    val replacement = content.toMutableList()
    for (iR in monster.indices) {
        for (iC in monster[iR].indices) {
            val m = monster[iR][iC]
            if (m == '#') {
                if (m != content[r + iR][c + iC]) {
                    return content
                } else {
                    val row = replacement[r + iR]
                    replacement[r + iR] = row.substring(0, c + iC) + "0" + row.substring(c + iC + 1)
                }
            }
        }
    }
    return replacement
}

data class Tile(
    val id: Int,
    val image: List<String>,
    val sides: List<String> = run {
        val (top, right, bottom, left) = MutableList(4) { StringBuilder() }
        for ((i, line) in image.withIndex()) {
            if (i == 0) top.append(line)
            bottom.clear().append(line)
            right.append(line.last())
            left.append(line.first())
        }

        listOf(top, right, bottom.reverse(), left.reverse()).map { it.toString() }
    },
    val rotation: Int = 0,
    val flipped: Boolean = false,
) {
    val size: Int = sides[0].length
    val content: String by lazy {
        buildString {
            repeat(size) { r ->
                repeat(size) { c ->
                    append(get(r, c))
                }
                appendLine()
            }
        }
    }

    operator fun get(side: Int): String {
        val i = rotation + side + (if (flipped && side % 2 == 0) 2 else 0)
        val value = sides[i % sides.size]
        return if (flipped) value.reversed()
        else value
    }

    operator fun get(r: Int, c: Int): Char {
        var aR = if (flipped) (size - 1) - r else r
        var aC = c
        repeat(rotation) {
            val tmp = aR
            aR = aC
            aC = ((size - 1) - tmp) % size
        }
//        println("($r, $c)->($aR, $aC)")
        return image[aR][aC]
    }

    override fun toString(): String {
        return "Tile($id, $rotation, $flipped)"
    }
}

fun findMatch(tiles: Map<Int, Tile>): List<Tile> {
    val sideLength = sqrt(tiles.size.toDouble()).toInt()


    fun recurse(memory: ArrayDeque<Tile>): List<Tile>? {
        val remaining = tiles.keys.filter { key -> memory.none { (id) -> key == id } }
//        println("$memory with $remaining")
        if (remaining.isEmpty()) return memory

        for (id in remaining) {
            for (flipped in listOf(false, true)) {
                for (rotation in 0 until 4) {
                    val tile = tiles[id]!!.copy(rotation = rotation, flipped = flipped)

                    if (memory.size % sideLength != 0) {
                        // compare left
                        val leftTile = memory.last()

                        val left = leftTile[1]
                        val right = tile[3].reversed()
                        if (left != right) {
//                            println("$memory -> left mismatch $tile and $leftTile  -> $right != $left")
                            continue
                        }

//                        println("$memory -> left match $tile and $leftTile  -> $right != $left")
                    }

                    if (memory.size >= sideLength) {
                        // compare top
                        val topTile = memory[memory.size - sideLength]

                        val top = topTile[2].reversed()
                        val bottom = tile[0]
                        if (top != bottom) {
//                            println("$memory -> top mismatch $tile and $topTile -> $bottom != $top")
                            continue
                        }
//                        println("$memory -> top match $tile and $topTile -> $bottom != $top")
                    }

                    memory.addLast(tile)
                    val result = recurse(memory)
                    if (result != null) return result
                    memory.removeLast()
                }
            }
        }

        return null
    }

    val memory = ArrayDeque<Tile>()
    for ((id) in tiles) {
        for (flipped in listOf(false, true)) {
            for (rotation in 0 until 4) {
                val tile = tiles[id]!!.copy(rotation = rotation, flipped = flipped)

                memory.addLast(tile)
                val result = recurse(memory)
                if (result != null) return result
                memory.removeLast()
            }
        }
    }

    TODO()
}

