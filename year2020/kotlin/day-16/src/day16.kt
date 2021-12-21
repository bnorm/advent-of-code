fun main() {
    val sample = """
        class: 0-1 or 4-19
        row: 0-5 or 8-19
        seat: 0-13 or 16-19
        
        your ticket:
        11,12,13
        
        nearby tickets:
        3,9,18
        15,1,5
        5,14,9
    """.trimIndent()

//    val input = sample.trim().splitToSequence("\n\n")
    val input = readResourceText("input.txt").trim().splitToSequence("\n\n")

    val (ruleBlock, yourBlock, nearbyBlock) = input.toList()

    val rules = ruleBlock.trim().splitToSequence("\n").map {
        val (name, r11, r12, r21, r22) = it.extract("([a-z ]+): (\\d+)-(\\d+) or (\\d+)-(\\d+)")!!
        Triple(name, r11.toInt()..r12.toInt(), r21.toInt()..r22.toInt())
    }

    val yourTicket = yourBlock.trim().splitToSequence("\n")
        .drop(1)
        .map { line -> line.split(",").map { it.toInt() } }
        .single()

    val nearbyTickets = nearbyBlock.trim().splitToSequence("\n")
        .drop(1)
        .map { line -> line.split(",").map { it.toInt() } }
        .toList()

    val part1 = nearbyTickets.flatMap { values ->
        values.filter { value ->
            rules.none { (_, r1, r2) -> value in r1 || value in r2 }
        }
    }.sum()
    println("part1=$part1")


    val validTickets = nearbyTickets.filter { values ->
        values.all { value -> rules.any { (_, r1, r2) -> value in r1 || value in r2 } }
    }

    val ruleMatchPossibilities = mutableMapOf<Int, MutableSet<String>>()
    for ((name, r1, r2) in rules) {
        for (i in yourTicket.indices) {
            if (validTickets.all { values -> values[i] in r1 || values[i] in r2 }) {
                val matches =
                    ruleMatchPossibilities[i] ?: mutableSetOf<String>().also { ruleMatchPossibilities[i] = it }
                matches += name
            }
        }
    }

    println(ruleMatchPossibilities)

    val ruleMatches = mutableMapOf<String, Int>()
    while (ruleMatchPossibilities.isNotEmpty()) {
        val (index, name) = ruleMatchPossibilities.entries.first { (_, names) -> names.size == 1 }

        ruleMatchPossibilities.remove(index)
        ruleMatches[name.single()] = index

        for (names in ruleMatchPossibilities.values) {
            names.remove(name.single())
        }
    }

    val part2 = ruleMatches.entries.mapNotNull { (name, i) ->
        if (name.startsWith("departure")) yourTicket[i].toLong()
        else null
    }.reduceOrNull { acc, i -> acc * i }

    println("part2=$part2")
}
