package aoc.day25

import utils.*

fun main() {
    val input = readInput("aoc/day25/input.txt")
    val sample1 = readInput("aoc/day25/sample1.txt")

    val part1 = part1(sample1)
    require(part1 == "54") { part1 }
    println(part1(input))
}

fun part1(input: List<String>): String {
    val components = parse(input)

    repeat(3) {
        // Flood from all nodes and count how many times each edge is used.
        val edgeCount = mutableMapOf<Set<String>, Int>()
        for (component in components.keys) {
            flood(components, component, edgeCount)
        }

        // Remove the most used edge from the graph.
        val (first, second) = edgeCount.entries.sortedBy { -it.value }[0].key.toList()
        components.getValue(first).remove(second)
        components.getValue(second).remove(first)
    }

    // After removing the top 3 most-used edges from the graph, pick a random node and count reachable nodes.
    val edgeCount = mutableMapOf<Set<String>, Int>()
    flood(components, components.keys.random(), edgeCount)
    val firstCount = edgeCount.keys.flatMapTo(mutableSetOf()) { it }.count()
    val secondCount = components.size - firstCount
    return (firstCount * secondCount).toString()
}

private fun parse(input: List<String>): Map<String, MutableSet<String>> {
    val components = mutableMapOf<String, MutableSet<String>>()
    for (line in input) {
        val (name, connections) = line.split(": ")
        val component = components.getOrPut(name) { mutableSetOf(name) }
        for (c in connections.split(" ")) {
            val connection = components.getOrPut(c) { mutableSetOf(c) }
            component.add(c)
            connection.add(name)
        }
    }
    return components
}

private fun flood(components: Map<String, MutableSet<String>>, component: String, edges: MutableMap<Set<String>, Int>) {
    val visited = mutableSetOf(component)
    val queue = ArrayDeque<String>()

    visited.add(component)
    queue.add(component)

    while (queue.isNotEmpty()) {
        val next = queue.removeFirst()
        for (connection in components.getValue(next)) {
            if (!visited.add(connection)) continue

            edges.compute(setOf(next, connection)) { _, value ->
                if (value == null) 1
                else value + 1
            }
            queue.add(connection)
        }
    }
}
