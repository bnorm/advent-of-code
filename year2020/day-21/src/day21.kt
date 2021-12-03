fun main() {
    val sample = """
        mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
        trh fvjkl sbzzf mxmxvkd (contains dairy)
        sqjhc fvjkl (contains soy)
        sqjhc mxmxvkd sbzzf (contains fish)
    """.trimIndent()

//    val input = sample.trim().splitToSequence("\n")
    val input = readResourceText("input.txt").trim().splitToSequence("\n")

    val food = input.map { line ->
        val (ingredients, allergens) = line.extract("([a-z ]+) \\(contains ([a-z ,]+)\\)") ?: error(line)
        ingredients.split(" ") to allergens.split(", ")
    }

    val allergens = food.flatMap { (ingredients, allergens) -> allergens.map { it to ingredients.toMutableSet() } }
        .groupingBy { it.first }
        .aggregateTo(mutableMapOf()) { _, accumulator: MutableSet<String>?, (_, ingredients), _ ->
            accumulator?.also { it.retainAll(ingredients) } ?: ingredients
        }
        .unique()

    val allergenicIngredients = allergens.values.toSet()

    val part1 = food.flatMap { (ingredients, _) -> ingredients }.count { it !in allergenicIngredients }
    println("part1=$part1")

    val part2 = allergens.entries.sortedBy { (k) -> k }.joinToString(",") { (_, v) -> v }
    println("part2=$part2")
}
