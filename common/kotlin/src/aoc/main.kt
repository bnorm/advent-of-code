package aoc

import aoc.input.downloadInput
import kotlin.time.measureTimedValue

suspend fun run(
    year: Int,
    day: Int,
    part1: (String) -> String,
    sample1: String,
    expected1: String,
    part2: (String) -> String,
    sample2: String = sample1,
    expected2: String,
) {
    val input = downloadInput(year, day)
    test(expected1) { part1(sample1) }
    benchmark("Part 1: ") { part1(input) }
    test(expected2) { part2(sample2) }
    benchmark("Part 2: ") { part2(input) }
}

fun test(expected: String, block: () -> String) {
    val result = block()
    if (result != expected) {
        throw IllegalStateException(
            """
                Test failed.
                Expected = $expected
                Result   = $result
            """.trimIndent()
        )
    }
}

fun benchmark(prefix: String, warmup: Boolean = true, block: () -> String) {
    if (warmup) repeat(5) { block() }
    val (result, duration) = measureTimedValue { block() }
    println("$prefix$result (${duration})")
}
