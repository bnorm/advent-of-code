fun main() {
    val sample = """
        mask = 000000000000000000000000000000X1001X
        mem[42] = 100
        mask = 00000000000000000000000000000000X0XX
        mem[26] = 1
    """.trimIndent()

//    val input = sample.trim().splitToSequence("\n")
    val input = readResourceText("input.txt").trim().splitToSequence("\n")

    val operations = input.map {
        val (op, value) = it.split(" = ")
        op to value
    }

    run {
        var mask = "X".repeat(36)
        val memory = mutableMapOf<Int, String>()

        fun setMemory(address: Int, value: String) {
            memory[address] = value.zip(mask) { v, m -> if (m == 'X') v else m }.joinToString("")
        }

        operations.forEach { (op, value) ->
            if ("mask" == op) {
                mask = value
            } else {
                val (address) = op.extract("mem\\[(\\d+)]")!!
                setMemory(address.toInt(), value.toLong().toString(2).padStart(36, '0'))
            }
//            println("mask = $mask")
//            println("memory = $memory")
        }

        val part1 = memory.values.map { it.toLong(2) }.sum()

        println("part1=$part1")
    }

    run {
        var mask = "X".repeat(36)
        val memory = mutableMapOf<String, String>()

        fun addressPermutations(address: String): List<String> {
            return when (address.indexOf('X')) {
                -1 -> listOf(address)
                else -> addressPermutations(address.replaceFirst('X', '0')) +
                        addressPermutations(address.replaceFirst('X', '1'))
            }
        }

        fun setMemory(address: String, value: String) {
//            println("mem[$address] = $value")
            val masked = address.zip(mask) { v, m -> if (m == '0') v else m }.joinToString("")

            for (a in addressPermutations(masked)) {
                memory[a] = value
            }
        }

        operations.forEach { (op, value) ->
            if ("mask" == op) {
                mask = value
            } else {
                val (address) = op.extract("mem\\[(\\d+)]")!!
                setMemory(
                    address.toLong().toString(2).padStart(36, '0'),
                    value.toLong().toString(2).padStart(36, '0')
                )
            }
//            println("mask = $mask")
//            println("memory = $memory")
        }

        val part2 = memory.values.map { it.toLong(2) }.sum()

        println("part2=$part2")
    }
}
