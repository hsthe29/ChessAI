package objects

import java.io.Serializable




enum class MoveList(var row: Int, var col: Int) {
    UP(-1, 0), UP_RIGHT(-1, 1), RIGHT(0, 1), DOWN_RIGHT(1, 1), DOWN(1, 0), DOWN_LEFT(1, -1), LEFT(0, -1),
    UP_LEFT(-1, -1),
    KNIGHT_LEFT_UP(-1, -2), KNIGHT_UP_LEFT(-2, -1), KNIGHT_UP_RIGHT(-2, 1), KNIGHT_RIGHT_UP(-1, 2),
    KNIGHT_RIGHT_DOWN(1, 2), KNIGHT_DOWN_RIGHT(2, 1), KNIGHT_DOWN_LEFT(2, -1), KNIGHT_LEFT_DOWN(1, -2),
    DOUBLE_UP(-2, 0), DOUBLE_DOWN(2, 0);

    fun isEqual(m: MoveList): Boolean {
        return (row == m.row) && (col == m.col)
    }

    fun isEqual(row: Int, col: Int): Boolean {
        return (this.row == row) && (this.col == col)
    }
}

class MoveInfo(var oldRow: Int, var oldCol: Int, var newRow: Int, var newCol: Int) : Serializable {

    override fun toString(): String {
        return getCharLabel(oldRow + 1) + (oldCol + 1) + " to " + getCharLabel(newRow + 1) + (newCol + 1)
    }

    val gapRow: Int
        get() = newRow - oldRow
    val gapCol: Int
        get() = newCol - oldCol

    // Converts x number poisition to character label
    private fun getCharLabel(i: Int)=  if (i in 1..26) (i + 64).toChar().toString() else null
}