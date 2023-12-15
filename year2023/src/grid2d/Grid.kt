package grid2d


inline fun <reified T> Grid(rows: List<List<T>>): Grid<T> = MutableGrid(rows).toImmutable()

inline fun <reified T> MutableGrid(rows: List<List<T>>): MutableGrid<T> =
    MutableGrid(Array(rows.size) { rows[it].toTypedArray() })

fun <T> Grid<T>.toImmutable(): Grid<T> {
    class ImmutableGrid(delegate: Grid<T>) : Grid<T> by delegate
    return if (this is ImmutableGrid) this else ImmutableGrid(this)
}

interface Grid<out T> {
    val xSpan: IntRange
    val ySpan: IntRange

    fun contains(x: Int, y: Int): Boolean = x in xSpan && y in ySpan

    operator fun get(x: Int, y: Int): T
    fun ref(x: Int, y: Int): Location<T>

    fun row(y: Int): Span<T>
    fun column(x: Int): Span<T>

    interface Location<out T> {
        val value: T
    }

    interface Span<out T> : Iterable<T> {
        val size: Int
        operator fun get(n: Int): T
        fun ref(n: Int): Location<T>
        override fun iterator(): Iterator<T> = spanIterator(0)
        fun spanIterator(n: Int = 0): SpanIterator<T>
    }

    interface SpanIterator<out T> : Iterator<T> {
        fun hasPrevious(): Boolean
        fun previous(): T
    }
}

class MutableGrid<T>(
    private val rows: Array<Array<T>>,
) : Grid<T> {
    init {
        val rowSizes = rows.map { it.size }
        require(rowSizes.isNotEmpty()) { "Must have at least 1 row." }
        require(rowSizes.toSet().size == 1) { "All rows must have the same length." }
    }

    override val xSpan: IntRange = rows[0].indices
    override val ySpan: IntRange = rows.indices
    private fun checkBounds(x: Int, y: Int): Unit = require(contains(x, y)) { "($x, $y) !in ($xSpan, $ySpan)" }

    override fun get(x: Int, y: Int): T {
        checkBounds(x, y)
        return rows[y][x]
    }

    operator fun set(x: Int, y: Int, value: T) {
        checkBounds(x, y)
        rows[y][x] = value
    }

    override fun ref(x: Int, y: Int): Location<T> {
        checkBounds(x, y)
        return object : Location<T> {
            override var value: T
                get() = rows[y][x]
                set(value) {
                    rows[y][x] = value
                }
        }
    }

    override fun row(y: Int): Span<T> {
        checkBounds(0, y)
        return RowSpan(y, this)
    }

    override fun column(x: Int): Span<T> {
        checkBounds(x, 0)
        return ColumnSpan(x, this)
    }

    interface Location<T> : Grid.Location<T> {
        override var value: T
    }

    interface Span<T> : Grid.Span<T> {
        operator fun set(n: Int, value: T)
        override fun ref(n: Int): Location<T>
    }

    private class RowSpan<T>(
        private val y: Int,
        private val grid: MutableGrid<T>,
    ) : Span<T> {
        override val size: Int = grid.xSpan.last + 1
        override fun get(n: Int) = grid[n, y]
        override fun set(n: Int, value: T) = grid.set(n, y, value)
        override fun ref(n: Int): Location<T> = grid.ref(n, y)
        override fun spanIterator(n: Int): Grid.SpanIterator<T> = SpanIterator(n, this)
    }

    private class ColumnSpan<T>(
        private val x: Int,
        private val grid: MutableGrid<T>,
    ) : Span<T> {
        override val size: Int = grid.ySpan.last + 1
        override fun get(n: Int) = grid[x, n]
        override fun set(n: Int, value: T) = grid.set(x, n, value)
        override fun ref(n: Int): Location<T> = grid.ref(x, n)
        override fun spanIterator(n: Int): Grid.SpanIterator<T> = SpanIterator(n, this)
    }

    private class SpanIterator<T>(
        private var n: Int = 0,
        private val span: Span<T>
    ) : Grid.SpanIterator<T> {
        override fun hasNext(): Boolean = n < span.size
        override fun next(): T = span[n++]
        override fun hasPrevious(): Boolean = n > 0
        override fun previous(): T = span[--n]
    }
}
