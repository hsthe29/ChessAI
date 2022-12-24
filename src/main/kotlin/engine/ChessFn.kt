package engine

import javafx.scene.image.Image
import Move

enum class GameMode {
    AI_MODE, PVP, AI_FULL
}

data class PieceInfo(var player: Boolean, var pieceName: String, var mode: Int)

data class CellPos(var rowId: Int, var columnId: Int)

data class CastleInfo(var leftWhite: Boolean, var rightWhite: Boolean, var leftBlack: Boolean, var rightBlack: Boolean)

data class Triple<A, B, C>(val first: A, val second: B, val third: C)

data class Entry<A, B>(var lower: A, var upper: B)
data class CPair<A, B>(var first: A, var second: B) {
    override operator fun equals(other: Any?) = if(other == null) {
            false
        } else if(other is CPair<*, *>) {
            (other.first == first)&&(other.second == second)
        } else false
    override fun hashCode(): Int {
        var result = first?.hashCode() ?: 0
        result = 31 * result + (second?.hashCode() ?: 0)
        return result
    }

    fun set(first: A, second: B) {
        this.first = first
        this.second = second
    }

    override fun toString(): String {
        return "($first, $second)"
    }
}
fun <T, V> pairOf(first: T, second: V) = CPair(first, second)

val darkBackground = Image("backgrounds/dark_color.png")
val lightBackground = Image("backgrounds/light_color.png")
val movableBackground = Image("backgrounds/movable.png")
val underAttackedBackground = Image("backgrounds/under_attacked.png")
val traceBackground = Image("backgrounds/trace.png")

val pieceMap = mapOf('K' to "king", 'Q' to "queen", 'B' to "bishop", 'R' to "rook", 'N' to "knight", 'P' to "pawn",
    'k' to "king", 'q' to "queen", 'b' to "bishop", 'r' to "rook", 'n' to "knight", 'p' to "pawn")
val rookDirections = listOf(1 to 0, 0 to 1, -1 to 0, 0 to -1)
val bishopDirections = listOf(1 to 1, -1 to 1, 1 to -1, -1 to -1)
val knightDirections = listOf(1 to 2, 1 to -2, -1 to 2, -1 to -2, 2 to 1, 2 to -1, -2 to 1, -2 to -1)
val kingDirections = listOf(1 to 0, 0 to 1, 0 to -1, -1 to 0, -1 to -1, -1 to 1, 1 to 1, 1 to -1)

fun onBoard(x: Int, y: Int) = (x in 0 until 8) && (y in 0 until 8)
fun checkEnemy(from: Char, to: Char) = (from.isUpperCase() && to.isLowerCase()) || (from.isLowerCase() && to.isUpperCase())

fun isAlly(a: Char, b: Char) = (a.isLowerCase() && b.isLowerCase()) || (a.isUpperCase() && b.isUpperCase())

fun isCellUnderAttacked(row: Int, col: Int, moves: MutableList<Move>): Boolean {
    for(move in moves) {
        if(row == move.toRow && col == move.toCol)
            return true
    }
    return false
}

// ________________________________________________________________________
// ________________________________________________________________________
// ________________________________________________________________________

fun setBit(bitboard: Long, row: Int, col: Int): Long{
    return bitboard or (1L shl (row*8 + col))
}

fun bitboardOf(board: Array<CharArray>, isMaxNode: Boolean): Long {
    var bitboard = 0L
    if(isMaxNode) {
        for (i in 0..7) {
            for (j in 0..7) {
                when(board[i][j]) {
                    'P' -> bitboard = setPBit(bitboard, i, j, board)
                    'R' -> bitboard = setRBit(bitboard, i, j, board)
                    'N' -> bitboard = setNBit(bitboard, i, j, board)
                    'B' -> bitboard = setBBit(bitboard, i, j, board)
                    'Q' -> bitboard = setQBit(bitboard, i, j, board)
                    'K' -> bitboard = setKBit(bitboard, i, j, board)
                }
            }
        }
    } else {
        for (i in 0..7) {
            for (j in 0..7) {
                when(board[i][j]) {
                    'p' -> bitboard = setPBit(bitboard, i, j, board)
                    'r' -> bitboard = setRBit(bitboard, i, j, board)
                    'n' -> bitboard = setNBit(bitboard, i, j, board)
                    'b' -> bitboard = setBBit(bitboard, i, j, board)
                    'q' -> bitboard = setQBit(bitboard, i, j, board)
                    'k' -> bitboard = setKBit(bitboard, i, j, board)
                }
            }
        }
    }
    return bitboard
}

fun setPBit(bitboard: Long, r: Int, c: Int, board: Array<CharArray>): Long {
    var bitboard = bitboard
    if(board[r][c].isUpperCase()) {
        if(c < 7 && r < 7) {
            if((board[r+1][c+1].isLowerCase() && board[r+1][c+1] in "kK") || !board[r+1][c+1].isLowerCase())
                bitboard = setBit(bitboard, r+1, c+1)
        }
        if(c > 0 && r < 7) {
            if((board[r+1][c-1].isLowerCase() && board[r+1][c-1] in "kK") || !board[r+1][c-1].isLowerCase())
                bitboard = setBit(bitboard, r+1, c-1)
        }
    }
    else {
        if(c < 7 && r > 0) {
            if((board[r-1][c+1].isUpperCase() && board[r-1][c+1] in "kK") || !board[r-1][c+1].isUpperCase())
                bitboard = setBit(bitboard, r-1, c+1)
        }
        if(c > 0 && r > 0) {
            if((board[r-1][c-1].isUpperCase() && board[r-1][c-1] in "kK") || !board[r-1][c-1].isUpperCase())
                bitboard = setBit(bitboard, r-1, c-1)
        }
    }
    return bitboard
}

fun setRBit(bitboard: Long, r: Int, c: Int, board: Array<CharArray>): Long {
    var bitboard = bitboard
    for(dir in rookDirections) {
        for (m in 1 until 8) {
            val endRow = r + dir.first*m
            val endCol = c + dir.second*m
            if(onBoard(endRow, endCol)) {
                if(board[endRow][endCol] == '-') {
                    bitboard = setBit(bitboard, endRow, endCol)
                } else if(checkEnemy(board[r][c], board[endRow][endCol])) {
                    if(board[endRow][endCol] in "Kk") {
                        bitboard = setBit(bitboard, endRow, endCol)
                    } else break
                } else {
                    bitboard = setBit(bitboard, endRow, endCol)
                    break
                }
            } else break
        }
    }
    return bitboard
}

fun setBBit(bitboard: Long, r: Int, c: Int, board: Array<CharArray>): Long {
    var bitboard = bitboard
    for(dir in bishopDirections) {
        for (m in 1 until 8) {
            val endRow = r + dir.first*m
            val endCol = c + dir.second*m
            if(onBoard(endRow, endCol)) {
                if(board[endRow][endCol] == '-') {
                    bitboard = setBit(bitboard, endRow, endCol)
                } else if(checkEnemy(board[r][c], board[endRow][endCol])) {
                    if(board[endRow][endCol] in "Kk") {
                        bitboard = setBit(bitboard, endRow, endCol)
                    } else break
                } else {
                    bitboard = setBit(bitboard, endRow, endCol)
                    break
                }
            } else break
        }
    }
    return bitboard
}

fun setQBit(bitboard: Long, r: Int, c: Int, board: Array<CharArray>): Long {
    val bitboard = setBBit(bitboard, r, c, board)
    return setRBit(bitboard,r, c, board)
}

fun setNBit(bitboard: Long, r: Int, c: Int, board: Array<CharArray>): Long {
    var bitboard = bitboard
    for(dir in knightDirections) {
        val endRow = r + dir.first
        val endCol = c + dir.second
        if(onBoard(endRow, endCol)) {
            if(board[endRow][endCol] == '-')
                bitboard = setBit(bitboard, endRow, endCol)
            else if(checkEnemy(board[r][c], board[endRow][endCol]) && board[endRow][endCol] in "kK")
                bitboard = setBit(bitboard, endRow, endCol)
            else if(isAlly(board[r][c], board[endRow][endCol]))
                bitboard = setBit(bitboard, endRow, endCol)
        }
    }
    return bitboard
}

fun setKBit(bitboard: Long, r: Int, c: Int, board: Array<CharArray>): Long {
    var bitboard = bitboard
    for(dir in kingDirections) {
        val endRow = r + dir.first
        val endCol = c + dir.second
        if(onBoard(endRow, endCol)) {
            if(board[endRow][endCol] == '-')
                bitboard = setBit(bitboard, endRow, endCol)
            else if(checkEnemy(board[r][c], board[endRow][endCol]) && board[endRow][endCol] in "kK")
                bitboard = setBit(bitboard, endRow, endCol)
            else if(isAlly(board[r][c], board[endRow][endCol]))
                bitboard = setBit(bitboard, endRow, endCol)
        }
    }
    return bitboard
}