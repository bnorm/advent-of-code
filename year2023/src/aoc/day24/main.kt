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
    require(stones.size >= 5) // Need at least 5 stones to solve with math.

    val p1 = stones[0].p
    val v1 = stones[0].v
    val p2 = stones[1].p
    val v2 = stones[1].v
    val p3 = stones[2].p
    val v3 = stones[2].v
    val p4 = stones[3].p
    val v4 = stones[3].v
    val p5 = stones[4].p
    val v5 = stones[4].v

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

    // Add stones 3, 4, and 5 to complete system of equations.
    // p0x * (v1y - v2y) + p0y * (v2x - v1x) + v0x * (p2y - p1y) + v0y * (p1x - p2x)  =  [(p1x * v1y - p1y * v1x) - (p2x * v2y - p2y * v2x)]
    // p0x * (v1y - v3y) + p0y * (v3x - v1x) + v0x * (p3y - p1y) + v0y * (p1x - p3x)  =  [(p1x * v1y - p1y * v1x) - (p3x * v3y - p3y * v3x)]
    // p0x * (v1y - v4y) + p0y * (v4x - v1x) + v0x * (p4y - p1y) + v0y * (p1x - p4x)  =  [(p1x * v1y - p1y * v1x) - (p4x * v4y - p4y * v4x)]
    // p0x * (v1y - v5y) + p0y * (v5x - v1x) + v0x * (p5y - p1y) + v0y * (p1x - p5x)  =  [(p1x * v1y - p1y * v1x) - (p5x * v5y - p5y * v5x)]

    val m = Matrix(
        arrayOf(
            arrayOf(v1.y - v2.y, v2.x - v1.x, p2.y - p1.y, p1.x - p2.x),
            arrayOf(v1.y - v3.y, v3.x - v1.x, p3.y - p1.y, p1.x - p3.x),
            arrayOf(v1.y - v4.y, v4.x - v1.x, p4.y - p1.y, p1.x - p4.x),
            arrayOf(v1.y - v5.y, v5.x - v1.x, p5.y - p1.y, p1.x - p5.x),
        )
    )

    val b = Vector(
        (p1.x * v1.y - p1.y * v1.x) - (p2.x * v2.y - p2.y * v2.x),
        (p1.x * v1.y - p1.y * v1.x) - (p3.x * v3.y - p3.y * v3.x),
        (p1.x * v1.y - p1.y * v1.x) - (p4.x * v4.y - p4.y * v4.x),
        (p1.x * v1.y - p1.y * v1.x) - (p5.x * v5.y - p5.y * v5.x),
    )

    solve(m, b)
    val (p0x, p0y, v0x, v0y) = b

    // Solve for z-axis with stones 1 and 2.

    // t = (p1x - p0x) / (v0x - v1x)
    // s = (p2x - p0x) / (v0x - v2x)

    val t = if (v0x != v1.x) (p1.x - p0x) / (v0x - v1.x) else (p1.y - p0y) / (v0y - v1.y)
    val s = if (v0x != v2.x) (p2.x - p0x) / (v0x - v2.x) else (p2.y - p0y) / (v0y - v2.y)

    // p0z + t * v0z = p1z + t * v1z = ptz
    // p0z + s * v0z = p2z + s * v2z = psz

    // p0z + t * v0z = ptz
    // v0z = (ptz - p0z) / t

    // p0z + s * v0z = psz
    // p0z + s * (ptz - p0z) / t = psz
    // p0z + s * ptz / t - s * p0z / t = psz
    // p0z - s * p0z / t = psz - s * ptz / t
    // t * p0z - s * p0z = t * psz - s * ptz
    // p0z = (t * psz - s * ptz) / (t - s)

    val ptz = p1.z + t * v1.z
    val psz = p2.z + s * v2.z
    val p0z = (t * psz - s * ptz) / (t - s)

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
