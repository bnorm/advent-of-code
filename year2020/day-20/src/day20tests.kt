fun test(tiles: Map<Int, Tile>) {
    val tile = tiles[2311]!!

    /*
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
     */

    test(tile.copy(rotation = 0, flipped = false), "..##.#..#.", "...#.##..#", "###..###..", ".#..#####.")
    test(tile.copy(rotation = 1, flipped = false), "...#.##..#", "###..###..", ".#..#####.", "..##.#..#.")
    test(tile.copy(rotation = 2, flipped = false), "###..###..", ".#..#####.", "..##.#..#.", "...#.##..#")
    test(tile.copy(rotation = 3, flipped = false), ".#..#####.", "..##.#..#.", "...#.##..#", "###..###..")

    test(tile.copy(rotation = 0, flipped = true), "###..###..".reversed(), "...#.##..#".reversed(), "..##.#..#.".reversed(), ".#..#####.".reversed())
    test(tile.copy(rotation = 1, flipped = true), ".#..#####.".reversed(), "###..###..".reversed(), "...#.##..#".reversed(), "..##.#..#.".reversed())
    test(tile.copy(rotation = 2, flipped = true), "..##.#..#.".reversed(), ".#..#####.".reversed(), "###..###..".reversed(), "...#.##..#".reversed())
    test(tile.copy(rotation = 3, flipped = true), "...#.##..#".reversed(), "..##.#..#.".reversed(), ".#..#####.".reversed(), "###..###..".reversed())
}

private fun test(tile: Tile, top: String, right: String, bottom: String, left: String) {
    require(tile[0] == top)
    require(tile[1] == right)
    require(tile[2] == bottom)
    require(tile[3] == left)

    val cTop = (0 until tile.size).joinToString("") { tile[0, it].toString() }
    val cRight = (0 until tile.size).joinToString("") { tile[it, tile.size - 1].toString() }
    val cBottom = (0 until tile.size).joinToString("") { tile[tile.size - 1, it].toString() }.reversed()
    val cLeft = (0 until tile.size).joinToString("") { tile[it, 0].toString() }.reversed()

    require(cTop == top) { "$cTop != $top" }
    require(cRight == right) { "$cRight != $right" }
    require(cBottom == bottom) { "$cBottom != $bottom" }
    require(cLeft == left) { "$cLeft != $left" }
}
