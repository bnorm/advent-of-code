import java.util.*

fun main() {
    val sample = """
        389125467
    """.trimIndent()

//    val input = sample.trim()
    val input = readResourceText("input.txt").trim()

    val cups = LinkedList(input.toList().map { it.toString().toInt() })

    repeat(100) {
        val current = cups.removeFirst()
        cups.addLast(current) // move to the end

        val three = listOf(cups.removeFirst(), cups.removeFirst(), cups.removeFirst())
        val destination = generateSequence(current - 1) { if (it <= 1) 9 else it - 1 }
            .first { it > 0 && it !in three }

        val iter = cups.listIterator(cups.size)
        while (iter.hasPrevious() && iter.previous() != destination) {
        }
        iter.next()
        for (c in three) {
            iter.add(c)
        }
    }

    while (true) {
        val current = cups.removeFirst()
        if (current == 1) break
        cups.addLast(current)
    }

    val part1 = cups.joinToString("")
    println("part1=$part1")
}
