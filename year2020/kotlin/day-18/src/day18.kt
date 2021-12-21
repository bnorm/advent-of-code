fun main() {
    val sample = """
        1 + (2 * 3) + (4 * (5 + 6))
        2 * 3 + (4 * 5)
        5 + (8 * 3 + 9 + 3 * 4 * 3)
        5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))
        ((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2
    """.trimIndent()

//    val input = sample.trim().splitToSequence("\n")
    val input = readResourceText("input.txt").trim().splitToSequence("\n")

    val part1 = input.map {
        calc(tokenize(it))
    }.sum()
    println("part1=$part1")

    val part2 = input.map {
        calcAdv(tokenize(it))
    }.sum()
    println("part2=$part2")
}

fun tokenize(line: String) = iterator<String> {
    var i = 0
    while (i < line.length) {
        when (val c = line[i]) {
            ' ' -> {
                i++
            }
            '(', ')', '*', '+' -> {
                yield(c.toString())
                i++
            }
            in '0'..'9' -> {
                val start = i
                while (i < line.length && line[i] in '0'..'9') i++
                yield(line.substring(start, i))
            }
        }
    }
}

fun calc(tokens: Iterator<String>): Long {
    var value = 0L
    var op = "+"

    fun op(next: Long) {
        when (op) {
            "+" -> value += next
            "*" -> value *= next
            else -> TODO("op=\"$op\"")
        }
    }

    while (tokens.hasNext()) {
        when (val token = tokens.next()) {
            ")" -> return value
            "(" -> op(calc(tokens))
            "*", "+" -> op = token
            else -> op(token.toLongOrNull() ?: TODO("token=\"$token\""))
        }
    }
    return value
}

fun calcAdv(tokens: Iterator<String>): Long {
    val values = ArrayDeque<Long>()
    var value = 0L
    var op = "+"

    fun op(next: Long) {
        when (op) {
            "+" -> value += next
            "*" -> {
                values.add(value)
                value = next
            }
            else -> TODO("op=\"$op\"")
        }
    }

    fun finalize(): Long {
        if (value != 0L) values.add(value)
        return values.reduce { acc, n -> acc * n }
    }

    while (tokens.hasNext()) {
        when (val token = tokens.next()) {
            ")" -> return finalize()
            "(" -> op(calcAdv(tokens))
            "*", "+" -> op = token
            else -> op(token.toLongOrNull() ?: TODO("token=\"$token\""))
        }
    }

    return finalize()
}
