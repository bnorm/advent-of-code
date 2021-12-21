fun main() {
    val sample = """
        light red bags contain 1 bright white bag, 2 muted yellow bags.
        dark orange bags contain 3 bright white bags, 4 muted yellow bags.
        bright white bags contain 1 shiny gold bag.
        muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
        shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
        dark olive bags contain 3 faded blue bags, 4 dotted black bags.
        vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
        faded blue bags contain no other bags.
        dotted black bags contain no other bags.
    """.trimIndent()

//    val input = sample.trim().splitToSequence("\n")
    val input = readResourceText("input.txt").trim().splitToSequence("\n")

    val bags = input.map { line ->
        val (color, subBags) = line.extract("(.*?) bags contain (.*?)\\.")!!
        val bags = if ("no other bags" !in subBags) {
            subBags.split(", ").map { subBag ->
                val (count, subBagColor) = subBag.extract("(\\d+) (.*?) bag[s]?")!!
                subBagColor to count.toInt()
            }.toMap()
        } else emptyMap()
        color to bags
    }.toMap()

    fun addParentBags(targetColor: String): Set<String> {
        val holdingBags = mutableSetOf<String>()
        for ((color, subBags) in bags) {
            if (targetColor in subBags.keys) {
                holdingBags.add(color)
                holdingBags.addAll(addParentBags(color))
            }
        }
        return holdingBags
    }
    println(addParentBags("shiny gold").size)

    fun countChildren(targetColor: String): Int {
        val subBags = bags[targetColor]!!
        return subBags.values.sum() +
                subBags.entries.sumBy { (color, count) ->
                    count * countChildren(color)
                }
    }
    println(countChildren("shiny gold"))
}
