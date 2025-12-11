package aoc.math

class Matrix<T>(
    private val values: Array<Array<T>>,
) {
    init {
        val rowSizes = values.map { it.size }
        require(rowSizes.isNotEmpty()) { "Must have at least 1 row." }
        require(rowSizes.toSet().size == 1) { "All rows must have the same length." }
    }

    val rows: IntRange = values.indices
    val columns: IntRange = values[0].indices

    fun contains(r: Int, c: Int): Boolean = r in rows && c in columns
    private fun checkBounds(r: Int, c: Int): Unit = require(contains(r, c)) { "($r, $c) !in ($columns, $rows)" }

    operator fun get(r: Int, c: Int): T {
        checkBounds(r, c)
        return values[r][c]
    }

    operator fun set(r: Int, c: Int, value: T) {
        checkBounds(r, c)
        values[r][c] = value
    }

    fun row(r: Int): Vector<T> {
        checkBounds(r, 0)
        return RowVector(r, this)
    }

    fun column(c: Int): Vector<T> {
        checkBounds(0, c)
        return ColumnVector(c, this)
    }

    override fun toString(): String {
        return buildString {
            for (r in rows) {
                if (r == 0) append("[[") else append(" [")

                for (c in columns) {
                    if (c != 0) append(", ")
                    append(this@Matrix[r, c])
                }

                if (r == rows.last) append("]]") else appendLine("]")
            }
        }
    }
}

private class RowVector<T>(
    private val r: Int,
    private val matrix: Matrix<T>,
) : AbstractVector<T>() {
    override val size: Int = matrix.columns.last + 1
    override fun get(i: Int) = matrix[r, i]
    override fun set(i: Int, value: T) = matrix.set(r, i, value)
}

private class ColumnVector<T>(
    private val c: Int,
    private val matrix: Matrix<T>,
) : AbstractVector<T>() {
    override val size: Int = matrix.rows.last + 1
    override fun get(i: Int) = matrix[i, c]
    override fun set(i: Int, value: T) = matrix.set(i, c, value)
}
