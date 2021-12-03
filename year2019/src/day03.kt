fun main() {
    val test = false
    val sample = """
        R75,D30,R83,U83,L12,D49,R71,U7,L72
        U62,R66,U55,R34,D71,R55,D58,R83
    """.trimIndent()

    val input = (if (test) sample else readResourceText("input-day03.txt"))
        .splitToSequence("\n")
    val wires = input.filter { it.isNotBlank() }

    val paths = wires
        .map { it.trim().split(",") }
        .map { expandPath(it) }

    val common = paths.map { it.keys }.reduce { acc, other -> acc intersect other }
    println(common.map { p -> paths.sumBy { it[p]!! } }.minOrNull())
}

private val regex = "([RDUL])(\\d+)".toRegex()
private data class Point(val x: Int, val y: Int)
private fun expandPath(route: List<String>): Map<Point, Int> {
    var x = 0
    var y = 0
    var step = 0
    val output = mutableMapOf<Point, Int>()
    for ((direction, length) in route.map { regex.matchEntire(it)!!.destructured }) {
        repeat(length.toInt()) {
            step++
            when (direction) {
                "R" -> x++
                "L" -> x--
                "U" -> y++
                "D" -> y--
                else -> check(false)
            }
            val point = Point(x, y)
            if (point !in output) output[point] = step
        }
    }
    return output
}
