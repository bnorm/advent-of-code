fun main() {
    val sample = """
        939
        7,13,x,x,59,x,31,19
    """.trimIndent()

//    val input = sample.trim().splitToSequence("\n")
    val input = readResourceText("input.txt").trim().splitToSequence("\n")

    val (timestamp, busSchedule) = input.toList()

    val buses = busSchedule.split(",")
        .mapIndexedNotNull { index, bus ->
            if (bus == "x") null
            else bus.toLong() to index.toLong()
        }

    val part1 = busSchedule.split(",")
        .filter { it != "x" }
        .map { bus ->
            bus.toInt() to (bus.toInt() - timestamp.toInt() % bus.toInt())
        }
        .minByOrNull { (_, after) -> after }!!
    println("part1=${part1.first * part1.second}")


    val max = buses.map { (_, a) -> a }.maxOrNull()!!
    val (_, ap) = buses
        .map { (n, a) ->
            // Find (timestamp + max) so remainders are positive
            n to (max - a) % n
        }
        .reduce { (n1, a1), (n2, a2) ->
            // https://en.wikipedia.org/wiki/Chinese_remainder_theorem#Using_the_existence_construction

            val (gcd, e1, e2) = euclid(n1, n2)

            val np = (n1 * n2) / gcd // least common denominator
            var ap = (a2.bg * n1.bg * e1.bg + a1.bg * n2.bg * e2.bg) % np.bg // chinese remainder theorem
            if (ap < 0L.bg) ap += np.bg

            np to ap.longValueExact()
        }

    println("part2=${ap - max}")
}
