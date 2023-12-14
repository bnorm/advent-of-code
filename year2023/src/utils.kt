@file:Suppress("NOTHING_TO_INLINE")

package utils

import java.nio.file.NoSuchFileException
import kotlin.io.path.readLines
import kotlin.io.path.toPath

fun readInput(fileName: String): List<String> {
    val resource = ClassLoader.getSystemResource(fileName) ?: throw NoSuchFileException(fileName)
    return resource.toURI().toPath().readLines()
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
