package grid2d

fun <T> Grid<T>.rotate(count: Int = 1, clockwise: Boolean = true): Grid<T> {
    var clockwiseCount = (if (clockwise) count else -count) % 4
    if (clockwiseCount < 0) clockwiseCount += 4
    else if (clockwiseCount == 0) return this // No rotation is actually being applied.

    return when (this) {
        is RotatedGrid -> {
            val combinedClockwiseCount = (this.clockwiseCount + clockwiseCount) % 4

            // Unwrap a rotated Grid which is no longer rotated.
            if (combinedClockwiseCount == 0) return this.delegate

            RotatedGrid(this.delegate, combinedClockwiseCount)
        }

        else -> RotatedGrid(this, clockwiseCount)
    }
}

private class RotatedGrid<T>(
    val delegate: Grid<T>,
    val clockwiseCount: Int,
) : Grid<T> {
    init {
        require(clockwiseCount in 1..3)
    }

    override val xSpan: IntRange = if (clockwiseCount % 2 == 0) delegate.xSpan else delegate.ySpan
    override val ySpan: IntRange = if (clockwiseCount % 2 == 0) delegate.ySpan else delegate.xSpan

    private fun delegateX(x: Int, y: Int): Int {
        return when (clockwiseCount) {
            1 -> delegate.xSpan.last - y
            2 -> delegate.xSpan.last - x
            3 -> y
            else -> error("!")
        }
    }

    private fun delegateY(x: Int, y: Int): Int {
        return when (clockwiseCount) {
            1 -> x
            2 -> delegate.ySpan.last - y
            3 -> delegate.ySpan.last - x
            else -> error("!")
        }
    }

    override fun get(x: Int, y: Int): T = delegate.get(delegateX(x, y), delegateY(x, y))
    override fun ref(x: Int, y: Int): Grid.Location<T> = delegate.ref(delegateX(x, y), delegateY(x, y))

    override fun row(y: Int): Grid.Span<T> {
        return when (clockwiseCount) {
            1 -> delegate.column(delegate.xSpan.last - y)
            2 -> delegate.row(delegate.ySpan.last - y).asReverse()
            3 -> delegate.column(y).asReverse()
            else -> error("!")
        }
    }

    override fun column(x: Int): Grid.Span<T> {
        return when (clockwiseCount) {
            1 -> delegate.row(x).asReverse()
            2 -> delegate.column(delegate.xSpan.last - x).asReverse()
            3 -> delegate.row(delegate.ySpan.last - x)
            else -> error("!")
        }
    }
}
