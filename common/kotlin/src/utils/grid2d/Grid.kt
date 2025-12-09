package utils.grid2d

fun Grid(input: String, reversed: Boolean = true): Grid<Char> {
    return MutableGrid(input, reversed).toImmutable()
}

fun MutableGrid(input: String, reversed: Boolean = true): MutableGrid<Char> {
    val lines = input.trim().lines()
    val grid = Array(lines.size) { y ->
        val line = if (reversed) lines[lines.size - y - 1] else lines[y]
        Array(line.length) { x -> line[x] }
    }
    return MutableGrid(grid)
}

inline fun <reified T> MutableGrid(xSpan: Int, ySpan: Int, factory: (x: Int, y: Int) -> T): MutableGrid<T> {
    val grid = Array(ySpan) { y ->
        Array(xSpan) { x -> factory(x, y) }
    }
    return MutableGrid(grid)
}

inline fun <reified T> Grid(rows: List<List<T>>): Grid<T> = MutableGrid(rows).toImmutable()

inline fun <reified T> MutableGrid(rows: List<List<T>>): MutableGrid<T> =
    MutableGrid(Array(rows.size) { rows[it].toTypedArray() })

private class ImmutableGrid<T>(delegate: Grid<T>) : Grid<T> by delegate
fun <T> Grid<T>.toImmutable(): Grid<T> {
    return if (this is ImmutableGrid) this else ImmutableGrid(this)
}

interface Grid<out T> {
    val xSpan: IntRange
    val ySpan: IntRange

    operator fun get(x: Int, y: Int): T
    fun ref(x: Int, y: Int): Location<T>

    fun row(y: Int): Span<T>
    fun column(x: Int): Span<T>

    interface Location<out T> {
        val value: T
    }

    interface Span<out T> : Iterable<T> {
        val size: Int
        fun contains(n: Int): Boolean = n >= 0 && n < size
        operator fun get(n: Int): T
        fun ref(n: Int): Location<T>
        fun subSpan(start: Int = 0, endExclusive: Int = size): Span<T>
        override fun iterator(): Iterator<T> = spanIterator(0)
        fun spanIterator(n: Int = 0): SpanIterator<T>
    }

    interface SpanIterator<out T> : Iterator<T> {
        fun hasPrevious(): Boolean
        fun previous(): T
    }
}

interface MutableGrid<T> : Grid<T> {
    operator fun set(x: Int, y: Int, value: T)
    override fun ref(x: Int, y: Int): MutableLocation<T>

    override fun row(y: Int): MutableSpan<T>
    override fun column(x: Int): MutableSpan<T>

    interface MutableLocation<T> : Grid.Location<T> {
        override var value: T
    }

    interface MutableSpan<T> : Grid.Span<T> {
        operator fun set(n: Int, value: T)
        override fun ref(n: Int): MutableLocation<T>
        override fun subSpan(start: Int, endExclusive: Int): MutableSpan<T>
    }

    // TODO mutable SpanIterator?
}

@PublishedApi
internal fun <T> MutableGrid(rows: Array<Array<T>>): MutableGrid<T> =
    RealMutableGrid(rows)

private class RealMutableGrid<T>(
    private val rows: Array<Array<T>>,
) : MutableGrid<T> {
    init {
        val rowSizes = rows.map { it.size }
        require(rowSizes.isNotEmpty()) { "Must have at least 1 row." }
        require(rowSizes.toSet().size == 1) { "All rows must have the same length." }
    }

    override val xSpan: IntRange = rows[0].indices
    override val ySpan: IntRange = rows.indices
    private fun checkBounds(x: Int, y: Int): Unit =
        require(x in xSpan && y in ySpan) { "($x, $y) !in ($xSpan, $ySpan)" }

    override fun get(x: Int, y: Int): T {
        checkBounds(x, y)
        return rows[y][x]
    }

    override fun set(x: Int, y: Int, value: T) {
        checkBounds(x, y)
        rows[y][x] = value
    }

    override fun ref(x: Int, y: Int): MutableGrid.MutableLocation<T> {
        checkBounds(x, y)
        return object : MutableGrid.MutableLocation<T> {
            override var value: T
                get() = rows[y][x]
                set(value) {
                    rows[y][x] = value
                }
        }
    }

    override fun row(y: Int): MutableGrid.MutableSpan<T> {
        checkBounds(0, y)
        return RowSpan(y, this, 0, xSpan.last + 1)
    }

    override fun column(x: Int): MutableGrid.MutableSpan<T> {
        checkBounds(x, 0)
        return ColumnSpan(x, this, 0, ySpan.last + 1)
    }
}

private abstract class AbstractSpan<T> : Grid.Span<T> {
    fun checkBounds(n: Int): Unit = require(contains(n)) { "$n !in ${0..<size}" }

    override fun spanIterator(n: Int): Grid.SpanIterator<T> = SpanIterator(n, this)

    override fun toString(): String {
        return joinToString(prefix = "Span[", postfix = "]")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Grid.Span<*>) return false
        if (size != other.size) return false
        for (n in 0..<size) {
            if (get(n) != other[n]) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 0
        for (n in 0..<size) {
            result = 31 * result + get(n).hashCode()
        }
        return result
    }
}

private class RowSpan<T>(
    private val y: Int,
    private val grid: MutableGrid<T>,
    private val offset: Int,
    override val size: Int,
) : AbstractSpan<T>(), MutableGrid.MutableSpan<T> {
    override fun get(n: Int): T {
        checkBounds(n)
        return grid[offset + n, y]
    }

    override fun set(n: Int, value: T) {
        checkBounds(n)
        grid[offset + n, y] = value
    }

    override fun ref(n: Int): MutableGrid.MutableLocation<T> {
        checkBounds(n)
        return grid.ref(offset + n, y)
    }

    override fun subSpan(start: Int, endExclusive: Int): MutableGrid.MutableSpan<T> {
        require(endExclusive > start) {}
        checkBounds(start)
        checkBounds(endExclusive - 1)
        return RowSpan(y, grid, start + offset, endExclusive - start)
    }
}

private class ColumnSpan<T>(
    private val x: Int,
    private val grid: MutableGrid<T>,
    private val offset: Int,
    override val size: Int,
) : AbstractSpan<T>(), MutableGrid.MutableSpan<T> {
    override fun get(n: Int): T {
        checkBounds(n)
        return grid[x, offset + n]
    }

    override fun set(n: Int, value: T) {
        checkBounds(n)
        grid[x, offset + n] = value
    }

    override fun ref(n: Int): MutableGrid.MutableLocation<T> {
        checkBounds(n)
        return grid.ref(x, offset + n)
    }

    override fun subSpan(start: Int, endExclusive: Int): MutableGrid.MutableSpan<T> {
        require(endExclusive > start) {}
        checkBounds(start)
        checkBounds(endExclusive - 1)
        return ColumnSpan(x, grid, start + offset, endExclusive - start)
    }
}

private class SpanIterator<T>(
    private var n: Int = 0,
    private val span: Grid.Span<T>
) : Grid.SpanIterator<T> {
    init {
        require(n >= 0 && n <= span.size) { "$n !in ${0..span.size}" }
    }

    override fun hasNext(): Boolean = n < span.size
    override fun next(): T = span[n++]
    override fun hasPrevious(): Boolean = n > 0
    override fun previous(): T = span[--n]
}

private class ReverseSpanIterator<T>(
    private val iter: Grid.SpanIterator<T>,
) : Grid.SpanIterator<T> {
    override fun hasPrevious(): Boolean = iter.hasNext()
    override fun previous(): T = iter.next()
    override fun hasNext(): Boolean = iter.hasPrevious()
    override fun next(): T = iter.previous()
}

private open class ReverseSpan<T>(
    open val delegate: Grid.Span<T>,
) : AbstractSpan<T>(), Grid.Span<T> {
    @Suppress("NOTHING_TO_INLINE")
    inline fun delegateN(n: Int): Int = delegate.size - n - 1

    override val size: Int get() = delegate.size
    override fun get(n: Int): T = delegate[delegateN(n)]
    override fun ref(n: Int): Grid.Location<T> = delegate.ref(delegateN(n))

    override fun subSpan(start: Int, endExclusive: Int): Grid.Span<T> {
        return ReverseSpan(delegate.subSpan(delegate.size - endExclusive, delegate.size - start))
    }

    override fun spanIterator(n: Int): ReverseSpanIterator<T> =
        ReverseSpanIterator(delegate.spanIterator(delegate.size - n))
}

private class MutableReverseSpan<T>(
    override val delegate: MutableGrid.MutableSpan<T>,
) : ReverseSpan<T>(delegate), MutableGrid.MutableSpan<T> {
    override fun set(n: Int, value: T) = delegate.set(delegateN(n), value)
    override fun ref(n: Int): MutableGrid.MutableLocation<T> = delegate.ref(delegateN(n))

    override fun subSpan(start: Int, endExclusive: Int): MutableGrid.MutableSpan<T> {
        return MutableReverseSpan(delegate.subSpan(delegate.size - endExclusive, delegate.size - start))
    }
}

fun <T> Grid.Span<T>.asReverse(): Grid.Span<T> =
    if (this is ReverseSpan) this.delegate else ReverseSpan(this)

fun <T> MutableGrid.MutableSpan<T>.asReverse(): MutableGrid.MutableSpan<T> =
    if (this is MutableReverseSpan) this.delegate else MutableReverseSpan(this)
