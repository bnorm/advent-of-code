package aoc.day23

import utils.*
import utils.grid2d.*
import java.util.PriorityQueue

fun main() {
    val input = readInput("aoc/day23/input.txt")
    val sample1 = readInput("aoc/day23/sample1.txt")
    val sample2 = readInput("aoc/day23/sample2.txt")

    val part1 = part1(sample1)
    require(part1 == "94") { part1 }
    println(part1(input))

    val part2 = part2(sample2)
    require(part2 == "154") { part2 }
    println(part2(input))
}

fun part1(input: List<String>): String {
    val map = Grid(input.map { it.toList() }.reversed())
    val start = Point(map.row(map.ySpan.last).indexOf('.'), map.ySpan.last)
    val end = Point(map.row(0).indexOf('.'), 0)
    val nodes = buildGraph(map, start, end)

    val length = findLongestPath(nodes, start, end)
    return length.toString()
}

fun part2(input: List<String>): String {
    val map = Grid(input.map { it.toList() }.reversed())
    val start = Point(map.row(map.ySpan.last).indexOf('.'), map.ySpan.last)
    val end = Point(map.row(0).indexOf('.'), 0)
    val nodes = buildGraph(map, start, end)

    for (node in nodes.values) {
        for ((neighbor, length) in node.neighbors) {
            neighbor.neighbors[node] = length // Make all paths bidirectional.
        }
    }

    val length = findLongestPath(nodes, start, end)
    return length.toString()
}

// TODO dynamic programming to make this faster?
private fun findLongestPath(nodes: Map<Point, Node>, start: Point, end: Point): Long {
    require(start in nodes && end in nodes)

    val startNode = nodes.getValue(start)
    val startPath = Path(setOf(startNode), startNode, 0)

    val queue = PriorityQueue<Path>()
    queue.add(startPath)

    var max = -1L
    while (queue.isNotEmpty()) {
        val path = queue.poll()!!
        for ((neighbor, length) in path.node.neighbors) {
            if (neighbor in path.visited) continue

            val nextLength = path.length + length
            if (neighbor.point == end) {
                max = maxOf(max, nextLength)
            } else {
                queue.add(Path(path.visited + neighbor, neighbor, nextLength))
            }
        }
    }

    return max
}

fun buildGraph(map: Grid<Char>, start: Point, end: Point): Map<Point, Node> {
    val nodes = mutableMapOf<Point, Node>()
    nodes[start] = Node(start)
    nodes[end] = Node(end)

    for (p in map.points) {
        if (map[p] == '#') continue
        val count = map.adjacent(p).filter { map[it] != '#' }.count()
        if (count > 2) {
            nodes[p] = Node(p)
        }
    }

    fun collectNeighbors(node: Node) {
        val queue = ArrayDeque<Pair<Point, Long>>()
        queue.add(node.point to 0)

        val visited = mutableSetOf<Point>()
        while (queue.isNotEmpty()) {
            val (point, length) = queue.removeFirst()
            visited.add(point)

            val adjacent = when (map[point]) {
                '>' -> sequenceOf(point + Point.ADJACENT[0])
                'v' -> sequenceOf(point + Point.ADJACENT[1])
                '<' -> sequenceOf(point + Point.ADJACENT[2])
                '^' -> sequenceOf(point + Point.ADJACENT[3])
                '.' -> map.adjacent(point).filter { map[it] != '#' && it !in visited }
                else -> error("!")
            }

            for (p in adjacent) {
                val neighbor = nodes[p]
                if (neighbor != null) {
                    node.neighbors[neighbor] = length + 1
                } else {
                    queue.add(p to length + 1)
                }
            }
        }
    }

    for (node in nodes.values) {
        collectNeighbors(node)
    }

    return nodes
}

class Node(
    val point: Point,
    val neighbors: MutableMap<Node, Long> = mutableMapOf(),
)

data class Path(
    val visited: Set<Node>,
    val node: Node,
    val length: Long,
) : Comparable<Path> {
    override fun compareTo(other: Path): Int {
        return compareValues(length, other.length)
    }
}