package aoc.day20

import utils.*

fun main() {
    val input = readInput("aoc/day20/input.txt")
    val sample1 = readInput("aoc/day20/sample1.txt")

    val part1 = part1(sample1)
    require(part1 == "32000000") { part1 }
    println(part1(input))

    println(part2(input))
}

fun part1(input: List<String>): String {
    val modules = Module.parse(input)

    var lowCount = 0L
    var highCount = 0L
    repeat(1000) {
        val queue = ArrayDeque<Signal>()
        queue.add(Signal(Button, "broadcaster", LOW_PULSE))
        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()
            if (next.pulse == HIGH_PULSE) highCount++ else lowCount++

            val module = modules[next.to] ?: continue
            val pulse = module.receive(next.from, next.pulse)
            if (pulse != null) {
                for (downstream in module.downstream) {
                    queue.add(Signal(module, downstream, pulse))
                }
            }
        }
    }

    return (lowCount * highCount).toString()
}

fun part2(input: List<String>): String {
    // 1. 'zp' is the only upstream module for 'rx' in my input.
    // 2. Because it is a conjunction module, all inputs need to be HIGH for it to output LOW.
    // Therefore: find how many button presses it takes for each upstream module of 'zp' to
    // output HIGH, and then calculate the LCM to determine the number of button presses until
    // they are all HIGH on the same button press.

    val modules = Module.parse(input)
    val upstream = modules.values.filter { "zp" in it.downstream }.map { it.label }

    val factors = mutableMapOf<String, Long>()

    var buttonPresses = 0L
    buttonPress@ while (true) {
        buttonPresses++

        val queue = ArrayDeque<Signal>()
        queue.add(Signal(Button, "broadcaster", LOW_PULSE))
        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()
            if (next.to == "zp" && next.pulse == HIGH_PULSE) {
                factors.compute(next.from.label) { _, old -> minOf(buttonPresses, old ?: Long.MAX_VALUE) }
                if (factors.keys.containsAll(upstream)) {
                    break@buttonPress
                }
            }

            val module = modules[next.to] ?: continue
            val pulse = module.receive(next.from, next.pulse)
            if (pulse != null) {
                for (downstream in module.downstream) {
                    queue.add(Signal(module, downstream, pulse))
                }
            }
        }
    }

    return factors.values.reduce { left, right -> lcm(left, right) }.toString()
}

const val LOW_PULSE = false
const val HIGH_PULSE = true

data class Signal(val from: Module, val to: String, val pulse: Boolean) {
    override fun toString(): String = "${from.label} -${if (pulse == HIGH_PULSE) "high" else "low"}-> $to"
}

sealed class Module(val label: String) {
    abstract val downstream: Iterable<String>
    abstract fun receive(from: Module, pulse: Boolean): Boolean?

    companion object {
        fun parse(lines: List<String>): Map<String, Module> {
            val modules = lines.map {
                val (module, downstream) = it.split(" -> ")
                val downstreamModules = downstream.split(", ")
                if (module == "broadcaster") {
                    Broadcaster(downstreamModules)
                } else {
                    when (val type = module[0]) {
                        '%' -> FlipFlop(module.drop(1), downstreamModules)
                        '&' -> Conjunction(module.drop(1), downstreamModules)
                        else -> error(type)
                    }
                }
            }.associateBy { it.label }

            for ((_, module) in modules) {
                for (d in module.downstream) {
                    val downstream = modules[d] ?: continue
                    if (downstream is Conjunction) {
                        downstream.receive(module, LOW_PULSE) // Prime memory
                    }
                }
            }

            return modules
        }

    }
}

object Button : Module("button") {
    override val downstream: Iterable<String> = listOf("broadcaster")

    override fun receive(from: Module, pulse: Boolean) = pulse

    override fun toString() = "button -> ${downstream.joinToString()}"
}

class Broadcaster(
    override val downstream: List<String>
) : Module("broadcaster") {


    override fun receive(from: Module, pulse: Boolean) = pulse

    override fun toString() = "broadcaster -> ${downstream.joinToString()}"
}

class FlipFlop(
    label: String,
    override val downstream: List<String>,
) : Module(label) {

    var on = false
        private set

    override fun receive(from: Module, pulse: Boolean): Boolean? {
        if (pulse == LOW_PULSE) {
            on = !on
            return if (on) HIGH_PULSE else LOW_PULSE
        } else {
            return null
        }
    }

    override fun toString() = "%$label -> ${downstream.joinToString()}"
}

class Conjunction(
    label: String,
    override val downstream: List<String>,
) : Module(label) {
    private val memory = mutableMapOf<Module, Boolean>()

    override fun receive(from: Module, pulse: Boolean): Boolean {
        memory[from] = pulse
        val allHigh = memory.values.all { it == HIGH_PULSE }
        return if (allHigh) LOW_PULSE else HIGH_PULSE
    }

    override fun toString() = "&$label -> ${downstream.joinToString()}"
}
