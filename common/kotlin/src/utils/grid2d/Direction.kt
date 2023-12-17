package utils.grid2d

enum class Direction {
    North,
    East,
    South,
    West,
    ;
}

fun Direction.turnRight(): Direction {
    return when (this) {
        Direction.North -> Direction.East
        Direction.East -> Direction.South
        Direction.South -> Direction.West
        Direction.West -> Direction.North
    }
}

fun Direction.turnLeft(): Direction {
    return when (this) {
        Direction.North -> Direction.West
        Direction.East -> Direction.North
        Direction.South -> Direction.East
        Direction.West -> Direction.South
    }
}
