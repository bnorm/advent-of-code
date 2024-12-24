package aoc.day23

import aoc.input.downloadInput

const val SAMPLE1 = """
kh-tc
qp-kh
de-cg
ka-co
yn-aq
qp-ub
cg-tb
vc-aq
tb-ka
wh-tc
yn-cg
kh-ub
ta-co
de-co
tc-td
tb-wq
wh-td
ta-ka
td-qp
aq-cg
wq-ub
ub-vc
de-ta
wq-aq
wq-vc
wh-yn
ka-de
kh-ta
co-tc
wh-qp
tb-vc
td-yn
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 23)

    val part1 = part1(SAMPLE1)
    require(part1 == "7") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "co,de,ka,ta") { part2 }
    println(part2(input))
}

private fun part1(input: String): String {
    val nodes = buildNodes(input)
    val tNodes = nodes.keys.filter { it.startsWith('t') }

    val parties = mutableSetOf<Set<String>>()
    for (n1 in tNodes) {
        val n1Links = nodes[n1] ?: continue
        for (n2 in n1Links) {
            val n2Links = nodes[n2] ?: continue
            val shared = n1Links.intersect(n2Links)
            for (n3 in shared) {
                parties.add(setOf(n1, n2, n3))
            }
        }
    }

    return parties.size.toString()
}

private fun part2(input: String): String {
    val nodes = buildNodes(input)

    var largest = emptySet<String>()
    for (n1 in nodes.keys) {
        val party = findBiggest(nodes, n1)
        if (party.size > largest.size) largest = party
    }

    return largest.sorted().joinToString(separator = ",")
}

private fun findBiggest(nodes: Map<String, Set<String>>, n1: String): Set<String> {
    val others = (nodes[n1] ?: return setOf(n1)).toList()

    fun recurse(party: Set<String>, index: Int): Set<String> {
        val found = mutableSetOf<String>()
        found.addAll(party)

        var max = party
        for (i in index..<others.size) {
            // Check if node was already found in a party.
            val n = others[i]
            if (n in found) continue

            // Check that node is linked to all others in party.
            val links = nodes[n] ?: continue
            if (party.any { it !in links }) continue

            val next = recurse(party + n, i + 1)
            if (next.size > max.size) max = next
            found.addAll(next)
        }
        return max
    }

    return recurse(setOf(n1), 0)
}

private fun buildNodes(input: String): Map<String, Set<String>> {
    return buildMap<String, MutableSet<String>> {
        val links = input.trim().lines().map { it.split('-') }
        for ((n1, n2) in links) {
            getOrPut(n1) { mutableSetOf() }.add(n2)
            getOrPut(n2) { mutableSetOf() }.add(n1)
        }
    }
}
