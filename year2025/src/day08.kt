@file:Suppress("PackageDirectoryMismatch")

package aoc.day08

import aoc.input.downloadInput
import java.util.*

const val SAMPLE1 = """
162,817,812
57,618,57
906,360,560
592,479,940
352,342,300
466,668,158
542,29,236
431,825,988
739,650,466
52,470,668
216,146,977
819,987,18
117,168,530
805,96,715
346,949,466
970,615,88
941,993,340
862,61,35
984,92,344
425,690,689
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2025, 8)

    val part1 = part1(SAMPLE1, 10)
    require(part1 == "40") { part1 }
    println(part1(input, 1000))

    val part2 = part2(SAMPLE2)
    require(part2 == "25272") { part2 }
    println(part2(input))
}

private fun part1(input: String, count: Int): String {
    val boxes = input.trim().lines().map { line ->
        val (x, y, z) = line.split(",").map { it.toLong() }
        JunctionBox(x, y, z)
    }

    // O(N^2 * log(count))
    val queue = PriorityQueue<Connection>(count + 1) { a, b -> -compareValues(a, b) }
    for (i1 in boxes.indices) {
        for (i2 in i1 + 1..boxes.lastIndex) {
            val c = Connection(boxes[i1], boxes[i2])
            if (queue.size < count) {
                queue.add(c)
            } else if (queue.peek() > c) {
                queue.poll()
                queue.add(c)
            }
        }
    }

    // O(count)
    val circuitsByBox = boxes.associateWithTo(mutableMapOf()) { Circuit(setOf(it)) }
    for (connection in queue) {
        val c1 = circuitsByBox.getValue(connection.box1)
        val c2 = circuitsByBox.getValue(connection.box2)
        if (c1 !== c2) {
            val c3 = c1 + c2
            for (box in c3.boxes) {
                circuitsByBox[box] = c3
            }
        }
    }

    // (max) O(N * log(N))
    val circuits = circuitsByBox.values.toSet().sortedByDescending { it.boxes.size }
    val (c1, c2, c3) = circuits
    return (c1.boxes.size * c2.boxes.size * c3.boxes.size).toString()
}

private fun part2(input: String): String {
    val boxes = input.trim().lines().map { line ->
        val (x, y, z) = line.split(",").map { it.toLong() }
        JunctionBox(x, y, z)
    }

    // O(N^2)
    val connections = mutableListOf<Connection>()
    for (i1 in boxes.indices) {
        for (i2 in i1 + 1..boxes.lastIndex) {
            connections.add(Connection(boxes[i1], boxes[i2]))
        }
    }

    // O(N^2 * log(N^2))
    connections.sort()

    // O(N^2)
    fun findLastConnection(): Connection {
        val circuitsByBox = boxes.associateWithTo(mutableMapOf()) { Circuit(setOf(it)) }
        for (connection in connections) {
            val c1 = circuitsByBox.getValue(connection.box1)
            val c2 = circuitsByBox.getValue(connection.box2)
            if (c1 !== c2) {
                val c3 = c1 + c2
                if (c3.boxes.size == boxes.size) return connection
                for (box in c3.boxes) {
                    circuitsByBox[box] = c3
                }
            }
        }
        error("!")
    }

    val lastConnection = findLastConnection()
    return (lastConnection.box1.x * lastConnection.box2.x).toString()
}

private class JunctionBox(val x: Long, val y: Long, val z: Long) {
    fun distSq(other: JunctionBox): Long {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return dx * dx + dy * dy + dz * dz
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }
}

private class Connection(val box1: JunctionBox, val box2: JunctionBox) : Comparable<Connection> {
    private val distSq = box1.distSq(box2)
    override fun compareTo(other: Connection): Int = compareValues(distSq, other.distSq)
}

private class Circuit(val boxes: Set<JunctionBox>) {
    operator fun plus(other: Circuit): Circuit = Circuit(boxes + other.boxes)

    override fun toString(): String {
        return "${boxes.size}"
    }
}
