import kotlin.math.abs

data class Point(val x: Int, val y: Int, val z: Int, val w: Int) {
    companion object {
        val ZERO = Point(0, 0, 0, 0)
    }

    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y, z + other.z, w + other.w)
    }
}

fun main() {
    val sample = """
        .#.
        ..#
        ###
    """.trimIndent()

//    val input = sample.trim().splitToSequence("\n")
    val input = readResourceText("input.txt").trim().splitToSequence("\n")

    val active = input.flatMapIndexed { r, line ->
        line.mapIndexedNotNull { c, state ->
            if (state == '#') Point(c, r, 0, 0)
            else null
        }
    }.toSet()

    run {
        var iter = active.normalize()
//    println(iter.toDisplay())
        repeat(6) {
            iter = propagate(iter).filter { it.w == 0 }.toSet()
//        println(iter.toDisplay())
        }
        println("part1=${iter.size}")
    }

    run {
        var iter = active.normalize()
//    println(iter.toDisplay())
        repeat(6) {
            iter = propagate(iter)
//        println(iter.toDisplay())
        }
        println("part2=${iter.size}")
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun Set<Point>.normalize(): Set<Point> {
    val maxX = maxOfOrNull { it.x } ?: 0
    val maxY = maxOfOrNull { it.y } ?: 0
    val maxZ = maxOfOrNull { it.z } ?: 0
    val maxW = maxOfOrNull { it.w } ?: 0

    return map { (x, y, z, w) ->
        Point(x - maxX / 2, y - maxY / 2, z - maxZ / 2, w - maxW / 2)
    }.toSet()
}

@OptIn(ExperimentalStdlibApi::class)
fun propagate(active: Set<Point>): Set<Point> {
    val rX = ((active.minOfOrNull { it.x } ?: 0) - 1)..((active.maxOfOrNull { it.x } ?: 0) + 1)
    val rY = ((active.minOfOrNull { it.y } ?: 0) - 1)..((active.maxOfOrNull { it.y } ?: 0) + 1)
    val rZ = ((active.minOfOrNull { it.z } ?: 0) - 1)..((active.maxOfOrNull { it.z } ?: 0) + 1)
    val rW = ((active.minOfOrNull { it.w } ?: 0) - 1)..((active.maxOfOrNull { it.w } ?: 0) + 1)

    fun activeOrthogonal(point: Point): Set<Point> = buildSet {
        inSpace(-1..1, -1..1, -1..1, -1..1) { vector ->
            if (vector != Point.ZERO) {
                val element = point + vector
                if (element in active) {
                    add(element)
                }
            }
        }
    }

    return buildSet {
        inSpace(rX, rY, rZ, rW) { point ->
            when (point) {
                in active -> {
                    if (activeOrthogonal(point).size in 2..3) {
                        add(point) // remains active
                    } // else becomes inactive
                }
                else -> {
                    if (activeOrthogonal(point).size == 3) {
                        add(point) // becomes active
                    } // else remains inactive
                }
            }
        }
    }
}

fun Set<Point>.toDisplay(): String = buildString {
    val active = this@toDisplay

    val max = active.map { maxOf(abs(it.x), abs(it.y)) }.maxOrNull() ?: 1
    val minZ = active.minOfOrNull { it.z } ?: 0
    val maxZ = active.maxOfOrNull { it.z } ?: 0
    val minW = active.minOfOrNull { it.w } ?: 0
    val maxW = active.maxOfOrNull { it.w } ?: 0

    var prevZ = minZ - 1
    var prevW = minW - 1
    var prevY = max + 1
    inSpace(-max..max, -max..max, minZ..maxZ, minW..maxW) { point ->
        if (prevY < point.y) {
            appendLine()
        }
        prevY = point.y
        if (point.z != prevZ || point.w != prevW) {
            appendLine()
            appendLine("z=${point.z}, w=${point.w}")
            prevZ = point.z
            prevW = point.w
        }
        append(if (point in active) '#' else '.')
    }
}

fun inSpace(r: IntRange, block: (Point) -> Unit) {
    inSpace(r, r, r, r, block)
}

fun inSpace(rX: IntRange, rY: IntRange, rZ: IntRange, rW: IntRange, block: (Point) -> Unit) {
    for (w in rW) {
        for (z in rZ) {
            for (y in rY) {
                for (x in rX) {
                    block(Point(x, y, z, w))
                }
            }
        }
    }
}
