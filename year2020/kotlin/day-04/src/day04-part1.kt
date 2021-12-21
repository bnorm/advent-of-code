fun main() {
    val sample = """
        ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
        byr:1937 iyr:2017 cid:147 hgt:183cm

        iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884
        hcl:#cfa07d byr:1929

        hcl:#ae17e1 iyr:2013
        eyr:2024
        ecl:brn pid:760753108 byr:1931
        hgt:179cm

        hcl:#cfa07d eyr:2025 pid:166559648
        iyr:2011 ecl:brn hgt:59in
    """.trimIndent()

    val required = listOf(
        "byr",
        "iyr",
        "eyr",
        "hgt",
        "hcl",
        "ecl",
        "pid",
    )

//    val input = sample.splitToSequence("\n")
    val input = readResourceText("input.txt").splitToSequence("\n")

    val passports = mutableListOf<String>()
    var current: String? = null
    for (value in input) {
        if (value.isBlank() && current != null) {
            passports.add(current.trim())
            current = null
        } else {
            current = (current ?: "") + " " + value
        }
    }
    if (current != null) {
        passports.add(current.trim())
    }

    val valid = passports
        .map { passport ->
            passport.split(" ")
                .map { value -> value.split(":")[0] }
                .toSet()
        }
        .filter { it.containsAll(required) }
        .count()

    println(valid)
}
