package aoc.day16

import aoc.input.downloadInput
import utils.grid2d.*
import java.util.*

const val SAMPLE1 = """
###############
#.......#....E#
#.#.###.#.###.#
#.....#.#...#.#
#.###.#####.#.#
#.#.#.......#.#
#.#.#####.###.#
#...........#.#
###.#.#####.#.#
#...#.....#.#.#
#.#.#.###.#.#.#
#.....#...#.#.#
#.###.#.#.#.#.#
#S..#.....#...#
###############
"""

const val SAMPLE1_BIGGER = """
#################
#...#...#...#..E#
#.#.#.#.#.#.#.#.#
#.#.#.#...#...#.#
#.#.#.#.###.#.#.#
#...#.#.#.....#.#
#.#.#.#.#.#####.#
#.#...#.#.#.....#
#.#.#####.#.###.#
#.#.#.......#...#
#.#.###.#####.###
#.#.#...#.....#.#
#.#.#.#####.###.#
#.#.#.........#.#
#.#.#.#########.#
#S#.............#
#################
"""

const val SAMPLE2 = SAMPLE1
const val SAMPLE2_BIGGER = SAMPLE1_BIGGER

suspend fun main() {
    val input = downloadInput(2024, 16)

    val part1 = part1(SAMPLE1)
    require(part1 == "7036") { part1 }
    val part1Bigger = part1(SAMPLE1_BIGGER)
    require(part1Bigger == "11048") { part1Bigger }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "45") { part2 }
    val part2Bigger = part2(SAMPLE2_BIGGER)
    require(part2Bigger == "64") { part2Bigger }
    println(part2(input))
}

data class Reindeer(
    val location: Point,
    val direction: Direction,
)

private fun part1(input: String): String {
    val maze = Grid(input.trim().lines().asReversed().map { it.toList().map { if (it == '#') '#' else '.' } })
    val reindeer = Reindeer(location = Point(1, 1), direction = Direction.East)
    val exit = Point(maze.xSpan.last - 1, maze.ySpan.last - 1)

    val (cost, _) = search(maze, reindeer, exit)
    return cost.toString()
}

private fun part2(input: String): String {
    val maze = Grid(input.trim().lines().asReversed().map { it.toList().map { if (it == '#') '#' else '.' } })
    val reindeer = Reindeer(location = Point(1, 1), direction = Direction.East)
    val exit = Point(maze.xSpan.last - 1, maze.ySpan.last - 1)

    val (_, locations) = search(maze, reindeer, exit)
    return locations.size.toString()
}

fun search(maze: Grid<Char>, start: Reindeer, goal: Point): Pair<Long, Set<Point>> {
    data class SearchNode(
        val reindeer: Reindeer,
        val path: Set<Point>,
        val cost: Long,
    ) : Comparable<SearchNode> {
        override fun compareTo(other: SearchNode): Int =
            compareValues(cost, other.cost)
    }

    fun SearchNode.move(): SearchNode? {
        val location = reindeer.location.move(reindeer.direction)
        if (maze[location] == '#') return null

        val reindeer = Reindeer(location, reindeer.direction)
        return SearchNode(reindeer, path + location, cost + 1)
    }

    fun SearchNode.turnLeft(): SearchNode {
        val reindeer = Reindeer(reindeer.location, reindeer.direction.turnLeft())
        return SearchNode(reindeer, path, cost + 1000)
    }

    fun SearchNode.turnRight(): SearchNode {
        val reindeer = Reindeer(reindeer.location, reindeer.direction.turnRight())
        return SearchNode(reindeer, path, cost + 1000)
    }

    val visited = mutableMapOf<Reindeer, SearchNode>()
    val queue = PriorityQueue<SearchNode>()
    queue.add(SearchNode(start, setOf(start.location), 0L))
    while (queue.isNotEmpty()) {
        val current = queue.poll()
        if (current.reindeer.location == goal) return current.cost to current.path

        for (next in listOfNotNull(current.move(), current.turnLeft(), current.turnRight())) {
            val existing = visited[next.reindeer]
            if (existing != null) {
                if (next > existing) continue

                queue.remove(existing)

                if (next.cost == existing.cost) {
                    val merged = next.copy(path = next.path + existing.path)
                    visited[next.reindeer] = merged
                    queue.add(merged)
                    continue
                }
            }

            visited[next.reindeer] = next
            queue.add(next)
        }
    }

    error("!")
}
