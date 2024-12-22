package aoc.day21

import aoc.input.downloadInput
import utils.grid2d.*

const val SAMPLE1 = """
029A
980A
179A
456A
379A
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 21)

    val part1 = part1(SAMPLE1)
    require(part1 == "126384") { part1 }
    println(part1(input))
    println(part2(input))
}

private fun part1(input: String): String {
    val codes = input.trim().lines()

    var sum = 0L
    for (code in codes) {
        val instructions = RobotLayer.Keypad.type(code)
            .flatMap { RobotLayer.Arrows.type(it) }
            .flatMap { RobotLayer.Arrows.type(it) }
        sum += instructions.sumOf { it.length } * code.substringBefore('A').toLong()
    }

    return sum.toString()
}


private fun part2(input: String): String {
    val codes = input.trim().lines()

    var sum = 0L
    for (code in codes) {
        var instructions = RobotLayer.Keypad.type(code)
            .groupingBy { it }.eachCount().mapValues { it.value.toLong() }

        repeat(25) {
            val newInstructions = mutableMapOf<String, Long>()
            for ((key, count) in instructions) {
                for (instruction in RobotLayer.Arrows.type(key)) {
                    newInstructions.compute(instruction) { _, v -> (v ?: 0) + count }
                }
            }
            instructions = newInstructions
        }

        sum += instructions.entries.sumOf { (key, count) -> count * key.length } * code.substringBefore('A').toLong()
    }

    return sum.toString()
}

/**
 * +---+---+---+
 * | 7 | 8 | 9 |
 * +---+---+---+
 * | 4 | 5 | 6 |
 * +---+---+---+
 * | 1 | 2 | 3 |
 * +---+---+---+
 *     | 0 | A |
 *     +---+---+
 */

private val keys = Grid(
    listOf(
        listOf('7', '8', '9'),
        listOf('4', '5', '6'),
        listOf('1', '2', '3'),
        listOf('#', '0', 'A'),
    ).asReversed()
)

/**
 *     +---+---+
 *     | ^ | A |
 * +---+---+---+
 * | < | v | > |
 * +---+---+---+
 */

private val arrows = Grid(
    listOf(
        listOf('#', '^', 'A'),
        listOf('<', 'v', '>'),
    ).asReversed()
)

enum class RobotLayer(private val grid: Grid<Char>) {
    Keypad(keys),
    Arrows(arrows),
    ;

    private val points = grid.points.associateBy { grid[it] }
    private val memory = mutableMapOf<String, List<String>>()

    fun type(code: String): List<String> = memory.getOrPut(code) {
        buildList {
            var start = points.getValue('A')
            for (key in code) {
                val end = points.getValue(key)
                add(buildInstruction(grid, start, end))
                start = end
            }
        }
    }
}

fun buildInstruction(
    grid: Grid<Char>,
    start: Point,
    end: Point,
): String {
    if (start == end) return "A"

    return buildString {
        fun moveSouth() = repeat(start.y - end.y) { append('v') }
        fun moveNorth() = repeat(end.y - start.y) { append('^') }
        fun moveEast() = repeat(end.x - start.x) { append('>') }
        fun moveWest() = repeat(start.x - end.x) { append('<') }

        if (grid[end.x, start.y] == '#') {
            // Have to move vertically first.
            moveSouth()
            moveNorth()
            moveEast()
            moveWest()
        } else if (grid[start.x, end.y] == '#') {
            // Have to move horizontally first.
            moveEast()
            moveWest()
            moveSouth()
            moveNorth()
        } else {
            // Can move either vertically or horizontally first.
            // Manual testing revealed the following preferences:
            // 1. Go West then South.
            // 2. Go West then North.
            // 3. Go South then East.
            // 4. North vs. East doesn't seem to matter.
            moveWest()
            moveSouth()
            moveNorth()
            moveEast()
        }

        append('A')
    }
}
