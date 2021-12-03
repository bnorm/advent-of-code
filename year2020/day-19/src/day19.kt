fun main() {
    val sample = """
        42: 9 14 | 10 1
        9: 14 27 | 1 26
        10: 23 14 | 28 1
        1: "a"
        11: 42 31
        5: 1 14 | 15 1
        19: 14 1 | 14 14
        12: 24 14 | 19 1
        16: 15 1 | 14 14
        31: 14 17 | 1 13
        6: 14 14 | 1 14
        2: 1 24 | 14 4
        0: 8 11
        13: 14 3 | 1 12
        15: 1 | 14
        17: 14 2 | 1 7
        23: 25 1 | 22 14
        28: 16 1
        4: 1 1
        20: 14 14 | 1 15
        3: 5 14 | 16 1
        27: 1 6 | 14 18
        14: "b"
        21: 14 1 | 1 14
        25: 1 1 | 1 14
        22: 14 14
        8: 42
        26: 14 22 | 1 20
        18: 15 15
        7: 14 5 | 1 21
        24: 14 1

        abbbbbabbbaaaababbaabbbbabababbbabbbbbbabaaaa
        bbabbbbaabaabba
        babbbbaabbbbbabbbbbbaabaaabaaa
        aaabbbbbbaaaabaababaabababbabaaabbababababaaa
        bbbbbbbaaaabbbbaaabbabaaa
        bbbababbbbaaaaaaaabbababaaababaabab
        ababaaaaaabaaab
        ababaaaaabbbaba
        baabbaaaabbaaaababbaababb
        abbbbabbbbaaaababbbbbbaaaababb
        aaaaabbaabaaaaababaa
        aaaabbaaaabbaaa
        aaaabbaabbaaaaaaabbbabbbaaabbaabaaa
        babaaabbbaaabaababbaabababaaab
        aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba
    """.trimIndent()

//    val input = sample.trim().splitToSequence("\n\n")
    val input = readResourceText("input.txt").trim().splitToSequence("\n\n")

    val (ruleLines, pictureLines) = input.toList()

    val rules = ruleLines.splitToSequence("\n")
        .map {
            val (number, value) = it.split(": ")
            number.toInt() to value
        }.toMap()

    val part1 = pictureLines.splitToSequence("\n")
        .filter { picture -> matches(picture, rules).any { it == picture.length}  }
//        .onEach { println("match=$it") }
        .count()

    println("part1=$part1")

    run {
        val newRules = rules.toMutableMap()
        newRules[8] = "42 | 42 8"
        newRules[11] = "42 31 | 42 11 31"

        val part2 = pictureLines.splitToSequence("\n")
            .filter { picture -> matches(picture, newRules).any { it == picture.length}  }
//            .onEach { println("match=$it") }
            .count()

        println("part2=$part2")
    }
}

fun matches(picture: String, rules: Map<Int, String>, pictureIndex: Int = 0, ruleIndex: Int = 0): List<Int> {
    if (pictureIndex >= picture.length) {
//        println("p($pictureIndex)= r($ruleIndex)=${rules[ruleIndex]}")
        return emptyList()
    }

    if (pictureIndex < 0) {
//        println("p($pictureIndex)= r($ruleIndex)=${rules[ruleIndex]}")
        return emptyList()
    }

//    println("p($pictureIndex)=${picture.substring(pictureIndex)} r($ruleIndex)=${rules[ruleIndex]}")

    val rule = rules[ruleIndex]!!
    if (rule.startsWith("\"")) {
        val match = rule.substring(1 until rule.lastIndex)
        for (i in match.indices) {
            if (pictureIndex + i >= picture.length || picture[pictureIndex + i] != match[i]) {
                return emptyList()
            }
        }
        return listOf(pictureIndex + match.length)
    } else {
        val indexes = mutableListOf<Int>()
        for (subRule in rule.split("|")) {
            var index = listOf(pictureIndex)
            for (r in subRule.trim().split(" ").map { it.toInt() }) {
                index = index.flatMap { matches(picture, rules, it, r) }
            }
            indexes.addAll(index)
        }
        return indexes
    }
}
