fun main() {
    val sample = """
        5764801
        17807724
    """.trimIndent()

//    val input = sample.trim().splitToSequence("\n")
    val input = readResourceText("input.txt").trim().splitToSequence("\n")

    val (cardPubKey, doorPubKey) = input.map { it.toLong() }.toList()
    val subjectNumber = 7L

    var cardLoopCount = -1
    var doorLoopCount = -1

    var loop = 0
    var key = 1L
    while (cardLoopCount < 0L || doorLoopCount < 0L) {
        key = (key * subjectNumber) % 20201227L
        loop++

        if (key == cardPubKey) {
            cardLoopCount = loop
        } else if (key == doorPubKey) {
            doorLoopCount = loop
        }
    }

    println("cardLoopCount = ${cardLoopCount}")
    println("doorLoopCount = ${doorLoopCount}")

    var encryptionKey = 1L
    repeat(cardLoopCount) {
        encryptionKey = (encryptionKey * doorPubKey) % 20201227L
    }

    val part1 = encryptionKey
    println("part1 = ${part1}")
}
