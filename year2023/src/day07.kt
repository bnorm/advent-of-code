package day07

import utils.*

fun main() {
    val sample1 = readInput("day07.sample1.txt")
    val sample2 = readInput("day07.sample2.txt")
    val input = readInput("day07.txt")

    require(part1(sample1) == "6440")
    println(part1(input))

    require(part2(sample2) == "5905")
    println(part2(input))
}

fun part1(input: List<String>): String {
    val labels = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
        .reversed().mapIndexed { index, c -> c to index }.toMap()
    val comparator = compareBy(Hand::rank)
        .thenComparator { a, b -> compareEachBy(a.cards, b.cards) { labels.getValue(it) } }

    val hands = input.map { Hand.parse(it, joker = false) }
    val ordered = hands.sortedWith(comparator)
    val winnings = ordered.mapIndexed { index, hand -> hand.bet * (index + 1) }
    return winnings.sum().toString()
}

fun part2(input: List<String>): String {
    val labels = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')
        .reversed().mapIndexed { index, c -> c to index }.toMap()
    val comparator = compareBy(Hand::rank)
        .thenComparator { a, b -> compareEachBy(a.cards, b.cards) { labels.getValue(it) } }

    val hands = input.map { Hand.parse(it, joker = true) }
    val ordered = hands.sortedWith(comparator)
    val winnings = ordered.mapIndexed { index, hand -> hand.bet * (index + 1) }
    return winnings.sum().toString()
}

enum class Rank {
    HighCard {
        override fun matches(counts: List<Map.Entry<Char, Int>>): Boolean =
            counts[0].value == 1
    },

    OnePair {
        override fun matches(counts: List<Map.Entry<Char, Int>>): Boolean =
            counts[0].value == 2 && counts[1].value < 2
    },

    TwoPair {
        override fun matches(counts: List<Map.Entry<Char, Int>>): Boolean =
            counts[0].value == 2 && counts[1].value == 2
    },

    ThreeOfKind {
        override fun matches(counts: List<Map.Entry<Char, Int>>): Boolean =
            counts[0].value == 3 && counts[1].value < 2
    },

    FullHouse {
        override fun matches(counts: List<Map.Entry<Char, Int>>): Boolean =
            counts[0].value == 3 && counts[1].value == 2
    },

    FourOfKind {
        override fun matches(counts: List<Map.Entry<Char, Int>>): Boolean =
            counts[0].value == 4
    },

    FiveOfKind {
        override fun matches(counts: List<Map.Entry<Char, Int>>): Boolean =
            counts[0].value == 5
    },

    ;

    companion object {
        fun toRank(cards: List<Char>, joker: Boolean): Rank {
            require(cards.size == 5)
            val counts = cards.groupingBy { it }.eachCount().toMutableMap()
            if (joker && 'J' in counts && counts.size > 1) {
                val count = counts.remove('J')!!
                val max = counts.entries.maxBy { it.value }
                max.setValue(max.value + count)
            }
            val sorted = counts.entries.sortedByDescending { it.value }
            return Rank.entries.first { it.matches(sorted) }
        }
    }

    protected abstract fun matches(counts: List<Map.Entry<Char, Int>>): Boolean
}

class Hand(
    val cards: List<Char>,
    val rank: Rank,
    val bet: Long,
) {
    init {
        require(cards.size == 5)
    }

    companion object {
        fun parse(line: String, joker: Boolean): Hand {
            val split = line.split(" ")
            val cards = split[0].toList()
            return Hand(cards, Rank.toRank(cards, joker), split[1].toLong())
        }
    }
}
