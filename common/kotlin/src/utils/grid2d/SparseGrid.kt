package utils.grid2d

class SparseGrid<T>(
    private val delegate: MutableGrid<T>,
    xSpan: List<Long>,
    ySpan: List<Long>,
) {
    val xSpan: List<Long> = xSpan.sorted()
    val ySpan: List<Long> = ySpan.sorted()

    init {
        require(xSpan.size == delegate.xSpan.last + 1)
        require(ySpan.size == delegate.ySpan.last + 1)
    }

    operator fun get(x: Long, y: Long): T {
        val mappedX = xSpan.binarySearch(x)
        val mappedY = ySpan.binarySearch(y)
        require(mappedX >= 0 && mappedY >= 0) { "($mappedX, $mappedY) is not present in sparse grid." }
        return delegate[mappedX, mappedY]
    }

    operator fun set(x: Long, y: Long, value: T) {
        val mappedX = xSpan.binarySearch(x)
        val mappedY = ySpan.binarySearch(y)
        require(mappedX >= 0 && mappedY >= 0) { "($mappedX, $mappedY) is not present in sparse grid." }
        delegate[mappedX, mappedY] = value
    }
}
