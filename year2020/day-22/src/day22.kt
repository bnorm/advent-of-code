fun main() {
    val sample = """
        Player 1:
        9
        2
        6
        3
        1

        Player 2:
        5
        8
        4
        7
        10
    """.trimIndent()

//    val input = sample.trim().splitToSequence("\n\n")
    val input = readResourceText("input.txt").trim().splitToSequence("\n\n")

    val (deck1, deck2) = input.map { block ->
        val lines = block.trim().split("\n")
        lines.subList(1, lines.size).map { it.toInt() }
    }.toList()

    run {
        val winner = playPart1(deck1, deck2)
        val part1 = winner.reversed().mapIndexed { index, i -> (index + 1) * i }.sum()
        println("part1=$part1")
    }

    run {
        val (result1, result2) = playPart2(deck1, deck2)
        val winner = result1.takeIf { it.isNotEmpty() } ?: result2
        val part2 = winner.reversed().mapIndexed { index, i -> (index + 1) * i }.sum()
        println("part2=$part2")
    }
}

private fun playPart1(deck1: List<Int>, deck2: List<Int>): List<Int> {
    val deck1 = ArrayDeque(deck1)
    val deck2 = ArrayDeque(deck2)
    while (deck1.isNotEmpty() && deck2.isNotEmpty()) {
        val card1 = deck1.removeFirst()
        val card2 = deck2.removeFirst()
        if (card1 > card2) {
            deck1.addLast(card1)
            deck1.addLast(card2)
        } else {
            deck2.addLast(card2)
            deck2.addLast(card1)
        }
    }

    return deck1.takeIf { it.isNotEmpty() } ?: deck2
}

private fun playPart2(deck1: List<Int>, deck2: List<Int>): Pair<List<Int>, List<Int>> {
    val deck1 = ArrayDeque(deck1)
    val deck2 = ArrayDeque(deck2)

    val memory = mutableListOf<Pair<List<Int>, List<Int>>>()
    while (deck1.isNotEmpty() && deck2.isNotEmpty()) {
        val snapshot = deck1.toList() to deck2.toList()
        if (snapshot in memory) {
            return deck1 to emptyList() // fake a player 1 win
        }
        memory.add(snapshot)

        val card1 = deck1.removeFirst()
        val card2 = deck2.removeFirst()

        val winner1 = when {
            deck1.size >= card1 && deck2.size >= card2 -> {
                val (result1, _) = playPart2(deck1.subList(0, card1), deck2.subList(0, card2))
                result1.isNotEmpty()
            }
            else -> {
                card1 > card2
            }
        }
        if (winner1) {
            deck1.addLast(card1)
            deck1.addLast(card2)
        } else {
            deck2.addLast(card2)
            deck2.addLast(card1)
        }
    }

    return deck1 to deck2
}
