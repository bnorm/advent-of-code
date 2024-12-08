package utils.grid2d

/**
 * ```
 * y = m * x + b
 *
 * b = y - m * x
 * y2 - m * x2 = y1 - m * x1
 * m * x1 - m * x2 = y1 - y2
 * m = (y1 - y2) / (x1 - x2)
 *
 * m = (y - b) / x
 * (y1 - b) / x1 = (y2 - b) / x2
 * x2 * y1 - x2 * b = x1 * y2 - x1 * b
 * x1 * b - x2 * b = x1 * y2 - x2 * y1
 * b = (x1 * y2 - x2 * y1) / (x1 - x2)
 *
 * yn = (y1 - y2) / (x1 - x2) * xn + (x1 * y2 - x2 * y1) / (x1 - x2)
 * yn = [(y1 - y2) * xn + (x1 * y2 - x2 * y1)] / (x1 - x2)
 * ```
 *
 * @param mNumerator `y1 - y2`
 * @param bNumerator `x1 * y2 - x2 * y1`
 * @param denominator `x1 - x2`
 */
class Line private constructor(
    private val mNumerator: Int,
    private val bNumerator: Int,
    private val denominator: Int,
) {
    constructor(p1: Point, p2: Point) : this(
        mNumerator = p1.y - p2.y,
        bNumerator = p1.x * p2.y - p2.x * p1.y,
        denominator = p1.x - p2.x
    )

    constructor(m: Int, b: Int) : this(
        mNumerator = m,
        bNumerator = b,
        denominator = 1,
    )

    val isHorizontal: Boolean get() = mNumerator == 0
    val isVertical: Boolean get() = denominator == 0

    /**
     * Return a [Point] based on the provided [x] value,
     * only if the calculated `y` value would be an [Int]
     * and the line is not vertical.
     */
    operator fun invoke(x: Int): Point? {
        if (isVertical) return null

        val numerator = x * mNumerator + bNumerator
        return if (numerator % denominator != 0) null
        else Point(x, numerator / denominator)
    }
}
