package aoc.day21

import utils.*
import utils.grid2d.*

fun main() {
    val input = readInput("aoc/day21/input.txt")
    val sample1 = readInput("aoc/day21/sample1.txt")

    val part1 = part1(sample1, 6)
    require(part1 == "16") { part1 }
    println(part1(input, 64))

    println(part2(input, 26501365))
}

fun part1(input: List<String>, steps: Int): String {
    val grid = Grid(input.map { it.toList() }.reversed())
    val start = grid.findAll { it == 'S' }.single()
    val visited = walk(grid, start, steps)
    return visited.steps(steps).toString()
}

fun part2(input: List<String>, steps: Int): String {
    val grid = Grid(input.map { it.toList() }.reversed())
    val start = grid.findAll { it == 'S' }.single()

    // Square grid.
    require(grid.xSpan.last == grid.ySpan.last)
    val size = grid.xSpan.last + 1

    // Manual inspection of real input reveals direct paths in each cardinal direction.
    // TODO test assertion.

    // Manual inspection of the input number of steps reveals that it brings use up directly
    // to the edge of the grid. Require this assertion to hold for the input.
    require((steps - (size / 2)) % size == 0) { "Solution assumptions invalid" }

    // Build saturated grids starting from the center, corners, and center of each edge.
    val center = walk(grid, start, size)
    val north = walk(grid, Point(start.x, 0), size)
    val northEast = walk(grid, Point(0, 0), 3 * size / 2)
    val east = walk(grid, Point(0, start.y), size)
    val southEast = walk(grid, Point(0, grid.ySpan.last), 3 * size / 2)
    val south = walk(grid, Point(start.x, grid.ySpan.last), size)
    val southWest = walk(grid, Point(grid.xSpan.last, grid.ySpan.last), 3 * size / 2)
    val west = walk(grid, Point(grid.xSpan.last, start.y), size)
    val northWest = walk(grid, Point(grid.xSpan.last, 0), 3 * size / 2)

//    display(grid, center, steps)
//    display(grid, center, steps - 1)
//    display(grid, north, steps - 1, limit = size)
//    display(grid, northEast, steps - 1, limit = (size / 2))
//    display(grid, northEast, steps, limit = (size / 2) + size)
//    display(grid, east, steps - 1, limit = size)
//    display(grid, southEast, steps - 1, limit = (size / 2))
//    display(grid, southEast, steps, limit = (size / 2) + size)
//    display(grid, south, steps - 1, limit = size)
//    display(grid, southWest, steps - 1, limit = (size / 2))
//    display(grid, southWest, steps, limit = (size / 2) + size)
//    display(grid, west, steps - 1, limit = size)
//    display(grid, northWest, steps - 1, limit = (size / 2))
//    display(grid, northWest, steps, limit = (size / 2) + size)

    //      .^.     length = 1 -> # =  1
    //      <#>  1                . =  4
    //      .v.          <, ^, >, v =  4

    //      .^.
    //     ./#\.  1  length = 2 -> # =  5
    //     <###>  3                . =  8
    //     .\#/.  1             /, \ =  4
    //      .v.           <, ^, >, v =  4

    //      .^.
    //     ./#\.   1
    //    ./###\.  3  length = 3 -> # = 13
    //    <#####>  5                . = 12
    //    .\###/.  3             /, \ =  8
    //     .\#/.   1       <, ^, >, v =  4
    //      .v.

    //      .^.
    //     ./#\.    1
    //    ./###\.   3
    //   ./#####\.  5  length = 4 -> # = 25  = length ^ 2 + (length - 1) ^ 2
    //   <#######>  7                . = 16  = 4 * length
    //   .\#####/.  5             /, \ = 12  = 4 * (length - 1)
    //    .\###/.   3       <, ^, >, v =  4  = 4
    //     .\#/.    1
    //      .v.

    val length = (steps / size).toLong()

    val count = 0L +
            (length - 1) * (length - 1) * center.steps(steps) +
            length * length * center.steps(steps - 1) +
            north.steps(steps - 1, limit = size) +
            length * northEast.steps(steps - 1, limit = (size / 2)) +
            (length - 1) * northEast.steps(steps, limit = (size / 2) + size) +
            east.steps(steps - 1, limit = size) +
            length * southEast.steps(steps - 1, limit = (size / 2)) +
            (length - 1) * southEast.steps(steps, limit = (size / 2) + size) +
            south.steps(steps - 1, limit = size) +
            length * southWest.steps(steps - 1, limit = (size / 2)) +
            (length - 1) * southWest.steps(steps, limit = (size / 2) + size) +
            west.steps(steps - 1, limit = size) +
            length * northWest.steps(steps - 1, limit = (size / 2)) +
            (length - 1) * northWest.steps(steps, limit = (size / 2) + size)

    return count.toString()
}

private fun Map<Point, Int>.steps(steps: Int, limit: Int = Int.MAX_VALUE) =
    count { (_, step) -> step <= limit && step % 2 == steps % 2 }.toLong()

private fun walk(grid: Grid<Char>, start: Point, steps: Int): Map<Point, Int> {
    val visited = mutableMapOf<Point, Int>()
    val queue = mutableSetOf<Point>()
    queue.add(start)

    for (step in 0..steps) {
        val batch = queue.toList().also { queue.clear() }
        if (batch.isEmpty()) break // Saturated
        for (next in batch) {
            if (next !in visited) {
                visited[next] = step
                for (adjacent in grid.adjacent(next)) {
                    if (grid[adjacent] != '#') queue.add(adjacent)
                }
            }
        }
    }

    return visited
}

private fun display(grid: Grid<Char>, visited: Map<Point, Int>, steps: Int, limit: Int = Int.MAX_VALUE) {
    for (y in grid.ySpan.reversed()) {
        for (x in grid.xSpan) {
            val p = visited[Point(x, y)]
            if (p != null && p < limit && p % 2 == steps % 2) {
                print('O')
            } else {
                print(grid[x, y])
            }
        }
        println()
    }
    println()
}
