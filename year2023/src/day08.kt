package day08

import utils.*

fun main() {
    val sample1 = readInput("day08.sample1.txt")
    val sample2 = readInput("day08.sample2.txt")
    val input = readInput("day08.txt")

    require(part1(sample1) == "2")
    println(part1(input))

    require(part2(sample2) == "6")
    println(part2(input))
}

fun part1(input: List<String>): String {
    val navigation = Navigation.parse(input)
    val steps = navigate(navigation, "AAA") { it == "ZZZ" }
    return steps.toString()
}

fun part2(input: List<String>): String {
    val navigation = Navigation.parse(input)
    val starts = navigation.turns.keys.filter { it.endsWith('A') }
    val steps = starts.map { start -> navigate(navigation, start) { it.endsWith('Z') } }
    val lcm = steps.reduceRight { a, b -> lcm(a, b) }
    return lcm.toString()
}

private fun navigate(navigation: Navigation, start: String, end: (String) -> Boolean): Long {
    var steps = 0L
    var location = start
    for (i in navigation.instructions) {
        steps += 1
        location = when (i) {
            'L' -> navigation.turns.getValue(location).first
            'R' -> navigation.turns.getValue(location).second
            else -> error(i)
        }
        if (end(location)) break
    }
    return steps
}

data class Navigation(
    val instructions: Iterable<Char>,
    val turns: Map<String, Pair<String, String>>,
) {
    companion object {
        fun parse(input: List<String>): Navigation {
            val instructions = input[0].toList()
            val infiniteInstructions = Iterable {
                iterator {
                    while (true) yieldAll(instructions)
                }
            }

            val turns = input.drop(2).associate {
                val (location, turns) = it.split(" = ")
                val (left, right) = turns.drop(1).dropLast(1).split(", ")
                location to (left to right)
            }

            return Navigation(infiniteInstructions, turns)
        }
    }
}
