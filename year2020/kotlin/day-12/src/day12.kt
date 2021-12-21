import kotlin.math.abs

fun main() {
    val sample = """
        F10
        N3
        F7
        R90
        F11
    """.trimIndent()

//    val input = sample.trim().splitToSequence("\n")
    val input = readResourceText("input.txt").trim().splitToSequence("\n")

    val commands = input.map {
        val (cmd, amount) = it.extract("([A-Z]+)(\\d+)")!!
        cmd to amount.toInt()
    }

    val directions = listOf("N", "E", "S", "W")

    run {
        var curr = "E"
        var x = 0
        var y = 0
        for ((cmd, amount) in commands) {
            when (if (cmd == "F") curr else cmd) {
                "R" -> curr = directions[(directions.size + directions.indexOf(curr) + (amount / 90)) % directions.size]
                "L" -> curr = directions[(directions.size + directions.indexOf(curr) - (amount / 90)) % directions.size]
                "N" -> y += amount
                "E" -> x += amount
                "S" -> y -= amount
                "W" -> x -= amount
            }
        }
        println("part1=${abs(x) + abs(y)}")
    }

    run {
        var wX = 10
        var wY = 1
        var sX = 0
        var sY = 0
        for ((cmd, amount) in commands) {
            when (cmd) {
                "R", "L" -> {
                    var rotation = if (cmd == "R") amount else 360 - amount
                    while (rotation > 0) {
                        val tmp = wX
                        wX = wY
                        wY = -tmp
                        rotation -= 90
                    }
                }
                "N" -> wY += amount
                "E" -> wX += amount
                "S" -> wY -= amount
                "W" -> wX -= amount
                "F" -> {
                    sX += wX * amount
                    sY += wY * amount
                }
            }
        }
        println("part2=${abs(sX) + abs(sY)}")
    }
}
