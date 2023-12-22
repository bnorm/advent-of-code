package aoc.day22

import utils.*

fun main() {
    val input = readInput("aoc/day22/input.txt")
    val sample1 = readInput("aoc/day22/sample1.txt")
    val sample2 = readInput("aoc/day22/sample2.txt")

    val part1 = part1(sample1)
    require(part1 == "5") { part1 }
    println(part1(input))

    val part2 = part2(sample2)
    require(part2 == "7") { part2 }
    println(part2(input))
}

fun part1(input: List<String>): String {
    val cubes = fall(input.mapIndexed { i, line -> parseCube(i.toLabel(), line) })
    buildSupports(cubes)

    var result = 0
    for (cube in cubes) {
        if (cube.children.all { it.parents.size > 1 }) result++
    }

    return result.toString()
}

fun part2(input: List<String>): String {
    val cubes = fall(input.mapIndexed { i, line -> parseCube(i.toLabel(), line) })
    buildSupports(cubes)

    var result = 0L
    for (c in cubes) {
        val tmp = countFalls(c)
        result += tmp
    }
    return result.toString()
}

data class Cube(
    val label: String,
    val xSpan: LongRange,
    val ySpan: LongRange,
    val zSpan: LongRange,
) {
    val children = mutableSetOf<Cube>()
    val parents = mutableSetOf<Cube>()

    override fun toString(): String {
        return "$label : ${xSpan.first},${ySpan.first},${zSpan.first}~${xSpan.last},${ySpan.last},${zSpan.last}"
    }
}

private fun Int.toLabel(): String {
    return when (this) {
        in 0..25 -> ('A' + this).toString()
        else -> (this / 26).toLabel() + (this % 26).toLabel()
    }
}

private fun countFalls(
    cube: Cube,
): Int {
    val disintegrated = mutableSetOf<Cube>()
    disintegrated.add(cube)

    val queue = ArrayDeque<Cube>()
    queue.addAll(cube.children)

    while (queue.isNotEmpty()) {
        val c = queue.removeFirst()
        if ((c.parents - disintegrated).isEmpty()) {
            disintegrated.add(c)
            queue.addAll(c.children)
        }
    }

    return disintegrated.size - 1
}

private fun parseCube(label: String, line: String): Cube {
    val (first, last) = line.split("~")
    val (firstX, firstY, firstZ) = first.split(",")
    val (lastX, lastY, lastZ) = last.split(",")
    return Cube(
        label = label,
        xSpan = firstX.toLong()..lastX.toLong(),
        ySpan = firstY.toLong()..lastY.toLong(),
        zSpan = firstZ.toLong()..lastZ.toLong(),
    )
}

private fun fall(cubes: List<Cube>): List<Cube> {
    val fallen = mutableListOf<Cube>()
    for (cube in cubes.sortedBy { it.zSpan.first }) {
        val below = fallen.filter { cube.isAbove(it) }.maxOfOrNull { it.zSpan.last + 1 } ?: 0
        fallen.add(cube.copy(zSpan = below..below + (cube.zSpan.last - cube.zSpan.first)))
    }
    return fallen
}

fun buildSupports(cubes: List<Cube>) {
    for (a in cubes) {
        for (b in cubes) {
            if (b.isDirectlyAbove(a)) {
                b.parents += a
                a.children += b
            }
        }
    }
}

fun Cube.isAbove(below: Cube): Boolean {
    if (this === below) return false
    return below.zSpan.last < zSpan.first &&
            ySpan intersects below.ySpan &&
            xSpan intersects below.xSpan
}

fun Cube.isDirectlyAbove(below: Cube): Boolean {
    if (this === below) return false
    return below.zSpan.last == zSpan.first - 1 &&
            ySpan intersects below.ySpan &&
            xSpan intersects below.xSpan
}
