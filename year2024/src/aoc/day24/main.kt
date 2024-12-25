package aoc.day24

import aoc.input.downloadInput
import utils.toPairs
import utils.transform

const val SAMPLE1 = """
x00: 1
x01: 0
x02: 1
x03: 1
x04: 0
y00: 1
y01: 1
y02: 1
y03: 1
y04: 1

ntg XOR fgs -> mjb
y02 OR x01 -> tnw
kwq OR kpj -> z05
x00 OR x03 -> fst
tgd XOR rvg -> z01
vdt OR tnw -> bfw
bfw AND frj -> z10
ffh OR nrd -> bqk
y00 AND y03 -> djm
y03 OR y00 -> psh
bqk OR frj -> z08
tnw OR fst -> frj
gnj AND tgd -> z11
bfw XOR mjb -> z00
x03 OR x00 -> vdt
gnj AND wpb -> z02
x04 AND y00 -> kjc
djm OR pbm -> qhw
nrd AND vdt -> hwm
kjc AND fst -> rvg
y04 OR y02 -> fgs
y01 AND x02 -> pbm
ntg OR kjc -> kwq
psh XOR fgs -> tgd
qhw XOR tgd -> z09
pbm OR djm -> kpj
x03 XOR y03 -> ffh
x00 XOR y04 -> ntg
bfw OR bqk -> z06
nrd XOR fgs -> wpb
frj XOR qhw -> z04
bqk OR frj -> z07
y03 OR x01 -> nrd
hwm AND bqk -> z03
tgd XOR rvg -> z12
tnw OR pbm -> gnj
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 24)

    val part1 = part1(SAMPLE1)
    require(part1 == "2024") { part1 }
    println(part1(input))
    println(part2(input))
}

private fun part1(input: String): String {
    val gates = parseGates(input)

    var result = 0L
    for (name in gates.keys.filter { it.startsWith("z") }.sortedDescending()) {
        val gate = gates.getValue(name)
        if (gate is Equation) gate.calculate(gates)

        result = result shl 1
        result += gate.result
    }

    return result.toString()
}

private fun part2(input: String): String {
    val gates = parseGates(input)
    val equations = gates.values.filterIsInstance<Equation>()

    val tried = mutableSetOf<Pair<Equation, Equation>>()
    fun findSwapped(remaining: Int, swapped: Set<String>): Set<String>? {
        val invalid = equations.count { !it.isValid }
        if (remaining == 0) {
            return if (invalid == 0) swapped else null
        }

        val potential = equations.asSequence().transform {
            if (!it.isValid) {
                val g1 = (gates[it.input1] as? Equation)?.takeIf { it.isValid }
                val g2 = (gates[it.input2] as? Equation)?.takeIf { it.isValid }
                if (g1 != null) yield(g1)
                if (g2 != null) yield(g2)
            } else if (it.name.startsWith("z") && it.role != 'z') {
                yield(it)
            }
        }.distinct().toList()

        for ((g1, g2) in potential.toPairs()) {
            if (!tried.add(g1 to g2)) continue

            // Swap gates.
            gates[g1.name] = g2
            gates[g2.name] = g1

            if (validate(gates) < invalid) {
                // Recurse.
                return findSwapped(remaining - 1, swapped + g1.name + g2.name)
            }

            // Swap gates back.
            gates[g1.name] = g1
            gates[g2.name] = g2
        }

        return null
    }

    validate(gates) // Run validation to find invalid gates.
    val swapped = findSwapped(remaining = 4, swapped = mutableSetOf()).orEmpty()
    return swapped.sorted().joinToString(separator = ",")
}

private fun parseGates(input: String): MutableMap<String, Gate> {
    val gates1 = mutableMapOf<String, Gate>()
    input.substringAfter("\n\n").trim().lines().forEach {
        val (input1, operation, input2, _, output) = it.split(" ")
        gates1[output] = Equation(input1, input2, operation, output)
    }
    input.substringBefore("\n\n").trim().lines().forEach {
        val (name, value) = it.split(": ")
        gates1[name] = Value(name, value.toInt())
    }
    val gates = gates1
    return gates
}

sealed class Gate {
    abstract val name: String
    abstract val result: Int

    abstract val role: Char
    abstract val bit: Int
}

class Value(
    override val name: String,
    override val result: Int,
) : Gate() {
    override val role: Char = if (name.startsWith("x")) 'x' else 'y'
    override val bit: Int = name.substring(1).toInt()

    override fun toString(): String {
        return name
    }
}

class Equation(
    val input1: String,
    val input2: String,
    val operation: String,
    override val name: String,
) : Gate() {
    override var result: Int = -1
        private set

    override var role: Char = ' '
        private set
    override var bit: Int = -1
        private set
    val isValid: Boolean
        get() = role != ' ' && bit != -1

    fun calculate(gates: Map<String, Gate>) {
        if (result == -1) {
            val gate1 = gates[input1] ?: error("input required: $input1")
            if (gate1 is Equation) gate1.calculate(gates)

            val gate2 = gates[input2] ?: error("input required: $input2")
            if (gate2 is Equation) gate2.calculate(gates)

            result = when (operation) {
                "AND" -> gate1.result and gate2.result
                "OR" -> gate1.result or gate2.result
                "XOR" -> gate1.result xor gate2.result
                else -> error("operation not supported: $operation")
            }
        }
    }

    /**
     * ```
     * x + y = z
     *
     * x00 XOR y00 -> z00 // bit0
     *
     * x00 AND y00 -> c00 // carry0
     * y01 XOR x01 -> i01 //
     * i01 XOR c00 -> z01 // bit1
     *
     * i01 AND c00 -> n01 //
     * x01 AND y01 -> m01 //
     * n01 OR  m01 -> c01 // carry1
     * y02 XOR x02 -> i02 //
     * c01 XOR i02 -> z02 // bit2
     *
     * i02 AND c01 -> n02 //
     * x02 AND y02 -> m02 //
     * n02 OR  m02 -> c02 // carry2
     * y03 XOR x03 -> i03 //
     * c02 XOR i03 -> z03 // bit3
     *
     * ... etc ...
     * ```
     */
    fun validate(gates: Map<String, Gate>) {
        if (role == ' ') {
            validate0(gates, mutableSetOf())
        }
    }

    private fun validate0(gates: Map<String, Gate>, stack: MutableSet<Gate>) {
        if (!stack.add(this)) return // Cycles in gates are never valid.

        val gate1 = gates[input1] ?: error("input required: $input1")
        if (gate1 is Equation) gate1.validate0(gates, stack)

        val gate2 = gates[input2] ?: error("input required: $input2")
        if (gate2 is Equation) gate2.validate0(gates, stack)

        val inputRoles = setOf(gate1.role, gate2.role)

        if (setOf('x', 'y') == inputRoles) {
            if (gate1.bit == gate2.bit) {
                bit = gate1.bit

                if (operation == "XOR") {
                    if (bit == 0) {
                        role = 'z'
                    } else {
                        role = 'i'
                    }
                } else if (operation == "AND") {
                    if (bit == 0) {
                        role = 'c'
                    } else {
                        role = 'm'
                    }
                }
            }
        } else if (setOf('i', 'c') == inputRoles) {
            val i = if (gate1.role == 'i') gate1 else gate2
            val c = if (gate1.role == 'i') gate2 else gate1

            if (i.bit - c.bit == 1) {
                bit = i.bit

                if (operation == "XOR") {
                    role = 'z'
                } else if (operation == "AND") {
                    role = 'n'
                }
            }
        } else if (setOf('n', 'm') == inputRoles) {
            if (gate1.bit == gate2.bit) {
                bit = gate1.bit

                if (operation == "OR") {
                    if (bit > 0) {
                        role = 'c'
                    }
                }
            }
        }
    }

    override fun toString(): String {
        return if (bit != -1) {
            "$role${bit.toString().padStart(2, '0')}"
        } else {
            name
        }
    }
}

private fun validate(gates: Map<String, Gate>): Int {
    var invalid = 0
    for (gate in gates.values) {
        if (gate is Equation) {
            gate.validate(gates)
            if (!gate.isValid) invalid++
        }
    }
    return invalid
}
