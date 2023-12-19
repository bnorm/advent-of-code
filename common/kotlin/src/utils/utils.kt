@file:Suppress("NOTHING_TO_INLINE")

package utils

import java.nio.file.NoSuchFileException
import kotlin.io.path.readLines
import kotlin.io.path.toPath

fun readInput(fileName: String): List<String> {
    val resource = ClassLoader.getSystemResource(fileName) ?: throw NoSuchFileException(fileName)
    val lines = resource.toURI().toPath().readLines()
    require(lines.isNotEmpty()) { "Input data required!" }
    return lines
}

fun List<String>.separateBy(predicate: (String) -> Boolean): List<List<String>> {
    val groups = mutableListOf<List<String>>()
    val group = mutableListOf<String>()
    for (line in this) {
        if (predicate(line)) {
            if (group.isNotEmpty()) {
                groups.add(group.toList())
                group.clear()
            }
        } else {
            group.add(line)
        }
    }
    if (group.isNotEmpty()) groups.add(group.toList())
    return groups
}

inline operator fun <T : Any> MutableCollection<in T>.plusAssign(element: T?) {
    if (element != null) this.add(element)
}

fun <T> compareEachBy(a: List<T>, b: List<T>, selector: (T) -> Comparable<*>?): Int {
    for (i in 0..<minOf(a.size, b.size)) {
        compareValuesBy(a[i], b[i], selector).let { if (it != 0) return it }
    }
    return compareValues(a.size, b.size)
}

fun <T> List<T>.toPairs(): List<Pair<T, T>> {
    require(this.size > 1)

    val pairs = mutableListOf<Pair<T, T>>()
    for (i in 0..<this.size - 1) {
        for (j in i + 1..<this.size) {
            pairs.add(this[i] to this[j])
        }
    }

    return pairs
}

fun <T> findCycleSize(items: List<T>): Int? {
    cycleSize@ for (cycleSize in 1..<items.size / 2) {
        var cycleStart = items.size - cycleSize
        val right = items.subList(cycleStart, cycleStart + cycleSize)

        // Repeatedly check to the beginning of the list to avoid local cycles.
        cycleStart -= cycleSize
        while (cycleStart > 0) {
            val left = items.subList(cycleStart, cycleStart + cycleSize)
            if (left != right) continue@cycleSize
            cycleStart -= cycleSize
        }

        return cycleSize
    }
    return null
}
