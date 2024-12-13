package aoc.day13

import aoc.input.downloadInput
import aoc.math.Matrix
import aoc.math.Vector
import aoc.math.solve
import java.math.BigInteger

const val SAMPLE1 = """
Button A: X+94, Y+34
Button B: X+22, Y+67
Prize: X=8400, Y=5400

Button A: X+26, Y+66
Button B: X+67, Y+21
Prize: X=12748, Y=12176

Button A: X+17, Y+86
Button B: X+84, Y+37
Prize: X=7870, Y=6450

Button A: X+69, Y+23
Button B: X+27, Y+71
Prize: X=18641, Y=10279
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 13)

    val part1 = part1(SAMPLE1)
    require(part1 == "480") { part1 }
    println(part1(input))
    println(part2(input))
}

private fun part1(input: String): String {
    val machines = input.toMachines()
    val tokens = machines.mapNotNull { it.solve() }
        .sumOf { (a, b) -> a * 3.toBigInteger() + b }
    return tokens.toString()
}

private fun part2(input: String): String {
    val machines = input.toMachines().map { machine ->
        machine.copy(
            prize = Vector(
                machine.prize[0] + 10000000000000.toBigInteger(),
                machine.prize[1] + 10000000000000.toBigInteger()
            )
        )
    }
    val tokens = machines.mapNotNull { it.solve() }
        .sumOf { (a, b) -> a * 3.toBigInteger() + b }
    return tokens.toString()
}

data class Machine(
    val a: Vector<BigInteger>,
    val b: Vector<BigInteger>,
    val prize: Vector<BigInteger>,
)

private fun String.toMachines(): List<Machine> {
    val regex = ".+?: X.(?<x>\\d+), Y.(?<y>\\d+)".toRegex()
    return trim().split("\n\n").map {
        val iter = it.lines().iterator()
        val a = regex.matchEntire(iter.next())!!.destructured
            .let { (x, y) -> Vector(x.toBigInteger(), y.toBigInteger()) }
        val b = regex.matchEntire(iter.next())!!.destructured
            .let { (x, y) -> Vector(x.toBigInteger(), y.toBigInteger()) }
        val prize = regex.matchEntire(iter.next())!!.destructured
            .let { (x, y) -> Vector(x.toBigInteger(), y.toBigInteger()) }
        Machine(a, b, prize)
    }
}

private fun Machine.solve(): Pair<BigInteger, BigInteger>? {
    val m = Matrix(
        arrayOf(
            arrayOf(a[0], b[0]),
            arrayOf(a[1], b[1]),
        )
    )
    val b = Vector(prize[0], prize[1])

    try {
        solve(m, b)
        return b[0] to b[1]
    } catch (_: Throwable) {
        // Matrix is unsolvable.
        return null
    }
}
