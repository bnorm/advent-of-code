fun main() {
    val sample = """
        nop +0
        acc +1
        jmp +4
        acc +3
        jmp -3
        acc -99
        acc +1
        jmp -4
        acc +6
    """.trimIndent()

//    val input = sample.trim().splitToSequence("\n")
    val input = readResourceText("input.txt").trim().splitToSequence("\n")

    val instructions = input.map { line ->
        val (op, value) = line.split(" ")
        Instruction(op, value.toInt()) to 0
    }.toList()

    runCatching {
        run(instructions.toMutableList())
    }.onFailure {
        println(it.message)
    }

    for (i in instructions.indices) {
        val copy = instructions.toMutableList()
        val (instruction, count) = instructions[i]
        if (instruction.op == "nop") {
            copy[i] = instruction.copy(op = "jmp") to count
        } else if (instruction.op == "jmp"){
            copy[i] = instruction.copy(op = "nop") to count
        } else {
            continue
        }
        runCatching {
            println(run(copy))
        }
    }

}

private fun run(instructions: MutableList<Pair<Instruction, Int>>): Int {
    var acc = 0
    var i = 0
    while (i < instructions.size) {
        val (instruction, count) = instructions[i]
        if (count == 1) error("$acc")
        instructions[i] = instruction to (count + 1)
        when (instruction.op) {
            "nop" -> i++
            "acc" -> {
                acc += instruction.value
                i++
            }
            "jmp" -> {
                i += instruction.value
            }
        }
    }
    return acc
}

data class Instruction(val op: String, val value: Int)
