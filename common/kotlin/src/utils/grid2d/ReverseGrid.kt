package utils.grid2d

fun <T> Grid<T>.reverse(xReverse: Boolean = false, yReverse: Boolean = false): Grid<T> {
    if (!xReverse && !yReverse) return this
    return when (this) {
        is ReverseGrid -> {
            val combinedVertically = xReverse xor this.xReverse
            val combinedHorizontally = yReverse xor this.yReverse

            // Unwrap when no longer flipped in either direction.
            if (!combinedVertically && !combinedHorizontally) return this.delegate
            ReverseGrid(this, combinedVertically, combinedHorizontally)
        }

        else -> ReverseGrid(this, xReverse, yReverse)
    }
}

private class ReverseGrid<T>(
    val delegate: Grid<T>,
    val xReverse: Boolean,
    val yReverse: Boolean,
) : Grid<T> {
    override val xSpan: IntRange get() = delegate.xSpan
    override val ySpan: IntRange get() = delegate.ySpan

    private fun delegateX(x: Int): Int {
        return if (xReverse) delegate.xSpan.last - x else x
    }

    private fun delegateY(y: Int): Int {
        return if (yReverse) delegate.ySpan.last - y else y
    }

    override fun get(x: Int, y: Int): T = delegate.get(delegateX(x), delegateY(y))
    override fun ref(x: Int, y: Int): Grid.Location<T> = delegate.ref(delegateX(x), delegateY(y))

    override fun row(y: Int): Grid.Span<T> =
        delegate.row(delegateY(y)).let { if (xReverse) it.asReverse() else it }

    override fun column(x: Int): Grid.Span<T> =
        delegate.column(delegateX(x)).let { if (yReverse) it.asReverse() else it }
}

fun <T> MutableGrid<T>.reverse(xReverse: Boolean = false, yReverse: Boolean = false): MutableGrid<T> {
    if (!xReverse && !yReverse) return this
    return when (this) {
        is ReverseMutableGrid -> {
            val combinedXReverse = xReverse xor this.xReverse
            val combinedYReverse = yReverse xor this.yReverse

            // Unwrap when no longer reversed in either direction.
            if (!combinedXReverse && !combinedYReverse) return this.delegate

            ReverseMutableGrid(this, combinedXReverse, combinedYReverse)
        }

        else -> ReverseMutableGrid(this, xReverse, yReverse)
    }
}

private class ReverseMutableGrid<T>(
    val delegate: MutableGrid<T>,
    val xReverse: Boolean,
    val yReverse: Boolean,
) : MutableGrid<T> {
    override val xSpan: IntRange get() = delegate.xSpan
    override val ySpan: IntRange get() = delegate.ySpan

    private fun delegateX(x: Int): Int = if (xReverse) delegate.xSpan.last - x else x
    private fun delegateY(y: Int): Int = if (yReverse) delegate.ySpan.last - y else y

    override fun get(x: Int, y: Int): T = delegate[delegateX(x), delegateY(y)]
    override fun set(x: Int, y: Int, value: T) = delegate.set(delegateX(x), delegateY(y), value)
    override fun ref(x: Int, y: Int): MutableGrid.MutableLocation<T> = delegate.ref(delegateX(x), delegateY(y))

    override fun row(y: Int): MutableGrid.MutableSpan<T> =
        delegate.row(delegateY(y)).let { if (xReverse) it.asReverse() else it }

    override fun column(x: Int): MutableGrid.MutableSpan<T> =
        delegate.column(delegateX(x)).let { if (yReverse) it.asReverse() else it }
}
