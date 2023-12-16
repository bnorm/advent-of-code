package utils.grid2d

fun <T> Grid<T>.flip(vertically: Boolean = false, horizontally: Boolean = false): Grid<T> {
    if (!vertically && !horizontally) return this
    return when (this) {
        is FlippedGrid -> {
            val combinedVertically = vertically xor this.vertically
            val combinedHorizontally = horizontally xor this.horizontally

            // Unwrap a flipped Grid which is no longer flipped in either direction.
            if (!combinedVertically && !combinedHorizontally) this.delegate

            FlippedGrid(this, combinedVertically, combinedHorizontally)
        }

        else -> FlippedGrid(this, vertically, horizontally)
    }
}

private class FlippedGrid<T>(
    val delegate: Grid<T>,
    val vertically: Boolean,
    val horizontally: Boolean,
) : Grid<T> {
    override val xSpan: IntRange get() = delegate.xSpan
    override val ySpan: IntRange get() = delegate.ySpan

    private fun delegateX(x: Int): Int {
        return if (vertically) delegate.xSpan.last - x else x
    }

    private fun delegateY(y: Int): Int {
        return if (horizontally) delegate.ySpan.last - y else y
    }

    override fun get(x: Int, y: Int): T = delegate.get(delegateX(x), delegateY(y))
    override fun ref(x: Int, y: Int): Grid.Location<T> = delegate.ref(delegateX(x), delegateY(y))

    override fun row(y: Int): Grid.Span<T> =
        delegate.row(delegateY(y)).let { if (vertically) it.asReverse() else it }

    override fun column(x: Int): Grid.Span<T> =
        delegate.column(delegateX(x)).let { if (horizontally) it.asReverse() else it }
}
