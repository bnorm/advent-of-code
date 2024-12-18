package aoc.day17

import aoc.input.downloadInput

const val SAMPLE1 = """
Register A: 729
Register B: 0
Register C: 0

Program: 0,1,5,4,3,0
"""

const val SAMPLE2 = """
Register A: 2024
Register B: 0
Register C: 0

Program: 0,3,5,4,3,0
"""

suspend fun main() {
    val input = downloadInput(2024, 17)

    val part1 = part1(SAMPLE1)
    require(part1 == "4,6,3,5,6,3,5,2,1,0") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "117440") { part2 }
    println(part2(input))
}

private fun part1(input: String): String {
    val (registersStr, instructionsStr) = input.trim().split("\n\n")
    val registers = registersStr.split("\n").map { it.substringAfter(":").trim().toLong() }
    val instructions = instructionsStr.substringAfter(":").trim().split(",").map { it.toInt() }

    val output = runUntilHalt(instructions, registers)

    return output.joinToString(",")
}

private fun part2(input: String): String {
    val (_, instructionsStr) = input.trim().split("\n\n")
    val instructions = instructionsStr.substringAfter(":").trim().split(",").map { it.toInt() }

    // Invariants of my program:
    // 1. Program loops until 'A' == 0.
    // 2. Each iteration right-shifts 'A' by 3.
    // 3. 'B' and 'C' are purely calculated from 'A'.
    //
    // Starting with the last instruction, search backwards, building up 'A'.
    // At each step, left-shift 'A' by 3 and add some number between 0-7.
    // If the first output of the program results in the desired instruction, recurse to the next instruction.
    //
    // Theoretical search space, where n is the number of instructions, is 8^n = 2^3n.
    // Though, in practice this is MUCH smaller, as few values of 'A' result in the correct output.
    fun search(i: Int, a: Long): Long? {
        // println("search($i, 0b${a.toString(2).padStart(instructions.size * 3, '0')}")
        if (i < 0) return a
        for (n in 0..<8) {
            val guess = (a shl 3) + n
            if (guess == 0L) continue
            val o = runUntilFirstOutput(instructions, a = guess)
            if (o == instructions[i]) {
                search(i - 1, guess)?.let { return it }
            }
        }
        return null
    }

    return search(instructions.lastIndex, 0L).toString()
}

private fun runUntilHalt(instructions: List<Int>, registers: List<Long>): MutableList<Int> {
    val output = mutableListOf<Int>()

    val (a, b, c) = registers
    val computer = Computer(instructions, a, b, c, output::add)
    while (computer.hasInstruction()) {
        Instruction.entries[computer.nextInstruction()!!].perform(computer)
    }

    return output
}

private fun runUntilFirstOutput(instructions: List<Int>, a: Long): Int {
    var output = -1

    val computer = Computer(instructions, a, 0, 0) { output = it }
    while (computer.hasInstruction()) {
        Instruction.entries[computer.nextInstruction()!!].perform(computer)
        if (output != -1) return output
    }

    error("!")
}

class Computer(
    val instructions: List<Int>,
    var a: Long,
    var b: Long,
    var c: Long,
    val output: (Int) -> Unit,
) {
    var index = 0

    fun hasInstruction(): Boolean = index < instructions.size
    fun nextInstruction(): Int? = if (hasInstruction()) instructions[index++] else null

    fun combo(operand: Int): Long {
        return when (operand) {
            in 0..3 -> operand.toLong()
            4 -> a
            5 -> b
            6 -> c
            7 -> error("reserved")
            else -> error("illegal")
        }
    }
}

enum class Instruction {
    adv {
        override fun perform(computer: Computer) {
            val operand = computer.nextInstruction() ?: return
            computer.a = computer.a shr computer.combo(operand).toInt()
        }
    },
    bxl {
        override fun perform(computer: Computer) {
            val operand = computer.nextInstruction() ?: return
            computer.b = computer.b xor operand.toLong()
        }
    },
    bst {
        override fun perform(computer: Computer) {
            val operand = computer.nextInstruction() ?: return
            computer.b = computer.combo(operand) and 7
        }
    },
    jnz {
        override fun perform(computer: Computer) {
            if (computer.a == 0L) return
            val operand = computer.nextInstruction() ?: return
            computer.index = operand.toInt()
        }
    },
    bxc {
        override fun perform(computer: Computer) {
            computer.nextInstruction() // Ignored
            computer.b = computer.b xor computer.c
        }
    },
    `out` {
        override fun perform(computer: Computer) {
            val operand = computer.nextInstruction() ?: return
            computer.output((computer.combo(operand) and 7).toInt())
        }
    },
    bdv {
        override fun perform(computer: Computer) {
            val operand = computer.nextInstruction() ?: return
            computer.b = computer.a shr computer.combo(operand).toInt()
        }
    },
    cdv {
        override fun perform(computer: Computer) {
            val operand = computer.nextInstruction() ?: return
            computer.c = computer.a shr computer.combo(operand).toInt()
        }
    },
    ;

    abstract fun perform(computer: Computer)
}
