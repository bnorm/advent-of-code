package day05

import utils.*

fun main() {
    val sample1 = readInput("day05.sample1.txt")
    val sample2 = readInput("day05.sample2.txt")
    val input = readInput("day05.txt")

    require(part1(sample1) == "35")
    println(part1(input))

    require(part2(sample2) == "46")
    println(part2(input))
}

fun part1(input: List<String>): String {
    val almanac = Almanac.parse(input)

    var seeds = almanac.seeds
    for (map in almanac.conversionMaps) {
        seeds = seeds.map { map.convert(it) }.sorted()
    }

    return seeds[0].toString()
}

fun part2(input: List<String>): String {
    val almanac = Almanac.parse(input)

    var seedRanges = almanac.seeds.windowed(size = 2, step = 2).map { it[0]..<it[0] + it[1] }.sortedBy { it.first }
    for (map in almanac.conversionMaps) {
        seedRanges = seedRanges.flatMap { map.convert(it) }.sortedBy { it.first }
    }

    return seedRanges[0].first.toString()
}

data class Almanac(
    val seeds: List<Long>,
    val conversionMaps: List<ConversionMap>,
) {
    companion object {
        fun parse(input: List<String>): Almanac {
            val sections = mutableListOf<List<String>>()
            var section = mutableListOf<String>()
            sections.add(section)

            for (line in input) {
                if (line.isBlank()) {
                    section = mutableListOf()
                    sections.add(section)
                } else {
                    section.add(line)
                }
            }

            return Almanac(
                seeds = sections[0][0].split(" ").drop(1).map { it.toLong() },
                conversionMaps = sections.drop(1).map { ConversionMap.parse(it) },
            )
        }
    }
}

data class ConversionMap(
    val fromName: String,
    val toName: String,
    private val rangeDeltas: Map<LongRange, Long>,
) {
    fun convert(value: Long): Long {
        val delta = rangeDeltas.entries.find { value in it.key }?.value ?: 0
        return value + delta
    }

    fun convert(values: LongRange): List<LongRange> {
        val dividers = listOf(values.first) +
                rangeDeltas.keys.flatMap { listOf(it.first, it.last + 1) }.filter { it in values }.sorted() +
                listOf(values.last + 1)

        return dividers.zipWithNext { first, last ->
            val delta = rangeDeltas.entries.find { first in it.key }?.value ?: 0
            return@zipWithNext first + delta..<last + delta
        }
    }

    companion object {
        fun parse(input: List<String>): ConversionMap {
            val names = input[0].split(" ")[0].split("-")
            return ConversionMap(
                fromName = names[0],
                toName = names[2],
                rangeDeltas = input.drop(1).associate {
                    val numbers = it.split(" ")
                    val toStart = numbers[0].toLong()
                    val fromStart = numbers[1].toLong()
                    val length = numbers[2].toLong()
                    fromStart..<fromStart + length to toStart - fromStart
                },
            )
        }
    }
}
