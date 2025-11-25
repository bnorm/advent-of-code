package utils.space

interface Space<T> {
    val xSpan: LongRange
    val ySpan: LongRange
    val zSpan: LongRange

    fun contains(x: Long, y: Long, z: Long): Boolean = x in xSpan && y in ySpan && z in zSpan

    operator fun get(x: Long, y: Long, z: Long): T

    fun xyPlane(z: Long): Plane<T>
}

class MutableSpace<T>(
    private val values: Array<Array<Array<T>>>,
) : Space<T> {
    init {
        val ySizes = values.map { y -> y.size }
        val xSizes = values.flatMap { y -> y.map { x -> x.size } }
        require(ySizes.toSet().size == 1 && xSizes.toSet().size == 1) { "inconsistent" }
    }

    override val xSpan: LongRange = values[0][0].indices.toLongRange()
    override val ySpan: LongRange = values[0].indices.toLongRange()
    override val zSpan: LongRange = values.indices.toLongRange()

    private fun checkBounds(x: Long, y: Long, z: Long): Unit =
        require(contains(x, y, z)) { "($x, $y, $z) !in ($xSpan, $ySpan, $zSpan)" }

    override fun get(x: Long, y: Long, z: Long): T {
        checkBounds(x, y, z)
        return values[z.toInt()][y.toInt()][x.toInt()]
    }

    operator fun set(x: Long, y: Long, z: Long, value: T) {
        checkBounds(x, y, z)
        values[z.toInt()][y.toInt()][x.toInt()] = value
    }

    override fun xyPlane(z: Long): MutablePlane<T> {
        return MutablePlane(this, z)
    }
}

private fun IntRange.toLongRange(): LongRange {
    return start.toLong()..endInclusive.toLong()
}
