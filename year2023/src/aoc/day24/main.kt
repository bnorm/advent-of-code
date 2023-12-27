package aoc.day24

import aoc.math.*
import utils.*
import java.math.BigInteger

fun main() {
    val input = readInput("aoc/day24/input.txt")
    val sample1 = readInput("aoc/day24/sample1.txt")
    val sample2 = readInput("aoc/day24/sample2.txt")

    val part1 = part1(sample1, 7, 21)
    require(part1 == "2") { part1 }
    println(part1(input, 200_000_000_000_000, 400_000_000_000_000))

    val part2 = part2(sample2)
    require(part2 == "47") { part2 }
    println(part2(input))
}

fun part1(input: List<String>, min: Long, max: Long): String {
    val stones = input.map { HailStone.parse(it) }

    var result = 0
    for ((a, b) in stones.toPairs()) {
        val intersects = a.intersects(b, min, max)

        if (intersects) result++
    }
    return result.toString()
}

fun part2(input: List<String>): String {
    val stones = input.map { HailStone.parse(it) }
    require(stones.size >= 3) // Need at least 3 stones to solve with math.

    val p1 = stones[0].p
    val v1 = stones[0].v
    val p2 = stones[1].p
    val v2 = stones[1].v
    val p3 = stones[2].p
    val v3 = stones[2].v

    // Help from: https://www.reddit.com/r/adventofcode/comments/18pnycy/comment/kf068o2/?utm_source=share&utm_medium=web2x&context=3

    // Intersection of stones 0 (unknown) and stone 1.
    // p0x + t * v0x = p1x + t * v1x
    // p0y + t * v0y = p1y + t * v1y

    // t = (p1x - p0x) / (v0x - v1x)
    // t = (p1y - p0y) / (v0y - v1y)

    // (p1x - p0x) / (v0x - v1x) = (p1y - p0y) / (v0y - v1y)
    // (p1y - p0y) * (v0x - v1x) = (v0y - v1y) * (p1x - p0x)
    // v0x * p1y - p1y * v1x - v0x * p0y + p0y * v1x  =  v0y * p1x - p0x * v0y - p1x * v1y + p0x * v1y
    // p0x * v0y - v0x * p0y  =  v0y * p1x - p1x * v1y + p0x * v1y - v0x * p1y + p1y * v1x - p0y * v1x

    // Add stone 2 to remove quadratic.
    // p0x * v0y - v0x * p0y  =  v0y * p1x - p1x * v1y + p0x * v1y - v0x * p1y + p1y * v1x - p0y * v1x
    // p0x * v0y - v0x * p0y  =  v0y * p2x - p2x * v2y + p0x * v2y - v0x * p2y + p2y * v2x - p0y * v2x

    // v0y * p1x - p1x * v1y + p0x * v1y - v0x * p1y + p1y * v1x - p0y * v1x - [v0y * p2x - p2x * v2y + p0x * v2y - v0x * p2y + p2y * v2x - p0y * v2x]  =  0
    // v0y * p1x - p1x * v1y + p0x * v1y - v0x * p1y + p1y * v1x - p0y * v1x - v0y * p2x + p2x * v2y - p0x * v2y + v0x * p2y - p2y * v2x + p0y * v2x  =  0
    // v0y * p1x - p1x * v1y + p0x * v1y - v0x * p1y + p1y * v1x - p0y * v1x - v0y * p2x + p2x * v2y - p0x * v2y + v0x * p2y - p2y * v2x + p0y * v2x  =  0
    // (p0x * v1y - p0x * v2y) + (p0y * v2x - p0y * v1x) + (v0x * p2y - v0x * p1y) + (v0y * p1x - v0y * p2x)  =  p1x * v1y - p1y * v1x - p2x * v2y + p2y * v2x
    // (p0x * v1y - p0x * v2y) + (p0y * v2x - p0y * v1x) + (v0x * p2y - v0x * p1y) + (v0y * p1x - v0y * p2x)  =  [(p1x * v1y - p1y * v1x) - (p2x * v2y - p2y * v2x)]
    // p0x * (v1y - v2y) + p0y * (v2x - v1x) + v0x * (p2y - p1y) + v0y * (p1x - p2x)  =  [(p1x * v1y - p1y * v1x) - (p2x * v2y - p2y * v2x)]

    // Add stone 3 and the z-axis to complete system of equations.
    // p0x * (v1y - v2y) + p0y * (v2x - v1x)                     + v0x * (p2y - p1y) + v0y * (p1x - p2x)                      =  [(p1x * v1y - p1y * v1x) - (p2x * v2y - p2y * v2x)]
    // p0x * (v1z - v2z)                     + p0z * (v2x - v1x) + v0x * (p2z - p1z)                     + v0z * (p1x - p2x)  =  [(p1x * v1z - p1z * v1x) - (p2x * v2z - p2z * v2x)]
    //                     p0y * (v1z - v2z) + p0z * (v2y - v1y) +                     v0y * (p2z - p1z) + v0z * (p1y - p2y)  =  [(p1y * v1z - p1z * v1y) - (p2y * v2z - p2z * v2y)]

    // p0x * (v1y - v3y) + p0y * (v3x - v1x)                     + v0x * (p3y - p1y) + v0y * (p1x - p3x)                      =  [(p1x * v1y - p1y * v1x) - (p3x * v3y - p3y * v3x)]
    // p0x * (v1z - v3z)                     + p0z * (v3x - v1x) + v0x * (p3z - p1z)                     + v0z * (p1x - p3x)  =  [(p1x * v1z - p1z * v1x) - (p3x * v3z - p3z * v3x)]
    //                     p0y * (v1z - v3z) + p0z * (v3y - v1y) +                     v0y * (p3z - p1z) + v0z * (p1y - p3y)  =  [(p1y * v1z - p1z * v1y) - (p3y * v3z - p3z * v3y)]

    val m = Matrix(
        arrayOf(
            arrayOf(v1.y - v2.y, v2.x - v1.x, BigInteger.ZERO, p2.y - p1.y, p1.x - p2.x, BigInteger.ZERO),
            arrayOf(v1.z - v2.z, BigInteger.ZERO, v2.x - v1.x, p2.z - p1.z, BigInteger.ZERO, p1.x - p2.x),
            arrayOf(BigInteger.ZERO, v1.z - v2.z, v2.y - v1.y, BigInteger.ZERO, p2.z - p1.z, p1.y - p2.y),
            arrayOf(v1.y - v3.y, v3.x - v1.x, BigInteger.ZERO, p3.y - p1.y, p1.x - p3.x, BigInteger.ZERO),
            arrayOf(v1.z - v3.z, BigInteger.ZERO, v3.x - v1.x, p3.z - p1.z, BigInteger.ZERO, p1.x - p3.x),
            arrayOf(BigInteger.ZERO, v1.z - v3.z, v3.y - v1.y, BigInteger.ZERO, p3.z - p1.z, p1.y - p3.y),
        )
    )

    val b = Vector(
        (p1.x * v1.y - p1.y * v1.x) - (p2.x * v2.y - p2.y * v2.x),
        (p1.x * v1.z - p1.z * v1.x) - (p2.x * v2.z - p2.z * v2.x),
        (p1.y * v1.z - p1.z * v1.y) - (p2.y * v2.z - p2.z * v2.y),
        (p1.x * v1.y - p1.y * v1.x) - (p3.x * v3.y - p3.y * v3.x),
        (p1.x * v1.z - p1.z * v1.x) - (p3.x * v3.z - p3.z * v3.x),
        (p1.y * v1.z - p1.z * v1.y) - (p3.y * v3.z - p3.z * v3.y),
    )

    solve(m, b)
    val (p0x, p0y, p0z) = b
    return (p0x + p0y + p0z).toString()
}

data class HailStone(
    val p: Point,
    val v: Point,
) {
    fun intersects(other: HailStone, min: Long, max: Long): Boolean {
        // r(t) = t * v + p
        // x = t * vx + px
        // y = t * vy + py

        val px1 = p.x.toDouble()
        val py1 = p.y.toDouble()
        val vx1 = v.x.toDouble()
        val vy1 = v.y.toDouble()

        val px2 = other.p.x.toDouble()
        val py2 = other.p.y.toDouble()
        val vx2 = other.v.x.toDouble()
        val vy2 = other.v.y.toDouble()

        // t * vy1 + py1 = s * vy2 + py2
        // t * vx1 + px1 = s * vx2 + px2
        // s = (t * vx1 + px1 - px2) / vx2
        // s = (t * vx1) / vx2 + (px1 - px2) / vx2
        // t * vy1 + py1 = [(t * vx1) / vx2 + (px1 - px2) / vx2] * vy2 + py2
        // t * vy1 - [(t * vx1) / vx2] * vy2 = py2 + [(px1 - px2) / vx2] * vy2 - py1
        // t * (vy1 - vx1 * vy2 / vx2) = py2 + (px1 - px2) * vy2 / vx2 - py1
        // t = (py2 + (px1 - px2) * vy2 / vx2 - py1) / (vy1 - vx1 * vy2 / vx2)

        val t = (py2 + (px1 - px2) * vy2 / vx2 - py1) / (vy1 - vx1 * vy2 / vx2)
        if (t < 0.0) return false // Intersected in the past.

        val s = (t * vx1 + px1 - px2) / vx2
        if (s < 0.0) return false // Intersected in the past.

        val x = t * vx1 + px1
        val y = t * vy1 + py1
        if (x !in min.toDouble()..max.toDouble() || y !in min.toDouble()..max.toDouble())
            return false // Intersected out of bounds.

        return true
    }

    override fun toString(): String = "$p @ $v"

    companion object {
        fun parse(line: String): HailStone {
            val (p, v) = line.split("@")
            val (px, py, pz) = p.trim().split(",")
            val (vx, vy, vz) = v.trim().split(",")
            return HailStone(
                p = Point(
                    x = px.trim().toLong().toBigInteger(),
                    y = py.trim().toLong().toBigInteger(),
                    z = pz.trim().toLong().toBigInteger(),
                ),
                v = Point(
                    x = vx.trim().toLong().toBigInteger(),
                    y = vy.trim().toLong().toBigInteger(),
                    z = vz.trim().toLong().toBigInteger(),
                )
            )
        }
    }
}

data class Point(val x: BigInteger, val y: BigInteger, val z: BigInteger) {
    override fun toString(): String = "$x, $y, $z"
}
