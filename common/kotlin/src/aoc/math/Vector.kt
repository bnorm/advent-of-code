package aoc.math

interface Vector<T> : Iterable<T> {
    val size: Int
    operator fun get(i: Int): T
    operator fun set(i: Int, value: T)
}

abstract class AbstractVector<T> : Vector<T> {
    override fun iterator(): Iterator<T> {
        return VectorIterator(0, this)
    }

    override fun toString(): String {
        return joinToString(prefix = "Vector[", postfix = "]")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vector<*>) return false
        if (size != other.size) return false
        for (n in 0..<size) {
            if (get(n) != other[n]) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 0
        for (n in 0..<size) {
            result = 31 * result + get(n).hashCode()
        }
        return result
    }

    private class VectorIterator<T>(
        private var n: Int = 0,
        private val vector: Vector<T>
    ) : Iterator<T> {
        override fun hasNext(): Boolean = n < vector.size
        override fun next(): T = vector[n++]
    }
}

fun <T> Vector(values: Array<T>): Vector<T> = ArrayVector(values)

@JvmName("VarargVector")
inline fun <reified T> Vector(vararg values: T): Vector<T> {
    return Vector(arrayOf(*values))
}

private class ArrayVector<T>(private val values: Array<T>) : AbstractVector<T>() {
    override val size: Int get() = values.size
    override fun get(i: Int): T = values[i]

    override fun set(i: Int, value: T) {
        values[i] = value
    }

    override fun iterator(): Iterator<T> = values.iterator()
}

operator fun <T> Vector<T>.component1(): T = get(0)
operator fun <T> Vector<T>.component2(): T = get(1)
operator fun <T> Vector<T>.component3(): T = get(2)
operator fun <T> Vector<T>.component4(): T = get(3)
operator fun <T> Vector<T>.component5(): T = get(4)
operator fun <T> Vector<T>.component6(): T = get(5)
operator fun <T> Vector<T>.component7(): T = get(6)
operator fun <T> Vector<T>.component8(): T = get(7)
operator fun <T> Vector<T>.component9(): T = get(8)
operator fun <T> Vector<T>.component10(): T = get(9)
