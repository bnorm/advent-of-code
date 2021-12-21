import intcode.Program

fun main() {
    val test = false
    val sample = """
        1002,4,3,4,33
    """.trimIndent()

    val input = (if (test) sample else readResourceText("input-day05.txt"))
        .splitToSequence("\n", ",")
    val program = input.filter { it.isNotBlank() }
        .map { it.trim().toLong() }
        .toList()

    run {
        val output = mutableListOf<Long>()
        Program(program, { 1 }, { output.add(it) }).run()
        println("part1 = $output")
    }

    run {
        val output = mutableListOf<Long>()
        Program(program, { 5 }, { output.add(it) }).run()
        println("part2 = $output")
    }
}
