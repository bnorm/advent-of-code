@file:Suppress("PackageDirectoryMismatch")

package aoc.year2025.day06

import aoc.run
import utils.grid2d.*

const val SAMPLE1 = """
123 328  51 64 
 45 64  387 23 
  6 98  215 314
*   +   *   +  
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() = run(
    part1 = ::part1, sample1 = SAMPLE1, expected1 = "4277556",
    part2 = ::part2, sample2 = SAMPLE2, expected2 = "3263827",
)

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
