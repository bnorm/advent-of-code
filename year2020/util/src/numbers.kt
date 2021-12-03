import java.math.BigInteger

val Long.bg: BigInteger
    get() = BigInteger.valueOf(this)

fun lcm(n1: Long, n2: Long): Long {
    return (n1 * n2) / gcf(n1, n2)
}

tailrec fun gcf(n1: Long, n2: Long): Long {
    // Euclidean Algorithm
    return when {
        n1 == 0L -> n2
        n2 == 0L -> n1
        // If n1 < n2, this will swap the values
        else -> gcf(n2, n1 % n2)
    }
}

data class Euclid(
    val gcd: Long,
    val b1: Long,
    val b2: Long
)

fun euclid(n1: Long, n2: Long): Euclid {
    /*
        // Extended Euclidean algorithm
        gcd(n1, n2) = b1 * n1 + b2 * n2

        function extended_gcd(n1, n2)
            (old_r, r) := (n1, n2)
            (old_s, s) := (1, 0)
            (old_t, t) := (0, 1)

            while r ≠ 0 do
                quotient := old_r div r
                (old_r, r) := (r, old_r − quotient × r)
                (old_s, s) := (s, old_s − quotient × s)
                (old_t, t) := (t, old_t − quotient × t)

            output "Bézout coefficients:", (old_s, old_t)
            output "greatest common divisor:", old_r
            output "quotients by the gcd:", (t, s)
     */

    var r = n2
    var oldR = n1
    var s = 0L
    var oldS = 1L
    var t = 1L
    var oldT = 0L

    while (r != 0L) {
        val quotient = oldR / r

        val tmpR = r
        r = oldR - quotient * r
        oldR = tmpR

        val tmpS = s
        s = oldS - quotient * s
        oldS = tmpS

        val tmpT = t
        t = oldT - quotient * t
        oldT = tmpT
    }

//    println("Bézout coefficients: ($oldS, $oldT)")
//    println("greatest common divisor: $oldR")
//    println("quotients by the gcd: ($t, $s)")
    return Euclid(oldR, oldS, oldT)
}
