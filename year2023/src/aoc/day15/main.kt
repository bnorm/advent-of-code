package aoc.day15

import utils.*

fun main() {
    val input = readInput("aoc/day15/input.txt")
    val sample1 = readInput("aoc/day15/sample1.txt")
    val sample2 = readInput("aoc/day15/sample2.txt")

    val part1 = part1(sample1)
    require(part1 == "1320") { part1 }
    println(part1(input))

    val part2 = part2(sample2)
    require(part2 == "145") { part2 }
    println(part2(input))
}

fun part1(input: List<String>): String {
    return input[0].split(",").sumOf { hash(it) }.toString()
}

fun part2(input: List<String>): String {
    val steps = input[0].split(",").map { it.split("[-=]".toRegex()) }
    val boxes = List(256) { LinkedHashMap<String, Int>() }

    for (step in steps) {
        val label = step[0]
        if (step[1].isEmpty()) {
            boxes[hash(label)].remove(label)
        } else {
            boxes[hash(label)][label] = step[1].toInt()
        }
    }

    var result = 0
    for (b in boxes.indices) {
        val box = boxes[b]
        for ((slot, entry) in box.entries.withIndex()) {
            result += (b + 1) * (slot + 1) * entry.value
        }
    }

    return result.toString()
}

fun hash(s: String): Int {
    var result = 0
    for (c in s) result = ((result + c.code) * 17) % 256
    return result
}
