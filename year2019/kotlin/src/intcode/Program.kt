package intcode

import kotlin.math.pow

class Program(
    private val program: List<Long>,
    private val input: () -> Long = { TODO() },
    private val output: (Long) -> Unit = {},
) {
    fun run(): State {
        val execution = State(program, input, output)
        while (execution.isRunning()) {
            execution.next()
        }
        return execution
    }

    class State(
        program: List<Long>,
        private val input: () -> Long,
        private val output: (Long) -> Unit,
    ) {
        private val memory = program.toMutableList()
        private val registry = mutableMapOf<Int, Long>().withDefault { 0L }

        private var pointer = 0
        private var modes = 0L
        private var relativeBase = 0L
        private fun modeOf(index: Int): Int {
            return (modes / 10.0.pow(index).toInt()).rem(10).toInt()
        }

        fun isRunning(): Boolean = pointer in 0 until memory.size

        fun next() {
            check(isRunning())

            val value = this[pointer]
            val opCode = value.rem(100)
            modes = value / 100L
            val instruction = opCodes.getValue(opCode)

//            println("$pointer=${memory.subList(pointer, pointer + instruction.parameters + 1)} ${instruction.debug(this)}")
            val skip = instruction.perform(this)
            if (skip > 0) {
                pointer += skip
            } else {
                pointer = -skip
            }
        }

        operator fun get(address: Int): Long {
            require(address >= 0)
            return if (address < memory.size) {
                memory[address]
            } else {
                registry.getValue(address)
            }
        }

        operator fun set(address: Int, value: Long) {
            require(address >= 0)
            if (address < memory.size) {
                memory[address] = value
            } else {
                registry[address] = value
            }
        }

        fun read(): Long {
            return input()
        }

        fun write(value: Long) {
            output(value)
        }

        fun parameter(index: Int): Long {
            val parameter = this[pointer + index + 1]
            return when (val mode = modeOf(index)) {
                0 -> this[parameter.toInt()]
                1 -> parameter
                2 -> this[(relativeBase + parameter).toInt()]
                else -> throw IllegalStateException("modes=$modes mode=$mode index=$index")
            }
        }

        fun addressParameter(index: Int): Int {
            require(index >= 0)

            val parameter = this[pointer + index + 1]
            return when (val mode = modeOf(index)) {
                0 -> parameter.toInt()
                2 -> (relativeBase + parameter).toInt()
                else -> throw IllegalStateException("modes=$modes mode=$mode index=$index")
            }
        }

        fun adjustRelativeBase(adjustment: Long) {
            relativeBase += adjustment
        }

        fun debugParameter(index: Int): String {
            val parameter = this[pointer + index + 1]
            return when (val mode = modeOf(index)) {
                0 -> "${this[parameter.toInt()]}(I=$index M=0 P=$parameter)"
                1 -> "$parameter(I=$index M=1)"
                2 -> "${this[(relativeBase + parameter).toInt()]}(I=$index M=2 B=$relativeBase P=$parameter)"
                else -> throw IllegalStateException("modes=$modes mode=$mode index=$index")
            }
        }

        fun debugAddressParameter(index: Int): String {
            val parameter = this[pointer + index + 1]
            return when (modeOf(index)) {
                0 -> "${parameter.toInt()}(I=$index M=0)"
                2 -> "${(relativeBase + parameter)}(I=$index M=2 B=$relativeBase)"
                else -> "ERROR(I=$index M=1)"
            }
        }
    }
}
