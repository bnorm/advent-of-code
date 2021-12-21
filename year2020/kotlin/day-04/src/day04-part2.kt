fun main() {
    val sample = """
        eyr:1972 cid:100
        hcl:#18171d ecl:amb hgt:170 pid:186cm iyr:2018 byr:1926
        
        iyr:2019
        hcl:#602927 eyr:1967 hgt:170cm
        ecl:grn pid:012533040 byr:1946
        
        hcl:dab227 iyr:2012
        ecl:brn hgt:182cm pid:021572410 eyr:2020 byr:1992 cid:277
        
        hgt:59cm ecl:zzz
        eyr:2038 hcl:74454a iyr:2023
        pid:3556412378 byr:2007
    """.trimIndent()

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
                .map { value ->
                    val split = value.split(":")
                    split[0] to split[1]
                }
                .toSet()
        }
        .filter { valid(it) }
        .count()

    println(valid)
}

private val required = listOf(
    "byr",
    "iyr",
    "eyr",
    "hgt",
    "hcl",
    "ecl",
    "pid",
)

private val eyeColors = setOf(
    "amb",
    "blu",
    "brn",
    "gry",
    "grn",
    "hzl",
    "oth"
)

private fun valid(passport: Set<Pair<String, String>>): Boolean {
    if (!passport.map { (key, _) -> key }.containsAll(required)) {
        println("not all required")
        return false
    }

    /*
        byr (Birth Year) - four digits; at least 1920 and at most 2002.
        iyr (Issue Year) - four digits; at least 2010 and at most 2020.
        eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
        hgt (Height) - a number followed by either cm or in:
            If cm, the number must be at least 150 and at most 193.
            If in, the number must be at least 59 and at most 76.
        hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
        ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
        pid (Passport ID) - a nine-digit number, including leading zeroes.
        cid (Country ID) - ignored, missing or not.
     */

    for ((key, value) in passport) {
        val valid = when (key) {
            "byr" -> {
                val year = value.toInt()
                year in 1920..2002
            }
            "iyr" -> {
                val year = value.toInt()
                year in 2010..2020
            }
            "eyr" -> {
                val year = value.toInt()
                year in 2020..2030
            }
            "hgt" -> {
                val (height, unit) = "(\\d+)(cm|in)".toRegex().matchEntire(value)?.destructured ?: run {
                    println(value)
                    return false
                }
                if (unit == "cm") {
                    height.toInt() in 150..193
                } else if (unit == "in") {
                    height.toInt() in 59..76
                } else {
                    false
                }
            }
            "hcl" -> {
                value.matches("#[a-f0-9]{6}".toRegex())
            }
            "ecl" -> {
                value in eyeColors
            }
            "pid" -> {
                value.matches("[0-9]{9}".toRegex())
            }
            else -> true
        }

        if (!valid) {
            println("$key = $value")
            return false
        }
    }
    return true
}
