import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

fun main() {
    val sample = """
        0,3,6
    """.trimIndent()

//    val input = sample.trim().splitToSequence(",")
    val input = readResourceText("input.txt").trim().splitToSequence(",")

    val startingNumbers = input.map { it.toLong() }

    val sequence = sequence {
        val memory0 = mutableMapOf<Long, Int>()
        val memory1 = mutableMapOf<Long, Int>()
        var prev = -1L
        var i = 0

        fun commit(n: Long) {
            prev = n
            val tmp = memory0.put(n, i++)
            if (tmp != null) memory1[n] = tmp
        }

        for (n in startingNumbers) {
            yield(n)
            commit(n)
        }

        while (true) {
            val h0 = i - 1
            val h1 = memory1[prev] ?: h0
            val n = (h0 - h1).toLong()

            yield(n)
            commit(n)
        }
    }

    println(measureTimeMillis {
        val part1 = sequence
            .take(2020)
            .last()
        println("part1=$part1")
    })

    println(measureTimeMillis {
        val part2 = sequence
            .take(30_000_000)
            .last()
        println("part2=$part2")
    })
}
