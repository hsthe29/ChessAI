package core

import Move
import SpecialMoves

enum class PinDirection {
    ROW, COL, F_DIAG, B_DIAG
}
data class Pin(val row: Int, val col: Int, val direction: PinDirection)
data class Line(val x1: Int, val y1: Int, val x2: Int, val y2: Int) {
    operator fun contains(point: Pair<Int, Int>): Boolean {
        val a1 = point.first - x1
        val a2 = point.second - y1
        val b1 = x2 - point.first
        val b2 = y2 - point.second
        return (a1*b2 == a2*b1) && (a1*b1 >= 0) && (a2*b2 >= 0)
    }
}
class Board(private val data: Array<CharArray>, var bitboard: Long, var specialMoves: SpecialMoves,
            var wkLoc: Pair<Int, Int>, var bkLoc: Pair<Int, Int>) {
    val pins = mutableListOf<Pair<Int, Int>>()
    val mateLines = mutableListOf<Line>()

    operator fun get(row: Int, col: Int) = data[row][col]
    operator fun set(row: Int, col: Int, value: Char) {
        data[row][col] = value
    }

    /** return copy of board data */
    fun data() = Array(data.size) { data[it].clone() }

    fun getLegalMoves(isMaxNode: Boolean): MutableList<Move> {
        val moves = mutableListOf<Move>()
        /*pins.clear()
        mateLines.clear()
        if (isMaxNode) {
            val checkmate = isAttacked(this.bitboard, bkLoc.first, bkLoc.second)
            this.checkPinsAndMate(bkLoc, checkmate)
            if (mateLines.size > 1) {
                movesOfKing(bkLoc.first, bkLoc.second, moves, this)
            } else {
                for (i in 0 until 8) {
                    for (j in 0 until 8) {
                        if (data[i][j].isUpperCase()) {
                            movesOf(data[i][j])(i, j, moves, this)
                        }
                    }
                }
                if (mateLines.size > 0) {
                    moves.removeIf { data[it.fromRow][it.fromCol] != 'K' && Pair(it.toRow, it.toCol) !in mateLines[0] }
                } else castleMoves(true, moves, this)
            }

        } else {
            val checkmate = isAttacked(this.bitboard, wkLoc.first, wkLoc.second)
            this.checkPinsAndMate(wkLoc, checkmate)
            if (mateLines.size > 1) {
                movesOfKing(wkLoc.first, wkLoc.second, moves, this)
            } else {
                for (i in 0 until 8) {
                    for (j in 0 until 8) {
                        if (data[i][j].isLowerCase()) {
                            movesOf(data[i][j])(i, j, moves, this)
                        }
                    }
                }
                if (mateLines.size > 0)
                    moves.removeIf { data[it.fromRow][it.fromCol] != 'k' && Pair(it.toRow, it.toCol) !in mateLines[0] }
                else castleMoves(false, moves, this)
            }
        }*/
        return moves
    }

    fun checkPinsAndMate(kingPos: Pair<Int, Int>, checkmate: Boolean) {
        // straight check
        val king = data[kingPos.first][kingPos.second]
        var pin: Pair<Int, Int>? = null
        val straight = arrayOf(Pair(1, 0), Pair(0, 1), Pair(-1, 0), Pair(0, -1))
        for (dir in straight) {
            var continuous = checkmate
            pin = null
            for (c in 1 until 8) {
                val row = kingPos.first + c * dir.first
                val col = kingPos.second + c * dir.second
                if (row in 0 until 8 && col in 0 until 8) {
                    if (isEnemy(king, data[row][col])) {
                        if (pin == null) {
                            if(continuous && data[row][col] in "rkqRKQ")
                                mateLines.add(Line(kingPos.first, kingPos.second, row, col))
                        } else {
                            pins.add(pin)
                        }
                        break
                    }
                    if (isAlly(king, data[row][col])) {
                        if (pin == null) {
                            continuous = false
                            pin = Pair(row, col)
                        } else break
                    }
                    continuous = continuous and isAttacked(this.bitboard, row, col)
                }
            }
        }
        // oblique check
        val oblique = arrayOf(Pair(1, 1), Pair(-1, -1), Pair(-1, 1), Pair(1, -1))
        for (dir in oblique) {
            var continuous = checkmate
            pin = null
            for (c in 1 until 8) {
                val row = kingPos.first + c * dir.first
                val col = kingPos.second + c * dir.second
                if (row in 0 until 8 && col in 0 until 8) {
                    if (isEnemy(king, data[row][col])) {
                        if (pin == null) {
                            if(continuous) {
                                if(data[row][col] in "bkqBKQ")
                                    mateLines.add(Line(kingPos.first, kingPos.second, row, col))
                                else if (data[row][col] == 'p' && king == 'K' && c == 1 && dir.first == 1)
                                    mateLines.add(Line(kingPos.first, kingPos.second, row, col))
                                else if(data[row][col] == 'P' && king == 'k' && c == 1 && dir.first == -1)
                                    mateLines.add(Line(kingPos.first, kingPos.second, row, col))
                            }
                        } else {
                            pins.add(pin)
                        }
                        break
                    }
                    if (isAlly(king, data[row][col])) {
                        if (pin == null) {
                            continuous = false
                            pin = Pair(row, col)
                        } else break
                    }
                    continuous = continuous and isAttacked(this.bitboard, row, col)
                }
            }
        }
        // Knight check
        val kn = arrayOf(
            Pair(1, 2),
            Pair(1, -2),
            Pair(-1, 2),
            Pair(-1, -2),
            Pair(2, 1),
            Pair(2, -1),
            Pair(-2, 1),
            Pair(-2, -1)
        )
        for ((dr, dc) in kn) {
            val row = kingPos.first + dr
            val col = kingPos.second + dc
            if (row in 0 until 8 && col in 0 until 8) {
                if (isEnemy(king, data[row][col]) && data[row][col] in "nN") {
                    mateLines.add(Line(kingPos.first, kingPos.second, row, col))
                }
            }
        }
    }
}

fun isStraight(char: Char): Boolean {
    return char.lowercaseChar() in "rq"
}

fun isDiagonal(char: Char): Boolean {
    return char.lowercaseChar() in "bq"
}

/*fun removeResidualMoves(moves: MutableList<Move>, board: Board, pins: MutableList<Pin>, line: Line)
    = moves.removeIf { board[it.fromRow, it.fromCol] !in "kK" && Pair(it.toRow, it.toCol) !in line }*/

fun isAttacked(bitboard: Long, row: Int, col: Int): Boolean {
    return ((bitboard shr (row*8 + col)) and 1L) == 1L
}