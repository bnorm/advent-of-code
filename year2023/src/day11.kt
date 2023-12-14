package day11

import grid2d.*
import utils.*
import kotlin.math.abs

fun main() {
    val sample1 = readInput("day11.sample1.txt")
    val sample2 = readInput("day11.sample2.txt")
    val input = readInput("day11.txt")

    require(part1(sample1) == "374")
    println(part1(input))

    val part2 = part2(sample2, 100)
    require(part2 == "8410") { part2 }
    println(part2(input, 1_000_000))
}

fun part1(input: List<String>): String {
    val grid = Grid(input.map { it.toList() }.reversed().expand())
    val galaxies = grid.findAll { it == '#' }.toList()
    val pairs = galaxies.toPairs()
    return pairs.sumOf { manhattan(it.first, it.second) }.toString()
}

fun part2(input: List<String>, expansion: Long): String {
    val rows = input.map { it.toList() }.reversed()
    val grid = Grid(rows)
    val (xGaps, yGaps) = rows.gaps()
    val galaxies = grid.findAll { it == '#' }.toList()
    val pairs = galaxies.toPairs()
    return pairs.sumOf { pair ->
        val xDist = abs(pair.first.x.toLong() - pair.second.x) +
                (minOf(pair.first.x, pair.second.x)..maxOf(pair.first.x, pair.second.x))
                    .count { it in xGaps } * (expansion - 1)
        val yDist = abs(pair.first.y.toLong() - pair.second.y) +
                (minOf(pair.first.y, pair.second.y)..maxOf(pair.first.y, pair.second.y))
                    .count { it in yGaps } * (expansion - 1)
        return@sumOf xDist + yDist
    }.toString()
}

fun List<List<Char>>.gaps(): Pair<List<Int>, List<Int>> {
    val xGaps = mutableListOf<Int>()
    val yGaps = mutableListOf<Int>()

    for (y in this.indices) {
        if (this[y].all { it == '.' }) {
            yGaps.add(y)
        }
    }

    for (x in this[0].indices) {
        if (this.all { it[x] == '.' }) {
            xGaps.add(x)
        }
    }

    return xGaps to yGaps
}

fun List<List<Char>>.expand(): List<List<Char>> {
    val rows = mutableListOf<MutableList<Char>>()

    for (row in this) {
        rows.add(row.toMutableList())
        if (row.all { it == '.' }) {
            rows.add(row.toMutableList())
        }
    }

    for (c in this[0].indices.reversed()) {
        if (rows.all { it[c] == '.' }) {
            for (row in rows) {
                row.add(c, '.')
            }
        }
    }

    return rows
}
