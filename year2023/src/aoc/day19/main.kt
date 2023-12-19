package aoc.day19

import utils.*

fun main() {
    val input = readInput("aoc/day19/input.txt")
    val sample1 = readInput("aoc/day19/sample1.txt")
    val sample2 = readInput("aoc/day19/sample2.txt")

    val part1 = part1(sample1)
    require(part1 == "19114") { part1 }
    println(part1(input))

    val part2 = part2(sample2)
    require(part2 == "167409079868000") { part2 }
    println(part2(input))
}

fun part1(input: List<String>): String {
    val (workflows, parts) = input.separateBy { it.isBlank() }
        .let { (workflows, parts) ->
            workflows.map { Workflow.parse(it) }.associateBy { it.name } to parts.map { Part.parse(it) }
        }

    val accepted = parts.filter { process(it, workflows) }
    return accepted.sumOf { it.x + it.m + it.a + it.s }.toString()
}

fun part2(input: List<String>): String {
    val workflows = input.separateBy { it.isBlank() }[0]
        .map { Workflow.parse(it) }.associateBy { it.name }

    val valid = process(PartRange(), "in", workflows)
    return valid.sumOf { it.size }.toString()
}

fun process(part: Part, workflows: Map<String, Workflow>): Boolean {
    var workflow = workflows.getValue("in")
    while (true) {
        when (val next = workflow.directives.firstNotNullOf { it.eval(part) }) {
            "A" -> return true
            "R" -> return false
            else -> workflow = workflows.getValue(next)
        }
    }
}

fun process(range: PartRange, name: String, workflows: Map<String, Workflow>): List<PartRange> {
    val workflow = workflows.getValue(name)
    val destinations = mutableMapOf<String, MutableList<PartRange>>()

    var carry: PartRange? = range
    for (directive in workflow.directives) {
        for ((destination, split) in directive.split(carry?.also { carry = null } ?: break)) {
            when (destination) {
                null -> carry = split // Carry this range to the next directive.
                else -> destinations.getOrPut(destination) { mutableListOf() }.add(split)
            }
        }
    }

    val valid = mutableListOf<PartRange>()
    for ((destination, ranges) in destinations) {
        valid += when (destination) {
            "R" -> emptyList() // Valid ranges should not be included.
            "A" -> ranges // Accepted ranges can be included.

            // Ranges destined for other workflows need to be processed recursively.
            else -> ranges.flatMap { process(it, destination, workflows) }
        }
    }
    return valid
}

data class Workflow(
    val name: String,
    val directives: List<Directive>,
) {
    companion object {
        fun parse(line: String): Workflow {
            val (name, directives) = line.dropLast(1).split('{')
            return Workflow(
                name = name,
                directives = directives.split(',').map { Directive.parse(it) },
            )
        }
    }

    data class Directive(
        val destination: String,
        val condition: Condition? = null,
    ) {
        fun eval(part: Part): String? =
            destination.takeIf { condition?.invoke(part) != false }

        fun split(range: PartRange): Map<String?, PartRange> {
            if (condition == null) {
                return mapOf(destination to range)
            } else {
                val split = condition.split(range)
                return mapOf(
                    destination to split[0],
                    null to split[1],
                )
            }
        }

        companion object {
            fun parse(line: String): Directive {
                if (':' in line) {
                    val (condition, destination) = line.split(':')
                    val lower = '<' in condition
                    val value = condition.substringAfter('<').substringAfter('>').toLong()
                    return Directive(
                        destination = destination,
                        condition = Condition(condition[0], lower, value),
                    )
                }

                return Directive(destination = line)
            }
        }

        class Condition(
            private val variable: Char,
            private val lower: Boolean,
            private val value: Long,
        ) : (Part) -> Boolean {
            private val property = when (variable) {
                'x' -> Part::x
                'm' -> Part::m
                'a' -> Part::a
                's' -> Part::s
                else -> error("!")
            }

            override fun invoke(p1: Part): Boolean {
                return when (lower) {
                    true -> property.invoke(p1) < value
                    false -> property.invoke(p1) > value
                }
            }

            fun split(range: PartRange): List<PartRange> {
                return listOf(
                    when (lower) {
                        true -> when (variable) {
                            'x' -> range.copy(x = range.x.first..<value)
                            'm' -> range.copy(m = range.m.first..<value)
                            'a' -> range.copy(a = range.a.first..<value)
                            's' -> range.copy(s = range.s.first..<value)
                            else -> error("!")
                        }

                        false -> when (variable) {
                            'x' -> range.copy(x = value + 1..range.x.last)
                            'm' -> range.copy(m = value + 1..range.m.last)
                            'a' -> range.copy(a = value + 1..range.a.last)
                            's' -> range.copy(s = value + 1..range.s.last)
                            else -> error("!")
                        }
                    },
                    when (lower) {
                        true -> when (variable) {
                            'x' -> range.copy(x = value..range.x.last)
                            'm' -> range.copy(m = value..range.m.last)
                            'a' -> range.copy(a = value..range.a.last)
                            's' -> range.copy(s = value..range.s.last)
                            else -> error("!")
                        }

                        false -> when (variable) {
                            'x' -> range.copy(x = range.x.first..value)
                            'm' -> range.copy(m = range.m.first..value)
                            'a' -> range.copy(a = range.a.first..value)
                            's' -> range.copy(s = range.s.first..value)
                            else -> error("!")
                        }
                    },
                )
            }

            override fun toString(): String {
                return when (lower) {
                    true -> "$variable<$value"
                    false -> "$variable>$value"
                }
            }
        }
    }
}

data class Part(
    val x: Long,
    val m: Long,
    val a: Long,
    val s: Long,
) {
    companion object {
        fun parse(line: String): Part {
            val (x, m, a, s) = line.drop(1).dropLast(1).split(",")
            return Part(
                x = x.substringAfter("=").toLong(),
                m = m.substringAfter("=").toLong(),
                a = a.substringAfter("=").toLong(),
                s = s.substringAfter("=").toLong(),
            )
        }
    }
}

data class PartRange(
    val x: LongRange = LongRange(1, 4000),
    val m: LongRange = LongRange(1, 4000),
    val a: LongRange = LongRange(1, 4000),
    val s: LongRange = LongRange(1, 4000),
) : Comparable<PartRange> {
    override fun compareTo(other: PartRange): Int {
        return comparator.compare(this, other)
    }

    val size get() = x.count() * m.count() * a.count() * s.count()
    private fun LongRange.count() = (last - first + 1)

    companion object {
        private val comparator =
            compareBy<PartRange> { it.x.first }.thenBy { it.x.last }
                .thenBy { it.m.first }.thenBy { it.m.last }
                .thenBy { it.a.first }.thenBy { it.a.last }
                .thenBy { it.s.first }.thenBy { it.s.last }
    }

}
