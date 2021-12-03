fun main() {
    val sample = """
        389125467
    """.trimIndent()

//    val input = sample.trim()
    val input = readResourceText("input.txt").trim()

    val cups = input.map { it.toString().toInt() }.map { Node(it) }.toMutableList()
    var i = cups.maxByOrNull { it.value }!!.value
    while (cups.size < 1_000_000) {
        cups.add(Node(++i))
    }

    // Create linking between cups
    var prev = cups.last()
    for (cup in cups) {
        prev.next = cup
        prev = cup
    }

    // Create map of all cups
    val map = cups.associateBy { it.value }

    val max = 1_000_000

    var current = cups.first()
    repeat(10_000_000) {
        val first = current.next
        val second = first.next
        val third = second.next

        var destinationValue = current.value - 1
        while (
            destinationValue < 1 ||
            destinationValue == first.value ||
            destinationValue == second.value ||
            destinationValue == third.value
        ) {
            destinationValue = if (destinationValue <= 1) max else destinationValue - 1
        }

        val destination = map[destinationValue]!!

        // Remove 3
        current.next = third.next

        // Add 3
        third.next = destination.next
        destination.next = first

        current = current.next
    }

    val one = map[1]!!

    val part2 = one.next.value * one.next.next.value
    println("part2=$part2")
}

class Node<T>(val value: T) {
    var next: Node<T> = this
}
