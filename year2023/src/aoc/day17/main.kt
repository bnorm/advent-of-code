package aoc.day17

import utils.*
import utils.grid2d.*
import java.util.*

fun main() {
    val input = readInput("aoc/day17/input.txt")
    val sample1 = readInput("aoc/day17/sample1.txt")
    val sample2 = readInput("aoc/day17/sample2.txt")

    val part1 = part1(sample1)
    require(part1 == "102") { part1 }
    println(part1(input))

    val part2 = part2(sample2)
    require(part2 == "94") { part2 }
    println(part2(input))
}

fun part1(input: List<String>): String {
    val grid = Grid(input.map { row -> row.map { it.digitToInt() } }.reversed())
    val start = Point(0, grid.ySpan.last)
    val end = Point(grid.xSpan.last, 0)

    fun Path.move(direction: Direction = this.direction): Path? {
        if (straight >= 3 && direction == this.direction) return null

        val location = location.move(direction)
        if (location !in grid) return null

        return Path(
            location = location,
            direction = direction,
            straight = if (direction != this.direction) 1 else straight + 1,
        )
    }

    val path = findPath(grid, start, end) {
        listOfNotNull(
            it.move(it.direction.turnLeft()),
            it.move(),
            it.move(it.direction.turnRight()),
        )
    }

    return path.cost.toString()
}

fun part2(input: List<String>): String {
    val grid = Grid(input.map { row -> row.map { it.digitToInt() } }.reversed())
    val start = Point(0, grid.ySpan.last)
    val end = Point(grid.xSpan.last, 0)

    fun Path.move(direction: Direction = this.direction): Path? {
        if (straight >= 10 && direction == this.direction) return null
        if (straight < 4 && direction != this.direction) return null

        val location = location.move(direction)
        if (location !in grid) return null

        return Path(
            location = location,
            direction = direction,
            straight = if (direction != this.direction) 1 else straight + 1,
        )
    }

    val path = findPath(
        grid, start, end,
        predicate = { it.location == end && it.straight >= 4 },
    ) {
        listOfNotNull(
            it.move(it.direction.turnLeft()),
            it.move(),
            it.move(it.direction.turnRight()),
        )
    }

    return path.cost.toString()
}

private fun findPath(
    grid: Grid<Int>,
    start: Point,
    end: Point,
    predicate: (Path) -> Boolean = { it.location == end },
    neighbors: (Path) -> List<Path>,
): Search<Path> {
    fun dist(point: Point): Long {
        return manhattan(point, end)
    }

    fun Search<Path>.next(path: Path): Search<Path> {
        return Search(
            current = path,
            cost = cost + grid[path.location],
            dist = dist(path.location),
        )
    }

    val heap: Queue<Search<Path>> = PriorityQueue()
    heap.add(Search(Path(start, Direction.East), 0, dist(start)))
    heap.add(Search(Path(start, Direction.South), 0, dist(start)))

    val visited = mutableMapOf<Path, Long>()
    for (search in heap) {
        visited[search.current] = search.cost + search.dist
    }

    while (heap.isNotEmpty()) {
        val search = heap.poll()!!
        if (predicate(search.current)) return search

        for (neighbor in neighbors(search.current).map { search.next(it) }) {
            val h = neighbor.cost + neighbor.dist
            if (visited.compute(neighbor.current) { _, old -> minOf(old ?: Long.MAX_VALUE, h) } != h) continue
            heap.removeIf { it.current == neighbor.current }
            heap.add(neighbor)
        }
    }

    error("empty!")
}

data class Path(
    val location: Point,
    val direction: Direction,
    val straight: Int = 0,
)

data class Search<T>(
    val current: T,
    val cost: Long,
    val dist: Long,
) : Comparable<Search<T>> {
    override fun compareTo(other: Search<T>): Int {
        return compareValues(cost + dist, other.cost + other.dist)
    }
}
