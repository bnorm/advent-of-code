package aoc.day15

import aoc.input.downloadInput
import utils.grid2d.*

const val SAMPLE1_SMALL = """
########
#..O.O.#
##@.O..#
#...O..#
#.#.O..#
#...O..#
#......#
########

<^^>>>vv<v>>v<<
"""

const val SAMPLE1 = """
##########
#..O..O.O#
#......O.#
#.OO..O.O#
#..O@..O.#
#O#..O...#
#O..O..O.#
#.OO.O.OO#
#....O...#
##########

<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
>^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 15)

    val part1Small = part1(SAMPLE1_SMALL)
    require(part1Small == "2028") { part1Small }

    val part1 = part1(SAMPLE1)
    require(part1 == "10092") { part1 }

    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "9021") { part2 }

    println(part2(input))
}

private fun part1(input: String): String {
    val (mapInput, instructionsInput) = input.trim().split("\n\n")
    val map = MutableGrid(mapInput.lines().map { it.toList() })
    val instructions = instructionsInput.mapNotNull { it.toDirection() }

    var robot = map.points.single { map[it] == '@' }
    for (direction in instructions) {
        robot = move1(map, robot, direction)
    }

//    for (y in map.ySpan) {
//        for (x in map.xSpan) {
//            print(map[x, y])
//        }
//        println()
//    }
//    println()

    val sum = map.points
        .filter { map[it] == 'O' }
        .sumOf { it.y * 100 + it.x }

    return sum.toString()
}

private fun part2(input: String): String {
    val (mapInput, instructionsInput) = input.trim().split("\n\n")
    val map = MutableGrid(mapInput.lines().map { it.expandWidth() })
    val instructions = instructionsInput.mapNotNull { it.toDirection() }

    var robot = map.points.single { map[it] == '@' }
    for (direction in instructions) {
        robot = move2(map, robot, direction)
    }

    val sum = map.points
        .filter { map[it] == '[' }
        .sumOf { it.y * 100 + it.x }

    return sum.toString()
}

private fun Char.toDirection(): Direction? {
    // !!! North and South are intentionally swapped to maintain directional movement !!!
    return when (this) {
        '^' -> Direction.South // Y is decreasing.
        '>' -> Direction.East
        'v' -> Direction.North // Y is increasing.
        '<' -> Direction.West
        else -> null
    }
}

private fun move1(grid: MutableGrid<Char>, robot: Point, direction: Direction): Point {
    require(grid[robot] == '@') { "invalid robot location: $robot" }

    val span = when (direction) {
        Direction.North -> grid.column(robot.x).subSpan(start = robot.y)
        Direction.East -> grid.row(robot.y).subSpan(start = robot.x)
        Direction.South -> grid.column(robot.x).subSpan(endExclusive = robot.y + 1).asReverse()
        Direction.West -> grid.row(robot.y).subSpan(endExclusive = robot.x + 1).asReverse()
    }

    val firstEmptyOrWall = (1..<span.size).firstOrNull { span[it] == '.' || span[it] == '#' }
    if (firstEmptyOrWall == null || span[firstEmptyOrWall] == '#') {
        // No empty space between the robot and the nearest wall.
        return robot
    }

    // Push into the empty space.
    for (i in firstEmptyOrWall downTo 0) {
        span[i] = when (i) {
            0 -> '.'
            else -> span[i - 1]
        }
    }

    return robot.move(direction)
}

fun String.expandWidth(): List<Char> = buildList {
    for (c in this@expandWidth) {
        when (c) {
            'O' -> {
                add('[')
                add(']')
            }

            '@' -> {
                add('@')
                add('.')
            }

            else -> {
                add(c)
                add(c)
            }
        }
    }
}

private fun move2(grid: MutableGrid<Char>, robot: Point, direction: Direction): Point {
    require(grid[robot] == '@') { "invalid robot location: $robot" }
    if (direction == Direction.East || direction == Direction.West) return move1(grid, robot, direction)

    val y = if (direction == Direction.South) grid.ySpan.endInclusive - robot.y else robot.y
    val grid = if (direction == Direction.South) grid.reverse(yReverse = true) else grid

    fun boxes(y: Int, xSpan: IntRange): List<IntRange> = buildList {
        var startX = -1
        for (nextX in xSpan) {
            if (nextX == xSpan.first && grid[nextX, y] == ']') {
                // Expand X to include the start of a box.
                startX = nextX - 1
            } else if (grid[nextX, y] == '.') {
                if (startX != -1) {
                    add(startX..<nextX)
                    startX = -1
                }
            } else if (grid[nextX, y] == '[') {
                if (startX == -1) startX = nextX
            }

            if (nextX == xSpan.last) {
                if (grid[nextX, y] == '[') {
                    // Expand X to include the end of a box.
                    add(startX..nextX + 1)
                } else if (startX != -1) {
                    // Make sure to include the last range.
                    add(startX..nextX)
                }
            }
        }
    }

    fun check(y: Int, xSpan: IntRange): Boolean {
        val nextY = y + 1
        if (xSpan.any { grid[it, nextY] == '#' }) return false
        return boxes(nextY, xSpan).all { check(nextY, it) }
    }

    fun push(y: Int, xSpan: IntRange) {
        val nextY = y + 1
        boxes(nextY, xSpan).forEach { push(nextY, it) }
        for (x in xSpan) {
            grid[x, nextY] = grid[x, y]
            grid[x, y] = '.'
        }
    }

    if (check(y, robot.x..robot.x)) {
        push(y, robot.x..robot.x)
        return robot.move(direction)
    } else {
        return robot
    }
}
