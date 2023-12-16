package aoc.day16

import grid2d.*
import utils.*

fun main() {
    val input = readInput("aoc/day16/input.txt")
    val sample1 = readInput("aoc/day16/sample1.txt")
    val sample2 = readInput("aoc/day16/sample2.txt")

    val part1 = part1(sample1)
    require(part1 == "46") { part1 }
    println(part1(input))

    val part2 = part2(sample2)
    require(part2 == "51") { part2 }
    println(part2(input))
}

fun part1(input: List<String>): String {
    val grid = Grid(input.map { row -> row.toList() }.reversed())
    val startingBeam = Beam(Point(0, grid.ySpan.last), Direction.East)
    return calculateEnergized(startingBeam, grid).toString()
}

fun part2(input: List<String>): String {
    val grid = Grid(input.map { row -> row.toList() }.reversed())

    val startingBeams = mutableListOf<Beam>()
    for (y in grid.ySpan) {
        startingBeams.add(Beam(Point(0, y), Direction.East))
        startingBeams.add(Beam(Point(grid.xSpan.last, y), Direction.West))
    }
    for (x in grid.xSpan) {
        startingBeams.add(Beam(Point(x, 0), Direction.North))
        startingBeams.add(Beam(Point(x, grid.ySpan.last), Direction.South))
    }

    return startingBeams.maxOf { calculateEnergized(it, grid) }.toString()
}

private fun calculateEnergized(startingBeam: Beam, grid: Grid<Char>): Int {
    val visited = mutableSetOf<Beam>()
    val beams = ArrayDeque<Beam>()
    beams.add(startingBeam)

    while (beams.isNotEmpty()) {
        val beam = beams.removeFirst()
        if (beam.location !in grid) continue
        if (!visited.add(beam)) continue // Already visited
        beams.addAll(beam.interact(grid[beam.location]))
    }

    return visited.distinctBy { it.location }.size
}

data class Beam(
    val location: Point,
    val direction: Direction,
)

enum class Direction {
    North,
    East,
    South,
    West,
}

fun Beam.interact(space: Char): List<Beam> {
    fun Beam.move(direction: Direction = this.direction): Beam {
        val location = when (direction) {
            Direction.East -> location + Point.ADJACENT[0]
            Direction.South -> location + Point.ADJACENT[1]
            Direction.West -> location + Point.ADJACENT[2]
            Direction.North -> location + Point.ADJACENT[3]
        }
        return Beam(location, direction)
    }

    return when (space) {
        '.' -> listOf(move())

        '\\' -> {
            when (direction) {
                Direction.North -> listOf(move(direction = Direction.West))
                Direction.East -> listOf(move(direction = Direction.South))
                Direction.South -> listOf(move(direction = Direction.East))
                Direction.West -> listOf(move(direction = Direction.North))
            }
        }

        '/' -> {
            when (direction) {
                Direction.North -> listOf(move(direction = Direction.East))
                Direction.East -> listOf(move(direction = Direction.North))
                Direction.South -> listOf(move(direction = Direction.West))
                Direction.West -> listOf(move(direction = Direction.South))
            }
        }

        '-' -> {
            if (direction == Direction.North || direction == Direction.South) {
                listOf(move(direction = Direction.East), move(direction = Direction.West))
            } else {
                listOf(move())
            }
        }

        '|' -> {
            if (direction == Direction.East || direction == Direction.West) {
                listOf(move(direction = Direction.South), move(direction = Direction.North))
            } else {
                listOf(move())
            }
        }

        else -> error("!")
    }
}
