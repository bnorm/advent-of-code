package aoc.day03

import utils.*

fun main() {
    val input = readInput("aoc/day03/input.txt")
    val sample1 = readInput("aoc/day03/sample1.txt")
    val sample2 = readInput("aoc/day03/sample2.txt")

    require(part1(sample1) == "4361")
    println(part1(input))

    require(part2(sample2) == "467835")
    println(part2(input))
}

fun part1(input: List<String>): String {
    val engine = Engine(input.map { it.toList() })

    val partNumbers = mutableSetOf<PartNumber>()
    for (r in 0..<engine.rows) {
        for (c in 0..<engine.columns) {
            if (engine[r, c] !in Engine.NON_SYMBOLS) {
                partNumbers += engine.getAdjacent(r, c)
            }
        }
    }

    return partNumbers.sumOf { it.value }.toString()
}

fun part2(input: List<String>): String {
    val engine = Engine(input.map { it.toList() })

    var gearRatios = 0
    for (r in 0..<engine.rows) {
        for (c in 0..<engine.columns) {
            if (engine[r, c] == Engine.GEAR) {
                val gears = engine.getAdjacent(r, c).toList()
                if (gears.size == 2) {
                    gearRatios += gears[0].value * gears[1].value
                }
            }
        }
    }

    return gearRatios.toString()
}

class Engine(
    val schematic: List<List<Char>>
) {
    init {
        require(schematic.isNotEmpty())
        val rowLengths = schematic.map { it.size }.distinct()
        require(rowLengths.size == 1 && rowLengths[0] != 0)
    }

    val rows = schematic.size
    val columns = schematic[0].size

    private fun checkInBounds(r: Int, c: Int): Boolean = r in 0..<rows && c in 0..<columns

    private fun requireInBounds(r: Int, c: Int) {
        if (!checkInBounds(r, c)) throw IndexOutOfBoundsException("$r !in 0..<$rows && $c !in 0..<$columns")
    }

    operator fun get(r: Int, c: Int): Char {
        requireInBounds(r, c)
        return schematic[r][c]
    }

    fun getAdjacent(r: Int, c: Int): Set<PartNumber> {
        requireInBounds(r, c)
        val numbers = mutableSetOf<PartNumber>()

        numbers += getPartNumber(r - 1, c - 1)
        numbers += getPartNumber(r - 1, c)
        numbers += getPartNumber(r - 1, c + 1)

        numbers += getPartNumber(r, c - 1)
        // Center not included
        numbers += getPartNumber(r, c + 1)

        numbers += getPartNumber(r + 1, c - 1)
        numbers += getPartNumber(r + 1, c)
        numbers += getPartNumber(r + 1, c + 1)

        return numbers
    }

    private fun getPartNumber(r: Int, c: Int): PartNumber? {
        if (!checkInBounds(r, c)) return null

        val row = schematic[r]
        if (row[c] !in DIGITS) return null

        var start = c
        while (start > 0 && row[start - 1] in DIGITS) start--

        var end = c
        while (end < columns - 1 && row[end + 1] in DIGITS) end++

        var value = 0
        for (i in start..end) {
            value *= 10
            value += row[i].digitToInt()
        }

        return PartNumber(r, start, value)
    }

    companion object {
        val DIGITS = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        val NON_SYMBOLS = setOf('.') + DIGITS
        val GEAR = '*'
    }
}

data class PartNumber(
    val r: Int,
    val c: Int,
    val value: Int,
)
