package day06

import utils.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {
    val sample1 = readInput("day06.sample1.txt")
    val sample2 = readInput("day06.sample2.txt")
    val input = readInput("day06.txt")

    require(part1(sample1) == "288")
    println(part1(input))

    require(part2(sample2) == "71503")
    println(part2(input))
}

fun part1(input: List<String>): String {
    val races = Race.parse(input)

    var result = 1L
    for (race in races) {
        val winRange = race.winRange
        result *= (winRange.last - winRange.first + 1)
    }

    return result.toString()
}

fun part2(input: List<String>): String {
    val race = Race.parseSingle(input)
    val winRange = race.winRange
    return (winRange.last - winRange.first + 1).toString()
}

data class Race(
    val time: Long,
    val distance: Long,
) {
    val winRange: LongRange = run {
        /*
        Given 'x' is the amount of time holding the button down, the win condition is as follows:
            distance / x <= time - x
            distance / x - time + x <= 0
            (distance - time * x + x^2) / x <= 0
            (distance - time * x + x^2) / x == 0 (to find the min and max time)
            x^2 - time * x + distance == 0, where x != 0
            x = (time +- sqrt(time^2 - 4 * distance)) / 2, where x != 0 (via quadratic formula)
         */

        val min = (time - sqrt((time * time - 4 * distance).toDouble())) / 2
        val max = (time + sqrt((time * time - 4 * distance).toDouble())) / 2
        floor(min + 1).toLong()..ceil(max - 1).toLong()
    }

    companion object {
        fun parse(input: List<String>): List<Race> {
            val times = input[0].split("\\s+".toRegex()).drop(1)
            val distances = input[1].split("\\s+".toRegex()).drop(1)
            return times.zip(distances) { time, distance -> Race(time.toLong(), distance.toLong()) }
        }

        fun parseSingle(input: List<String>): Race {
            val time = input[0].split("\\s+".toRegex()).drop(1).joinToString("")
            val distance = input[1].split("\\s+".toRegex()).drop(1).joinToString("")
            return Race(time.toLong(), distance.toLong())
        }
    }
}
