package aoc.day10

import aoc.input.downloadInput
import utils.grid2d.*

const val SAMPLE1 = """
89010123
78121874
87430965
96549874
45678903
32019012
01329801
10456732
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 10)

    val part1 = part1(SAMPLE1)
    require(part1 == "36") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "81") { part2 }
    println(part2(input))
}

private fun part1(input: String): String {
    val topology = Grid(
        input.trim().lines().asReversed()
            .map { line -> line.toList().map { it.digitToInt() } }
    )

    fun score(head: Point): Int {
        val visited = mutableSetOf<Point>()
        val queue = ArrayDeque<Point>()
        queue.add(head)

        val peaks = mutableSetOf<Point>()
        while (queue.isNotEmpty()) {
            val p = queue.removeFirst()
            if (!visited.add(p)) continue
            if (topology[p] == 9) {
                peaks.add(p)
            } else {
                val height = topology[p] + 1
                topology.adjacent(p)
                    .filter { topology[it] == height }
                    .forEach { queue.add(it) }
            }
        }

        return peaks.size
    }

    var sum = 0
    for (p in topology.points) {
        if (topology[p] == 0) {
            sum += score(p)
        }
    }

    return sum.toString()
}

private fun part2(input: String): String {
    val topology = Grid(
        input.trim().lines().asReversed()
            .map { line -> line.toList().map { it.digitToInt() } }
    )

    fun score(head: Point): Int {
        val queue = ArrayDeque<Point>()
        queue.add(head)

        var score = 0
        while (queue.isNotEmpty()) {
            val p = queue.removeFirst()
            if (topology[p] == 9) {
                score++
            } else {
                val height = topology[p] + 1
                topology.adjacent(p)
                    .filter { topology[it] == height }
                    .forEach { queue.add(it) }
            }
        }

        return score
    }

    var sum = 0
    for (p in topology.points) {
        if (topology[p] == 0) {
            sum += score(p)
        }
    }

    return sum.toString()
}
