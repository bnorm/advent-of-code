package utils.grid2d

data class Vector(val point: Point, val direction: Direction)

// ===== DIRECTION ===== //

fun Vector.move(): Vector {
    return copy(point = point.move(direction))
}

fun Vector.turnRight(): Vector {
    return copy(direction = direction.turnRight())
}

fun Vector.turnLeft(): Vector {
    return copy(direction = direction.turnLeft())
}

// ===== GRID ===== //

operator fun <T> Grid<T>.contains(vector: Vector): Boolean = vector.point in this
operator fun <T> Grid<T>.get(vector: Vector): T = get(vector.point)
operator fun <T> MutableGrid<T>.set(vector: Vector, value: T) = set(vector.point, value)
fun <T> Grid<T>.ref(vector: Vector) = ref(vector.point)
fun <T> MutableGrid<T>.ref(vector: Vector) = ref(vector.point)
