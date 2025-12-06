@file:Suppress("PackageDirectoryMismatch")

package aoc.day06

import aoc.input.downloadInput
import utils.grid2d.*

const val SAMPLE1 = """
123 328  51 64 
 45 64  387 23 
  6 98  215 314
*   +   *   +  
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2025, 6)

    val part1 = part1(SAMPLE1)
    require(part1 == "4277556") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "3263827") { part2 }
    println(part2(input))
}

private fun part1(input: String): String {
    val lines = input.trim().lines()
    val numbers = Grid(
        lines.subList(0, lines.size - 1)
            .map { line -> line.trim().split("\\s+".toRegex()).map { it.toLong() } }
    )
    val operators = lines.last().split("\\s+".toRegex())

    var total = 0L
    for (x in numbers.xSpan) {
        val column = numbers.column(x)
        total += when (operators[x]) {
            "+" -> column.reduce { a, b -> a + b }
            "*" -> column.reduce { a, b -> a * b }
            else -> error("!")
        }
    }

    return total.toString()
}

private fun part2(input: String): String {
    // Need to preserve line padding.
    val lines = input.split("\n").filter { it.isNotBlank() }
    val grid = Grid(lines.map { it.toCharArray().toList() })

    var lastOp = ' '
    var subTotal = 0L
    var grandTotal = 0L
    for (x in grid.xSpan) {
        val column = grid.column(x)
        val op = column.last()
        val digits = column.subSpan(0, column.size - 1).filter { it != ' ' }
        if (digits.isEmpty()) continue

        val number = digits.joinToString("").toLong()
        if (op != ' ') {
            grandTotal += subTotal
            subTotal = number
            lastOp = op
        } else {
            when (lastOp) {
                '+' -> subTotal += number
                '*' -> subTotal *= number
                else -> error("unknown op ($lastOp)")
            }
        }
    }

    grandTotal += subTotal
    return grandTotal.toString()
}
