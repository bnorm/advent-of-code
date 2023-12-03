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
