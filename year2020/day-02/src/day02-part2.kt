fun main() {
//    val passwords = """
//        1-3 a: abcde
//        1-3 b: cdefg
//        2-9 c: ccccccccc
//    """.trimIndent().splitToSequence("\n")

    val passwordPattern = "(\\d+)-(\\d+) (.): (.*)".toRegex()
    val passwords = readResourceText("input.txt").splitToSequence("\n")

    val total = passwords.count()
    val parsed = passwords.mapNotNull { passwordPattern.matchEntire(it)?.destructured }
    val invalid = parsed
            .filter { (index1, index2, charString, password) ->
                !validatePassword(password, charString[0], index1.toInt(), index2.toInt())
            }
            .count()
    val valid = parsed
            .filter { (index1, index2, charString, password) ->
                validatePassword(password, charString[0], index1.toInt(), index2.toInt())
            }
            .count()
    println("total = ${total}")
    println("valid = ${valid}")
    println("invalid = ${invalid}")
}

private fun validatePassword(password: String, char: Char, index1: Int, index2: Int): Boolean {
    val check1 = password.length >= index1 && password[index1 - 1] == char
    val check2 = password.length >= index2 && password[index2 - 1] == char
    return check1 xor check2
}
