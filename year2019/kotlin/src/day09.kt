import intcode.Program

fun main() {
    val test = false
    val sample = """
        109, 1, 203, 2, 204, 2, 99
    """.trimIndent()

    val input = (if (test) sample else readResourceText("input-day09.txt"))
        .splitToSequence("\n", ",")
    val program = input.filter { it.isNotBlank() }
        .map { it.trim().toLong() }
        .toList()

    run {
        val output = mutableListOf<Long>()
        Program(program, { 2 }, { output.add(it) }).run()
        println("output = $output")
    }
}
