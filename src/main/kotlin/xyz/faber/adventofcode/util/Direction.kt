package xyz.faber.adventofcode.util

enum class Direction {
    N, E, S, W;

    fun opposite(): Direction {
        return when (this) {
            N -> S
            S -> N
            E -> W
            W -> E
        }
    }

    fun turn(dir: String): Direction {
        return when (dir) {
            "L" -> turnLeft()
            "R" -> turnRight()
            else -> throw IllegalArgumentException(dir)
        }
    }

    fun turnRight(): Direction {
        return when (this) {
            N -> E
            S -> W
            E -> S
            W -> N
        }
    }

    fun turnLeft(): Direction {
        return when (this) {
            N -> W
            S -> E
            E -> N
            W -> S
        }
    }

    fun toChar(): Char {
        return when (this) {
            N -> '^'
            S -> 'v'
            E -> '>'
            W -> '<'
        }
    }
}

fun String.toDirection(): Direction {
    return when (this) {
        "N", "U" -> Direction.N
        "E", "R" -> Direction.E
        "S", "D" -> Direction.S
        "W", "L" -> Direction.W
        else -> throw IllegalArgumentException("Unknown direction: $this")
    }
}

fun Pair<Pos, Pos>.toDirection(): Direction {
    return (this.second - this.first).toDirection()
}

fun Pos.toDirection(): Direction {
    return if (this.x == 1 && this.y == 0) {
        Direction.E
    } else if (this.x == -1 && this.y == 0) {
        Direction.W
    } else if (this.y == 1 && this.x == 0) {
        Direction.S
    } else if (this.y == -1 && this.x == 0) {
        Direction.N
    } else {
        throw IllegalArgumentException("Bad direction $this")
    }
}