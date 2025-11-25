package utils.space

data class Cube(
    val xSpan: LongRange,
    val ySpan: LongRange,
    val zSpan: LongRange,
) {
    override fun toString(): String {
        return "${xSpan.first},${ySpan.first},${zSpan.first}~${xSpan.last},${ySpan.last},${zSpan.last}"
    }
}
