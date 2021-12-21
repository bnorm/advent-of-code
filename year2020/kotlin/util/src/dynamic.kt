fun <R> dynamic(size: Int, calc: (i: Int, memory: List<R>) -> R): R {
    val memory = mutableListOf<R>()
    for (i in 0 until size) {
        memory.add(calc(i, memory))
    }
    return memory.last()
}
