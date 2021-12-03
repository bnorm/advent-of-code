package intcode

val INSTRUCTIONS = listOf(
    Instruction.Add,
    Instruction.Multiply,
    Instruction.ReadInput,
    Instruction.WriteOutput,
    Instruction.JumpIfNotZero,
    Instruction.JumpIfZero,
    Instruction.LessThan,
    Instruction.Equal,
    Instruction.AdjustRelativeBase,

    // Last
    Instruction.Exit,
)

val opCodes = INSTRUCTIONS.associateBy { it.code }

sealed class Instruction(
    val code: Long,
    val parameters: Int,
) {
    abstract fun perform(program: Program.State): Int
    abstract fun debug(program: Program.State): String

    object Exit : Instruction(99, 0) {
        override fun perform(program: Program.State): Int = Int.MIN_VALUE // -MIN_VALUE is still negative
        override fun debug(program: Program.State): String = "EXIT"
    }

    object Add : Instruction(1, 3) {
        override fun perform(program: Program.State): Int {
            val input1 = program.parameter(0)
            val input2 = program.parameter(1)
            val output = input1 + input2

            val outputAddress = program.addressParameter(2)
            program[outputAddress] = output

            return 4
        }

        override fun debug(program: Program.State): String {
            val input1 = program.debugParameter(0)
            val input2 = program.debugParameter(1)
            val output = program.parameter(0) + program.parameter(1)

            val outputAddress = program.debugAddressParameter(2)
            return "ADD $input1 $input2 = $output -> $outputAddress"
        }
    }

    object Multiply : Instruction(2, 3) {
        override fun perform(program: Program.State): Int {
            val input1 = program.parameter(0)
            val input2 = program.parameter(1)
            val output = input1 * input2

            val outputAddress = program.addressParameter(2)
            program[outputAddress] = output

            return 4
        }

        override fun debug(program: Program.State): String {
            val input1 = program.debugParameter(0)
            val input2 = program.debugParameter(1)
            val output = program.parameter(0) * program.parameter(1)

            val outputAddress = program.debugAddressParameter(2)
            return "MUL $input1 $input2 = $output -> $outputAddress"
        }
    }

    object ReadInput : Instruction(3, 1) {
        override fun perform(program: Program.State): Int {
            val address = program.addressParameter(0)
            val input = program.read()
            program[address] = input
            return 2
        }

        override fun debug(program: Program.State): String {
            val inputAddress = program.debugAddressParameter(0)
            return "READ -> $inputAddress"
        }
    }

    object WriteOutput : Instruction(4, 1) {
        override fun perform(program: Program.State): Int {
            val output = program.parameter(0)
            program.write(output)
            return 2
        }

        override fun debug(program: Program.State): String {
            val output = program.debugParameter(0)
            return "WRITE $output"
        }
    }

    object JumpIfNotZero : Instruction(5, 2) {
        override fun perform(program: Program.State): Int {
            val input = program.parameter(0)
            return if (input != 0L) {
                -program.parameter(1).toInt()
            } else {
                3
            }
        }

        override fun debug(program: Program.State): String {
            val input = program.debugParameter(0)
            return "JUMP $input!=0 -> ${program.debugParameter(1)}"
        }
    }

    object JumpIfZero : Instruction(6, 2) {
        override fun perform(program: Program.State): Int {
            val input = program.parameter(0)
            return if (input == 0L) {
                -program.parameter(1).toInt()
            } else {
                3
            }
        }

        override fun debug(program: Program.State): String {
            val input = program.debugParameter(0)
            return "JUMP $input==0 -> ${program.debugParameter(1)}"
        }
    }

    object LessThan : Instruction(7, 3) {
        override fun perform(program: Program.State): Int {
            val left = program.parameter(0)
            val right = program.parameter(1)
            val outputAddress = program.addressParameter(2)
            if (left < right) {
                program[outputAddress] = 1
            } else {
                program[outputAddress] = 0
            }
            return 4
        }

        override fun debug(program: Program.State): String {
            val left = program.debugParameter(0)
            val right = program.debugParameter(1)
            val outputAddress = program.debugAddressParameter(2)
            return "CMP $left < $right -> $outputAddress"
        }
    }

    object Equal : Instruction(8, 3) {
        override fun perform(program: Program.State): Int {
            val left = program.parameter(0)
            val right = program.parameter(1)
            val outputAddress = program.addressParameter(2)
            if (left == right) {
                program[outputAddress] = 1
            } else {
                program[outputAddress] = 0
            }
            return 4
        }

        override fun debug(program: Program.State): String {
            val left = program.debugParameter(0)
            val right = program.debugParameter(1)
            val outputAddress = program.debugAddressParameter(2)
            return "CMP $left = $right -> $outputAddress"
        }
    }

    object AdjustRelativeBase : Instruction(9, 1) {
        override fun perform(program: Program.State): Int {
            val value = program.parameter(0)
            program.adjustRelativeBase(value)
            return 2
        }

        override fun debug(program: Program.State): String {
            val value = program.debugParameter(0)
            return "ADJ $value"
        }
    }
}
