package xyz.faber.adventofcode.util

enum class Turn {
  LEFT, RIGHT, STRAIGHT, REVERSE
}

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

  fun turn(turn: String): Direction {
    return when (turn) {
      "L" -> turnLeft()
      "R" -> turnRight()
      else -> throw IllegalArgumentException(turn)
    }
  }

  fun turn(turn: Turn): Direction {
    return when (turn) {
      Turn.LEFT -> turnLeft()
      Turn.RIGHT -> turnRight()
      Turn.STRAIGHT -> this
      Turn.REVERSE -> reverse()
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

  fun reverse(): Direction {
    return when (this) {
      N -> S
      S -> N
      E -> W
      W -> E
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

fun Pair<Direction, Direction>.toTurn(): Turn {
  return when (this.first) {
    Direction.N -> when (this.second) {
      Direction.W -> Turn.LEFT
      Direction.N -> Turn.STRAIGHT
      Direction.E -> Turn.RIGHT
      Direction.S -> Turn.REVERSE
    }

    Direction.E -> when (this.second) {
      Direction.N -> Turn.LEFT
      Direction.E -> Turn.STRAIGHT
      Direction.S -> Turn.RIGHT
      Direction.W -> Turn.REVERSE
    }

    Direction.S -> when (this.second) {
      Direction.E -> Turn.LEFT
      Direction.S -> Turn.STRAIGHT
      Direction.W -> Turn.RIGHT
      Direction.N -> Turn.REVERSE
    }

    Direction.W -> when (this.second) {
      Direction.S -> Turn.LEFT
      Direction.W -> Turn.STRAIGHT
      Direction.N -> Turn.RIGHT
      Direction.E -> Turn.REVERSE
    }
  }

}
