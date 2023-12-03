package day02

import utils.*

fun main() {
    val sample1 = readInput("day02.sample1.txt")
    val sample2 = readInput("day02.sample2.txt")
    val input = readInput("day02.txt")

    require(part1(sample1, CubeSet(red = 12, green = 13, blue = 14)) == "8")
    println(part1(input, CubeSet(red = 12, green = 13, blue = 14)))

    require(part2(sample2) == "2286")
    println(part2(input))
}

private fun part1(input: List<String>, bag: CubeSet): String {
    return input.map { Game.parse(it) }
        .filter { it.possibleWith(bag) }
        .sumOf { it.id }
        .toString()
}

private fun part2(input: List<String>): String {
    return input.map { Game.parse(it) }
        .sumOf { it.minimum().power }
        .toString()
}

private data class CubeSet(
    val red: Int,
    val green: Int,
    val blue: Int,
) {
    val power get() = red * green * blue

    companion object {
        val Empty = CubeSet(0, 0, 0)
    }
}

private data class Game(
    val id: Int,
    val handfuls: List<CubeSet>,
) {
    fun possibleWith(bag: CubeSet): Boolean =
        handfuls.all { bag.red >= it.red && bag.green >= it.green && bag.blue >= it.blue }

    fun minimum(): CubeSet {
        var bag = CubeSet.Empty
        for (handful in handfuls) {
            bag = bag.copy(
                red = maxOf(bag.red, handful.red),
                green = maxOf(bag.green, handful.green),
                blue = maxOf(bag.blue, handful.blue),
            )
        }
        return bag
    }

    companion object {
        private val GAME_PATTERN = "Game (?<game>\\d+): (?<draws>.*)".toRegex()

        fun parse(line: String): Game {
            val (id, handfuls) = GAME_PATTERN.matchEntire(line)!!.destructured
            return Game(id.toInt(), handfuls.split(";").map { it.toCubeSet() })
        }

        private fun String.toCubeSet(): CubeSet {
            var hand = CubeSet.Empty

            for (pair in trim().split(",")) {
                val (count, color) = pair.trim().split(" ")
                when (color) {
                    "red" -> hand = hand.copy(red = hand.red + count.toInt())
                    "green" -> hand = hand.copy(green = hand.green + count.toInt())
                    "blue" -> hand = hand.copy(blue = hand.blue + count.toInt())
                }
            }

            return hand
        }
    }
}
