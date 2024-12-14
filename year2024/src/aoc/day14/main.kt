package aoc.day14

import aoc.input.downloadInput
import utils.grid2d.Point
import utils.grid2d.times
import java.util.*

const val SAMPLE1 = """
p=0,4 v=3,-3
p=6,3 v=-1,-3
p=10,3 v=-1,2
p=2,0 v=2,-1
p=0,0 v=1,3
p=3,0 v=-2,-2
p=7,6 v=-1,-3
p=3,0 v=-1,-2
p=9,3 v=2,3
p=7,3 v=-1,2
p=2,4 v=2,-3
p=9,5 v=-3,-3
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 14)

    val part1 = part1(SAMPLE1, Point(11, 7))
    require(part1 == "12") { part1 }
    println(part1(input, Point(101, 103)))
    println(part2(input))
}

class Robot(
    val start: Point,
    val velocity: Point,
)

fun parseRobots(input: String): List<Robot> {
    return input.trim().lines().map { line ->
        val (startStr, velocityStr) = line.split(" ")
        val start = startStr.substringAfter('=').split(',').map(String::toInt).let { (x, y) -> Point(x, y) }
        val velocity = velocityStr.substringAfter('=').split(',').map(String::toInt).let { (x, y) -> Point(x, y) }
        Robot(start, velocity)
    }
}

private fun part1(input: String, size: Point): String {
    val robots = parseRobots(input)
    val locations = iterate(robots, size, 100)
    return calculateSafety(locations, size).toString()
}

private fun part2(input: String): String {
    val size = Point(101, 103)

    val robots = parseRobots(input)
    var i = 0
    while (true) {
        val locations = iterate(robots, size, i)
        val pointsYX = locations
            .groupByTo(TreeMap()) { (_, y) -> y }
            .mapValues { (_, value) -> value.mapTo(TreeSet()) { (x, _) -> x } }

        if (
            pointsYX.values.any { row ->
                row.any { x ->
                    // Arbitrary guess that 10 points will line up horizontally...
                    for (d in 1..<10) {
                        if (x + d !in row) return@any false
                    }
                    return@any true
                }
            }
        ) {
            return i.toString()
        }
        i++

        if (i > 1_000_000) break // Give up...
    }

    error("!")
}

private fun iterate(robots: List<Robot>, size: Point, time: Int): List<Point> {
    return robots
        .map { it.start + time * it.velocity }
        .map { (x, y) ->
            Point(
                x = (x % size.x).let { if (it < 0) it + size.x else it },
                y = (y % size.y).let { if (it < 0) it + size.y else it },
            )
        }
}

private fun calculateSafety(locations: List<Point>, size: Point): Long {
    val xMiddle = size.x / 2
    val yMiddle = size.y / 2

    var safety = 1L
    safety *= locations.count { (x, y) -> x < xMiddle && y < yMiddle }
    safety *= locations.count { (x, y) -> x > xMiddle && y < yMiddle }
    safety *= locations.count { (x, y) -> x < xMiddle && y > yMiddle }
    safety *= locations.count { (x, y) -> x > xMiddle && y > yMiddle }
    return safety
}
