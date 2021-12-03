import intcode.Program

fun main() {
    val program = readResourceText("input-day25.txt")
        .trim().splitToSequence("\n", ",")
        .map { it.toLong() }.toList()

    val output = StringBuilder()
    val init = """
                east
                take weather machine
                west
                west
                west
                take bowl of rice
                east
                north
                take polygon
                east
                take hypercube
                south
                take dark matter
                north
                west
                north
                take candy cane
                west
                west
                north
                take dehydrated water
                west
            """.trimIndent()
    val input = iterator<Long> {
        yieldAll(init.map { it.toLong() })
        yield('\n'.toLong())
        output.clear()

        val items = listOf(
            "weather machine",
            "bowl of rice",
            "polygon",
            "hypercube",
            "dark matter",
            "candy cane",
            "dehydrated water",
        )
        val permutations = items.permutations().iterator()
        var success = false
        while (!success) {
            for (item in items) {
                yieldAll("drop $item\n".map { it.toLong() })
            }
            val attempt = permutations.next()
            for (item in attempt) {
                yieldAll("take $item\n".map { it.toLong() })
            }
            yieldAll("south\n".map { it.toLong() })

            println("attempt=$attempt")
            if ("Droids on this ship are lighter than the detected value" in output) {
                println("HEAVY")
            } else if ("Droids on this ship are heavier than the detected value!" in output) {
                println("LIGHT")
            } else {
                println("CORRECT")
                success = true
            }
            output.clear()
        }

        while (true) yield(read().toLong())
    }

    Program(
        program,
        input = { input.next() },
        output = {
            print(it.toChar())
            output.append(it.toChar())
        }
    ).run()
}

private fun <T> List<T>.permutations(): Sequence<List<T>> {
    return sequence {
        val list = this@permutations
        if (list.size >= 2) {
            val first = list[0]
            val subList = list.subList(1, list.size)
            val subPerms = subList.permutations().toList()
            yield(listOf(first))
            yieldAll(subPerms.map {
                mutableListOf<T>().apply {
                    add(first)
                    addAll(it)
                }
            })
            yieldAll(subPerms)
        } else if (list.size == 1) {
            yield(list)
        }
    }
}

private fun read() = System.`in`.read()

/*
HEAVY
- manifold
- weather machine
- dehydrated water
- polygon
- bowl of rice
- hypercube
- candy cane
- dark matter

HEAVY
- weather machine
- dehydrated water
- polygon
- bowl of rice
- hypercube
- candy cane
- dark matter

HEAVY
- dehydrated water
- polygon
- bowl of rice
- hypercube
- candy cane
- dark matter

HEAVY
- dehydrated water
- polygon
- bowl of rice
- candy cane
- dark matter

HEAVY
- polygon
- bowl of rice
- candy cane
- dark matter

LIGHT
- bowl of rice
- candy cane
- dark matter

HEAVY
- manifold
- bowl of rice
- candy cane
- dark matter

HEAVY
- manifold
- candy cane
- dark matter

LIGHT
- manifold
- dark matter

LIGHT
- manifold
- dark matter
- bowl of rice

HEAVY
- manifold
- dark matter
- bowl of rice
- candy cane
 */
