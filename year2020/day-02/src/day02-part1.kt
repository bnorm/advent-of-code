fun main() {
//    val sample = """
//        1-3 a: abcde
//        1-3 b: cdefg
//        2-9 c: ccccccccc
//    """.trimIndent()

    val passwordPattern = "(\\d+)-(\\d+) (.): (.*)".toRegex()
    val passwords = readResourceText("input.txt").splitToSequence("\n")
    val parsed = passwords.mapNotNull { passwordPattern.matchEntire(it)?.destructured }

    val total = passwords.count()
    val invalid = parsed
            .filter { (min, max, charString, password) ->
                !validatePassword(password, charString[0], min.toInt(), max.toInt())
            }
            .count()
    val valid = parsed
            .filter { (min, max, charString, password) ->
                validatePassword(password, charString[0], min.toInt(), max.toInt())
            }
            .count()
    println("total = ${total}")
    println("valid = ${valid}")
    println("invalid = ${invalid}")
}

private fun validatePassword(password: String, char: Char, min: Int, max: Int) =
        password.count { it == char } in min..max
