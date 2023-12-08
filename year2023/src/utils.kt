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

fun lcm(a: Long, b: Long): Long {
    val step = maxOf(a, b)
    val maxLcm = a * b
    var lcm = step
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) return lcm
        lcm += step
    }
    return maxLcm
}

fun <T> compareEachBy(a: List<T>, b: List<T>, selector: (T) -> Comparable<*>?): Int {
    for (i in 0..<minOf(a.size, b.size)) {
        compareValuesBy(a[i], b[i], selector).let { if (it != 0) return it }
    }
    return compareValues(a.size, b.size)
}
