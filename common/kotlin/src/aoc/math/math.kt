package aoc.math

import java.math.BigInteger

// Vector Math
private inline val <T> Vector<T>.x get() = this[0]
private inline val <T> Vector<T>.y get() = this[1]
private inline val <T> Vector<T>.z get() = this[2]

operator fun BigInteger.times(v: Vector<BigInteger>): Vector<BigInteger> = v.times(this)
operator fun Vector<BigInteger>.times(n: BigInteger): Vector<BigInteger> {
    return Vector(Array(size) { this[it] * n })
}

operator fun BigInteger.div(v: Vector<BigInteger>): Vector<BigInteger> = v.div(this)
operator fun Vector<BigInteger>.div(n: BigInteger): Vector<BigInteger> {
    return Vector(Array(size) { this[it] / n })
}

operator fun Vector<BigInteger>.plus(v: Vector<BigInteger>): Vector<BigInteger> {
    require(size == v.size)
    return Vector(Array(size) { i -> this[i] + v[i] })
}

operator fun Vector<BigInteger>.minus(v: Vector<BigInteger>): Vector<BigInteger> {
    require(size == v.size)
    return Vector(Array(size) { i -> this[i] - v[i] })
}

operator fun Vector<BigInteger>.div(v: Vector<BigInteger>): Vector<BigInteger> {
    require(size == v.size)
    return Vector(Array(size) { i -> this[i] / v[i] })
}

infix fun Vector<BigInteger>.cross(v: Vector<BigInteger>): Vector<BigInteger> {
    require(size == v.size)
    require(size == 3) // TODO can do we remove this?
    val px = this.y * v.z - this.z * v.y
    val py = this.z * v.x - this.x * v.z
    val pz = this.x * v.y - this.y * v.x
    return Vector(px, py, pz)
}

infix fun Vector<BigInteger>.dot(v: Vector<BigInteger>): BigInteger {
    require(size == v.size)
    var result = BigInteger.ZERO
    for (i in 0..<size) {
        result += this[i] * v[i]
    }
    return result
}

// Matrix Math

operator fun Matrix<BigInteger>.times(v: Vector<BigInteger>): Vector<BigInteger> {
    require(columns.last + 1 == v.size)
    return Vector(Array(rows.last + 1) { r -> row(r).dot(v) })
}
