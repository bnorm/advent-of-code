import intcode.Program

fun main() {
    val test = false
    val sample = """
        #######...#####
        #.....#...#...#
        #.....#...#...#
        ......#...#...#
        ......#...###.#
        ......#.....#.#
        ^########...#.#
        ......#.#...#.#
        ......#########
        ........#...#..
        ....#########..
        ....#...#......
        ....#...#......
        ....#...#......
        ....#####......
    """.trimIndent()

    val input = readResourceText("input-day17.txt")
        .splitToSequence("\n", ",")
    val program = input.filter { it.isNotBlank() }
        .map { it.trim().toLong() }
        .toList()

    val output = if (test) sample else {
        val result = Program(program).run()
//        result.toAscii()
        TODO()
    }

    val grid = output.split("\n")
        .filter { it.isNotBlank() }
        .map { it.toList() }

    println(output)

    skip {
        var sum = 0
        grid.windowed(3)
            .forEachIndexed { index, (row1, row2, row3) ->
                for (column in 1 until row1.size - 1) {
                    if (
                        row1[column] == '#' &&
                        row2[column - 1] == '#' && row2[column] == '#' && row2[column + 1] == '#' &&
                        row3[column] == '#'
                    ) {
                        sum += (index + 1) * column
                    }
                }
            }
        println("part1 = $sum")
    }

    run {
        val grid = Grid(grid)

        val robot = grid.indices.mapNotNull { (r, c) ->
            val value = grid[r, c]
            if (value in ROBOT_CHARACTERS) Robot(r, c, value) else null
        }.single()

        val movement = findPath(robot, grid)
        println(movement)

        val segments = findSegments(movement)
        println(segments)

        val instructions = buildInstructions(movement, segments)
        println(instructions)

        val input = (listOf(instructions.map { ('A'.toInt() + it).toChar() }.joinToString(",")) +
                segments.map { segment -> segment.joinToString(",") })
            .joinToString("\n", postfix = "\n")
            .map { it.toLong() }
        val inputIter = (input + 'n'.toLong() + '\n'.toLong()).iterator()


        val updatedProgram = program.toMutableList().apply { this[0] = 2L }

        val output = mutableListOf<Long>()
        Program(updatedProgram, { inputIter.next() }, { output.add(it) }).run()
        println(output.last())
    }
}

private fun buildInstructions(movement: List<String>, segments: List<List<String>>) = sequence<Int> {
    var i = 0
    while (i < movement.size) {
        val (index, segment) = segments.withIndex().first { (_, segment) ->
            i + segment.size <= movement.size &&
                    segment == movement.subList(i, i + segment.size)
        }
        yield(index)
        i += segment.size
    }
}.toList()

private fun findSegments(movement: List<String>): List<List<String>> {
    for (a in 1..movement.size) {
        val aPath = movement.subList(0, a)
        if (aPath.joinToString(",").length > 20) break

        val aRemaining = split(aPath, movement)

        println("aPath=$aPath aRemaining=$aRemaining")
        if (aRemaining.isEmpty()) return listOf(aPath)

        val aFirst = aRemaining.first()
        for (b in 1..aFirst.size) {
            val bPath = aFirst.subList(0, b)
            if (bPath.joinToString(",").length > 20) break

            run {
                val abRemaining = aRemaining.flatMap { split(bPath, it) }

                println("aPath=$aPath bPath=$bPath abRemaining=$abRemaining")
                if (abRemaining.isEmpty()) return listOf(aPath, bPath)

                val bFirst = abRemaining.first()
                for (c in 1..bFirst.size) {
                    val cPath = bFirst.subList(0, c)
                    if (cPath.joinToString(",").length > 20) break

                    val cRemaining = abRemaining.flatMap { split(cPath, it) }
                    println("aPath=$aPath bPath=$bPath cPath=$cPath cRemaining=$cRemaining")
                    if (cRemaining.isEmpty()) return listOf(aPath, bPath, cPath)
                }
            }

            run {
                val baRemaining = split(bPath, movement).flatMap { split(aPath, it) }

                println("aPath=$aPath bPath=$bPath baRemaining=$baRemaining")
                if (baRemaining.isEmpty()) return listOf(aPath, bPath)

                val bFirst = baRemaining.first()
                for (c in 1..bFirst.size) {
                    val cPath = bFirst.subList(0, c)
                    if (cPath.joinToString(",").length > 20) break

                    val cRemaining = baRemaining.flatMap { split(cPath, it) }
                    println("aPath=$aPath bPath=$bPath cPath=$cPath cRemaining=$cRemaining")
                    if (cRemaining.isEmpty()) return listOf(aPath, bPath, cPath)
                }
            }
        }
    }

    TODO()
}

private fun split(split: List<String>, value: List<String>): List<List<String>> {
    if (split.size > value.size) return listOf(value)
    if (split.size == value.size) {
        return if (split == value) emptyList()
        else listOf(value)
    }

    return sequence {
        var i = 0
        val builder = mutableListOf<String>()
        while (i <= value.size - split.size) {
            if (value.subList(i, i + split.size) == split) {
                i += split.size
                if (builder.isNotEmpty()) {
                    yield(builder.toList())
                    builder.clear()
                }
            } else {
                builder.add(value[i])
                i++
            }
        }
        if (builder.isNotEmpty() || i < value.size) {
            yield(builder + value.subList(i, value.size))
        }
    }.toList()
}

private fun findPath(robot: Robot, grid: Grid): List<String> {
    var current: Robot? = robot

    val builder = mutableListOf<String>()
    while (current != null) {
        current = when (current.value) {
            '^' -> {
                if (current.c != 0 && grid[current.r, current.c - 1] == '#') {
                    val count = countLeft(grid, current)
                    if (count > 0) {
                        builder.add("L,$count")
                        Robot(current.r, current.c - count, '<')
                    } else null
                } else {
                    val count = countRight(grid, current)
                    if (count > 0) {
                        builder.add("R,$count")
                        Robot(current.r, current.c + count, '>')
                    } else null
                }
            }
            'v' -> {
                if (current.c != 0 && grid[current.r, current.c - 1] == '#') {
                    val count = countLeft(grid, current)
                    if (count > 0) {
                        builder.add("R,$count")
                        Robot(current.r, current.c - count, '<')
                    } else null
                } else {
                    val count = countRight(grid, current)
                    if (count > 0) {
                        builder.add("L,$count")
                        Robot(current.r, current.c + count, '>')
                    } else null
                }
            }
            '<' -> {
                if (current.r != 0 && grid[current.r - 1, current.c] == '#') {
                    val count = countUp(grid, current)
                    if (count > 0) builder.add("R,$count")
                    moveUp(current, count)
                } else {
                    val count = countDown(grid, current)
                    if (count > 0) builder.add("L,$count")
                    moveDown(current, count)
                }
            }
            '>' -> {
                if (current.r != 0 && grid[current.r - 1, current.c] == '#') {
                    val count = countUp(grid, current)
                    if (count > 0) builder.add("L,$count")
                    moveUp(current, count)
                } else {
                    val count = countDown(grid, current)
                    if (count > 0) builder.add("R,$count")
                    moveDown(current, count)
                }
            }
            else -> throw IllegalStateException()
        }
    }
    return builder
}

private fun moveUp(robot: Robot, count: Int): Robot? {
    return if (count == 0) null
    else Robot(robot.r - count, robot.c, '^')
}

private fun moveDown(robot: Robot, count: Int): Robot? {
    return if (count == 0) null
    else Robot(robot.r + count, robot.c, 'v')
}

private fun countRight(grid: Grid, robot: Robot): Int {
    var column = robot.c + 1
    while (column < grid.columns && grid[robot.r, column] == '#') {
        column++
    }
    return column - robot.c - 1
}

private fun countLeft(grid: Grid, robot: Robot): Int {
    var column = robot.c - 1
    while (column >= 0 && grid[robot.r, column] == '#') {
        column--
    }
    return robot.c - column - 1
}

private fun countDown(grid: Grid, robot: Robot): Int {
    var row = robot.r + 1
    while (row < grid.rows && grid[row, robot.c] == '#') {
        row++
    }
    return row - robot.r - 1
}

private fun countUp(grid: Grid, robot: Robot): Int {
    var row = robot.r - 1
    while (row >= 0 && grid[row, robot.c] == '#') {
        row--
    }
    return robot.r - row - 1
}


private data class Robot(val r: Int, val c: Int, val value: Char)
private data class Command(val right: Boolean, val count: Int)
private class Grid(private val value: List<List<Char>>) {
    val rows = value.size
    val columns = value.first().size

    val indices = sequence {
        for (r in 0 until rows) {
            for (c in 0 until columns) {
                yield(r to c)
            }
        }
    }

    operator fun get(r: Int, c: Int): Char {
        require(r in 0 until rows && c in 0 until columns) { "r=$r c=$c !in ($rows, $columns)" }
        return value[r][c]
    }
}

val ROBOT_CHARACTERS = setOf('^', 'v', '<', '>')

fun skip(@Suppress("UNUSED_PARAMETER") block: () -> Unit) = Unit
