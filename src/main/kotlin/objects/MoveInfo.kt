import engine.*

data class SpecialMoves(val enPassant: Pair<Int, Int>?, var wk: Boolean, var wq: Boolean, var bk: Boolean, var bq: Boolean)

data class Move(val fromRow: Int, val fromCol: Int,
                val toRow: Int, val toCol: Int,
                val isEnPassantMove: Boolean = false,
                val isCastleMove: Boolean = false) {
    override fun toString(): String {
        return "($fromRow, $fromCol) -> ($toRow, $toCol)"
    }
}

fun movesOfPawn(row: Int, col: Int, moves: MutableList<Move>, board: Board) {
    if(board[row, col].isLowerCase()) {
        if(row > 0) {
            if (board[row-1, col] == '-')
                moves.add(Move(row, col, (row - 1), col))
            if (row == 6 && board[row-2, col] == '-' && board[row-1, col] == '-')
                moves.add(Move(row, col, (row - 2), col))
            if (col > 0) {
                if(board[row-1, col - 1] != '-' && checkEnemy(board[row-1, col - 1], board[row, col]))
                    moves.add(Move(row, col, (row - 1), (col - 1)))
                if(board[row-1, col - 1] == '-' && Pair(row-1, col-1) == board.specialMoves.enPassant) {
                    moves.add(Move(row, col, (row - 1), (col - 1), isEnPassantMove = true))
                }
            }
            if (col < 7) {
                if(board[row-1, col+1] != '-' && checkEnemy(board[row-1, col+1], board[row, col]))
                    moves.add(Move(row, col, row - 1, col + 1))
                if(board[row-1, col+1] == '-' && Pair(row-1, col+1) == board.specialMoves.enPassant) {
                    moves.add(Move(row, col, (row - 1), (col + 1), isEnPassantMove = true))
                }
            }
        }
    }
    else {
        if(row < 7) {
            if (board[row+1, col] == '-')
                moves.add(Move(row, col, (row + 1), col))
            if (row == 1 && board[row+2, col] == '-' && board[row + 1, col] == '-')
                moves.add(Move(row, col, (row + 2), col))
            if (col > 0) {
                if(board[row+1, col-1] != '-' && checkEnemy(board[row+1, col-1], board[row, col]))
                    moves.add(Move(row, col, (row + 1), (col - 1)))
                if(board[row+1, col-1] == '-' && Pair(row+1, col-1) == board.specialMoves.enPassant) {
                    moves.add(Move(row, col, (row + 1), (col - 1), isEnPassantMove = true))
                }
            }
            if (col < 7) {
                if(board[row+1, col+1] != '-' && checkEnemy(board[row+1, col+1], board[row, col]))
                    moves.add(Move(row, col, (row + 1), (col + 1)))
                if(board[row+1, col+1] == '-' && Pair(row+1, col+1) == board.specialMoves.enPassant) {
                    moves.add(Move(row, col, (row + 1), (col + 1), isEnPassantMove = true))
                }
            }
        }
    }
}

fun movesOfRook(row: Int, col: Int, moves: MutableList<Move>, board: Board) {
    for(dir in rookDirections) {
        for (c in 1 until 8) {
            val endRow = row + dir.first*c
            val endCol = col + dir.second*c
            if(onBoard(endRow, endCol)) {
                if(board[endRow, endCol] == '-') {
                    moves.add(Move(row, col, endRow, endCol))
                } else if(checkEnemy(board[row, col], board[endRow, endCol])) {
                    moves.add(Move(row, col, endRow, endCol))
                    break
                } else break
            } else break
        }
    }
}

fun movesOfBishop(row: Int, col: Int, moves: MutableList<Move>, board: Board) {
    for(dir in bishopDirections) {
        for (c in 1 until 8) {
            val endRow = row + dir.first*c
            val endCol = col + dir.second*c
            if(onBoard(endRow, endCol)) {
                if(board[endRow, endCol] == '-') {
                    moves.add(Move(row, col, endRow, endCol))
                } else if(checkEnemy(board[row, col], board[endRow, endCol])) {
                    moves.add(Move(row, col, endRow, endCol))
                    break
                } else break
            } else break
        }
    }
}

fun movesOfQueen(row: Int, col: Int, moves: MutableList<Move>, board: Board) {
    movesOfBishop(row, col, moves, board)
    movesOfRook(row, col, moves, board)
}

fun movesOfKnight(row: Int, col: Int, moves: MutableList<Move>, board: Board) {
    for(dir in knightDirections) {
        val endRow = row + dir.first
        val endCol = col + dir.second
        if(onBoard(endRow, endCol)) {
            if(board[endRow, endCol] == '-' || checkEnemy(board[row, col], board[endRow, endCol])) {
                moves.add(Move(row, col, endRow, endCol))
            }
        }
    }
}

fun movesOfKing(row: Int, col: Int, moves: MutableList<Move>, board: Board) {
    for(dir in kingDirections) {
        val endRow = row + dir.first
        val endCol = col + dir.second
        if(onBoard(endRow, endCol)) {
            if(isAttacked(board.bitboard, endRow, endCol)) {
                continue
            }
            if(board[endRow, endCol] == '-' || checkEnemy(board[row, col], board[endRow, endCol])) {
                moves.add(Move(row, col, endRow, endCol))
            }
        }
    }
}

fun movesOf(char: Char):  (Int, Int, MutableList<Move>, Board) -> Unit =
    when(char.lowercaseChar()) {
        'p'  -> ::movesOfPawn
        'r'  -> ::movesOfRook
        'n'  -> ::movesOfKnight
        'b'  -> ::movesOfBishop
        'q'  -> ::movesOfQueen
        else -> ::movesOfKing
    }

fun castleMoves(black: Boolean, moves: MutableList<Move>, board: Board) {
    val kingPos = if(black) board.blackKingPos else board.whiteKingPos
    if((black && board.specialMoves.bq) || (!black && board.specialMoves.wq))
        queenSideCastleMoves(kingPos.first, kingPos.second, moves, board)
    if((black && board.specialMoves.bk) || (!black && board.specialMoves.wk))
        kingSideCastleMoves(kingPos.first, kingPos.second, moves, board)
}

fun kingSideCastleMoves(row: Int, col: Int, moves: MutableList<Move>, board: Board) {
    if(board[row, col + 1] == '-' && board[row, col + 2] == '-')
        if(!isAttacked(board.bitboard, row, col+2))
            moves.add(Move(row, col, row, (col + 2), isCastleMove = true))
}

fun queenSideCastleMoves(row: Int, col: Int, moves: MutableList<Move>, board: Board) {
    if(board[row, col - 1] == '-' && board[row, col - 2] == '-' && board[row, col - 3] == '-')
        if(!isAttacked(board.bitboard, row, col-2))
            moves.add(Move(row, col, row, (col - 2), isCastleMove = true))
}