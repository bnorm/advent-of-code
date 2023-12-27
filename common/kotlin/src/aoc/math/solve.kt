package aoc.math

import java.math.BigInteger

// Solve:  m x = b  for x
fun solve(m: Matrix<BigInteger>, b: Vector<BigInteger>) {
    val size = b.size
    require(m.rows.last == m.columns.last)
    require(m.rows.last + 1 == size)

    // Calculate and divide by row GCD
    fun normalize(t: Int) {
        var gcd = b[t]
        for (c in m.columns) {
            gcd = gcd.gcd(m[t, c])
        }

        // Normalize sign to the diagonal value sign.
        if (m[t, t] < BigInteger.ZERO && gcd > BigInteger.ZERO) {
            gcd = gcd.negate()
        }

        if (gcd != BigInteger.ONE) {
            b[t] /= gcd
            for (c in m.columns) {
                m[t, c] /= gcd
            }
        }
    }

    // Check if a row swap is required because of a zero-value in current diagonal.
    fun checkSwap(t: Int) {
        if (m[t, t] == BigInteger.ZERO) {
            if (t + 1 == size) error("! unsolvable !")

            // Swap rows with a later, non-zero, row.
            for (r in t + 1..<size) {
                if (m[r, t] != BigInteger.ZERO) {
                    b.swap(r, t)
                    m.row(t).swap(m.row(r))
                    break
                }
            }
        }
    }

    // Subtract row 't' from row 'r' using GCD at column 't'.
    fun subtract(t: Int, r: Int) {
        val gcd = m[t, t].gcd(m[r, t])
        val u = m[t, t] / gcd
        val v = m[r, t] / gcd

        b[r] = b[r] * u - b[t] * v
        for (c in m.columns) {
            m[r, c] = m[r, c] * u - m[t, c] * v
        }
    }

    for (r in m.rows) normalize(r)

    for (t in 0..<size) {
        checkSwap(t)
        for (r in m.rows) {
            if (r != t) {
                subtract(t, r)
                normalize(r)
            }
        }
    }

    // TODO is this check required? exact factional value b[t] / m[t, t].
    for (t in 0..<size) {
        if (m[t, t] != BigInteger.ONE) error("! non-integer solution !")
    }
}

fun <T> Vector<T>.swap(a: Int, b: Int) {
    val tmp = this[a]
    this[a] = this[b]
    this[b] = tmp
}

fun <T> Vector<T>.swap(v: Vector<T>) {
    require(size == v.size)
    for (i in 0..<size) {
        val tmp = this[i]
        this[i] = v[i]
        v[i] = tmp
    }
}
