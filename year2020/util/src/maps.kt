fun <K, V> Map<K, Iterable<V>>.unique(): Map<K, V> {
    val upstream = this.mapValuesTo(mutableMapOf()) { (_, v) -> v.toMutableSet() }
    val result = mutableMapOf<K, V>()
    while (isNotEmpty()) {
        val (key, values) = upstream.entries.first { (_, v) -> v.size == 1 }
        val value = values.single()
        result[key] = value

        upstream.remove(key)
        upstream.entries.forEach { (_, v) -> v.remove(value) }
    }
    return result
}
