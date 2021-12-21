import intcode.Program

fun main() {
    val test = false
    val sample = """
        1,9,10,3,
        2,3,11,0,
        99,
        30,40,50
    """.trimIndent()

    val input = (if (test) sample else readResourceText("input-day02.txt"))
        .splitToSequence("\n", ",")
    val program = input.filter { it.isNotBlank() }
        .map { it.trim().toLong() }

    run {
        val memory = program.toMutableList()
        memory[1] = 12
        memory[2] = 2
        val result = Program(memory).run()
        val part1 = result[0]
        println("part1 = $part1")
    }

    search@ for (noun in 0..99L) {
        for (verb in 0..99L) {
            val memory = program.toMutableList()
            memory[1] = noun
            memory[2] = verb
            val result = Program(memory).run()
            val part2 = result[0]
            if (part2 == 19690720L) {
                println("part2: noun=$noun verb=$verb result=${100 * noun + verb}")
                break@search
            }
        }
    }
}
