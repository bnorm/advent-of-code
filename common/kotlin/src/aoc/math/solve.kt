package aoc.math

import java.math.BigInteger

// Solve:  m x = b  for x
fun solve(m: Matrix<BigInteger>, b: Vector<BigInteger>) {
    require(m.rows.last == m.columns.last)
    require(m.rows.last + 1 == b.size)

    for (t in 0..<b.size) {
        for (r in m.rows) {
            if (r != t) {
                val gcd = m[t, t].gcd(m[r, t])
                val u = m[t, t] / gcd
                val v = m[r, t] / gcd

                b[r] = b[r] * u - b[t] * v
                for (c in m.columns) {
                    m[r, c] = m[r, c] * u - m[t, c] * v
                }
            }
        }
    }

    for (t in m.columns) {
        b[t] = b[t] / m[t, t]
    }
}
