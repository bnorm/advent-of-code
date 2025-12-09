package utils

tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

fun lcm(a: Long, b: Long) = a * b / gcd(a, b)

infix fun LongRange.intersects(other: LongRange): Boolean =
    this.first in other || this.last in other || other.first in this || other.last in this

infix fun IntRange.intersects(other: IntRange): Boolean =
    this.first in other || this.last in other || other.first in this || other.last in this
