import objects.Move

enum class GameMode {
    AI_MODE, PVP, AI_FULL
}

data class PieceInfo(var player: Boolean, var pieceName: String, var mode: Int)

data class CellPos(var rowId: Int, var columnId: Int)

fun <T, V> pairOf(first: T, second: V) = Pair(first, second)

val pieceScore = mapOf('K' to 900, 'Q' to 90, 'R' to 50, 'B' to 30, 'N' to 30, 'P' to 10,
                        'k' to -900, 'q' to -90, 'r' to -50, 'b' to -30, 'n' to -30, 'p' to -10, '-' to 0)
val pieceMap = mapOf('K' to "king", 'Q' to "queen", 'B' to "bishop", 'R' to "rook", 'N' to "knight", 'P' to "pawn",
    'k' to "king", 'q' to "queen", 'b' to "bishop", 'r' to "rook", 'n' to "knight", 'p' to "pawn")
val rookDirections = listOf(1 to 0, 0 to 1, -1 to 0, 0 to -1)
val bishopDirections = listOf(1 to 1, -1 to 1, 1 to -1, -1 to -1)
val knightDirections = listOf(1 to 2, 1 to -2, -1 to 2, -1 to -2, 2 to 1, 2 to -1, -2 to 1, -2 to -1)
val kingDirections = listOf(1 to 0, 0 to 1, 0 to -1, -1 to 0, -1 to -1, -1 to 1, 1 to 1, 1 to -1)

fun onBoard(x: Int, y: Int) = (x in 0 until 8) && (y in 0 until 8)
fun checkEnemy(from: Char, to: Char) = !(from.isUpperCase().xor(to.isLowerCase()))
val moveFunction = mapOf('k' to ::getKingMoves, 'q' to ::getQueenMoves, 'b' to ::getBishopMoves, 'r' to ::getRookMoves, 'n' to ::getKnightMoves, 'p' to ::getPawnMoves)

fun isAlly(a: Char, b: Char) = (a.isLowerCase() && b.isLowerCase()) || (a.isUpperCase() && b.isUpperCase())

fun getPawnMoves(row: Int, col: Int, board: Array<CharArray>, moves: MutableList<Move>) {
    if(board[row][col].isLowerCase()) {
        if(row > 0) {
            if (board[row - 1][col] == '-')
                moves.add(Move(row to col, (row - 1) to col, board))
            if (row == 6 && board[row - 2][col] == '-' && board[row - 1][col] == '-')
                moves.add(Move(row to col, (row - 2) to col, board))
            if (col > 0) {
                if(board[row - 1][col - 1] != '-' && checkEnemy(board[row - 1][col - 1], board[row][col]))
                    moves.add(Move(row to col, (row - 1) to (col - 1), board))
            }
            if (col < 7) {
                if(board[row - 1][col + 1] != '-' && checkEnemy(board[row - 1][col + 1], board[row][col]))
                    moves.add(Move(row to col, (row - 1) to (col + 1), board))
            }
        }
    }
    else {
        if(row < 7) {
            if (board[row + 1][col] == '-')
                moves.add(Move(row to col, (row + 1) to col, board))
            if (row == 1 && board[row + 2][col] == '-' && board[row + 1][col] == '-')
                moves.add(Move(row to col, (row + 2) to col, board))
            if (col > 0) {
                if(board[row + 1][col - 1] != '-' && checkEnemy(board[row + 1][col - 1], board[row][col]))
                    moves.add(Move(row to col, (row + 1) to (col - 1), board))
            }
            if (col < 7) {
                if(board[row + 1][col + 1] != '-' && checkEnemy(board[row + 1][col + 1], board[row][col]))
                    moves.add(Move(row to col, (row + 1) to (col + 1), board))
            }
        }
    }
}

fun getRookMoves(row: Int, col: Int, board: Array<CharArray>, moves: MutableList<Move>) {
    for(dir in rookDirections) {
        for (c in 1 until 8) {
            val endRow = row + dir.first*c
            val endCol = col + dir.second*c
            if(onBoard(endRow, endCol)) {
                if(board[endRow][endCol] == '-' || checkEnemy(board[row][col], board[endRow][endCol])) {
                    moves.add(Move(row to col, endRow to endCol, board))
                } else break
            } else break
        }
    }
}

fun getBishopMoves(row: Int, col: Int, board: Array<CharArray>, moves: MutableList<Move>) {
    for(dir in bishopDirections) {
        for (c in 1 until 8) {
            val endRow = row + dir.first*c
            val endCol = col + dir.second*c
            if(onBoard(endRow, endCol)) {
                if(board[endRow][endCol] == '-' || checkEnemy(board[row][col], board[endRow][endCol])) {
                    moves.add(Move(row to col, endRow to endCol, board))
                } else break
            } else break
        }
    }
}

fun getQueenMoves(row: Int, col: Int, board: Array<CharArray>, moves: MutableList<Move>) {
    getBishopMoves(row, col, board, moves)
    getRookMoves(row, col, board, moves)
}

fun getKnightMoves(row: Int, col: Int, board: Array<CharArray>, moves: MutableList<Move>) {
    for(dir in knightDirections) {
        val endRow = row + dir.first
        val endCol = col + dir.second
        if(onBoard(endRow, endCol)) {
            if(board[endRow][endCol] == '-' || checkEnemy(board[row][col], board[endRow][endCol])) {
                moves.add(Move(row to col, endRow to endCol, board))
            }
        }
    }
}

fun getKingMoves(row: Int, col: Int, board: Array<CharArray>, moves: MutableList<Move>) {
    for(dir in kingDirections) {
        val endRow = row + dir.first
        val endCol = col + dir.second
        if(onBoard(endRow, endCol)) {
            if(board[endRow][endCol] == '-' || checkEnemy(board[row][col], board[endRow][endCol])) {
                moves.add(Move(row to col, endRow to endCol, board))
            }
        }
    }
}
