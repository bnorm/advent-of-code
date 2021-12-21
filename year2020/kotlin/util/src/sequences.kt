fun <T> Sequence<T>.chunked(
    predicate: (T) -> Boolean
): Sequence<List<T>> = sequence {
    val group = mutableListOf<T>()
    for (line in this@chunked) {
        group.add(line)
        if (predicate(line)) {
            yield(group.toList())
            group.clear()
        }
    }
    if (group.isNotEmpty()) {
        yield(group)
    }
}

fun <T> Sequence<T>.untilStable(): T =
    windowed(2) { (prev, next) -> next.takeIf { it != prev } }
        .takeWhile { it != null }
        .filterNotNull()
        .last()
