package day04

import utils.*
import kotlin.math.pow

fun main() {
    val sample1 = readInput("day04.sample1.txt")
    val sample2 = readInput("day04.sample2.txt")
    val input = readInput("day04.txt")

    require(part1(sample1) == "13")
    println(part1(input))

    require(part2(sample2) == "30")
    println(part2(input))
}

fun part1(input: List<String>): String {
    return input.map { Card.parse(it) }
        .sumOf { it.worth }
        .toString()
}

fun part2(input: List<String>): String {
    val cards = input.map { Card.parse(it) }
    val cardCount = cards.associateTo(mutableMapOf()) { it.id to 1L }
    for (card in cards) {
        for (i in card.id + 1..card.id + card.winnerCount) {
            cardCount[i] = cardCount.getValue(i) + cardCount.getValue(card.id)
        }
    }
    return cardCount.values.sum().toString()
}

data class Card(
    val id: Int,
    val winning: Set<Int>,
    val numbers: Set<Int>
) {
    val winnerCount: Int
        get() = numbers.count { it in winning }

    val worth: Int
        get() = 2.0.pow(winnerCount - 1).toInt()

    companion object {
        fun parse(line: String): Card {
            val (id, winning, numbers) = line.split(":", "|")
            return Card(
                id = id.trim().split("\\s+".toRegex())[1].toInt(),
                winning = winning.trim().split("\\s+".toRegex()).map { it.toInt() }.toSet(),
                numbers = numbers.trim().split("\\s+".toRegex()).map { it.toInt() }.toSet(),
            )
        }
    }
}