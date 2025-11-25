package utils.space

interface Plane<T> {
    val xSpan: LongRange
    val ySpan: LongRange

    fun contains(x: Long, y: Long, z: Long): Boolean = x in xSpan && y in ySpan

    operator fun get(x: Long, y: Long): T
}

class MutablePlane<T>(
    private val space: MutableSpace<T>,
    private val z: Long,
) : Plane<T> {
    override val xSpan: LongRange get() = space.xSpan
    override val ySpan: LongRange get() = space.ySpan

    override fun get(x: Long, y: Long): T {
        return space[x, y, z]
    }

    operator fun set(x: Long, y: Long, value: T) {
        space[x, y, z] = value
    }
}