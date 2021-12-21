inline fun indexPairs(size: Int, block: (i: Int, j: Int) -> Unit) {
    for (i in 0 until size - 1) {
        for (j in 1 until size) {
            block(i, j)
        }
    }
}
