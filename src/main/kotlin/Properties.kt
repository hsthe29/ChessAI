import objects.PlayerOrder

enum class GameMode {
    AI_MODE, PVP, AI_FULL
}

data class ChessPos(var rowId: Int, var columnId: Int,val signal: String) {
    fun setLocate(rowId: Int, columnId: Int) {
        this.rowId = rowId
        this.columnId = columnId
    }
}

data class Edible(var player: Boolean, var pieceName: String, var mode: Int)

data class CellPos(var rowId: Int, var columnId: Int)

val piecesName = listOf("bishop", "king", "knight", "pawn", "queen", "rook")
val kingPos = listOf(ChessPos(7, 4, "w_"), ChessPos(0, 4, "b_"))
val queenPos = listOf(ChessPos(7, 3, "w_"), ChessPos(0, 3, "b_"))
val knightPos = listOf(ChessPos(7, 1, "w_"), ChessPos(7, 6, "w_"),
    ChessPos(0, 1, "b_"), ChessPos(0, 6, "b_"))
val bishopPos = listOf(ChessPos(7, 2, "w_"), ChessPos(7, 5, "w_"),
    ChessPos(0, 1, "b_"), ChessPos(0, 6, "b_"))
val rookPos = listOf(ChessPos(7, 0, "w_"), ChessPos(7, 7, "w_"),
    ChessPos(0, 0, "b_"), ChessPos(0, 7, "b_"))
val pawnPos = listOf(ChessPos(6, 0, "w_"), ChessPos(6, 1, "w_"),
    ChessPos(6, 2, "w_"), ChessPos(6, 3, "w_"), ChessPos(6, 4, "w_"),
    ChessPos(6, 5, "w_"), ChessPos(6, 6, "w_"), ChessPos(6, 7, "w_"),
    ChessPos(1, 0, "b_"), ChessPos(1, 1, "b_"), ChessPos(1, 2, "b_"),
    ChessPos(1, 3, "b_"), ChessPos(1, 4, "b_"), ChessPos(1, 5, "b_"),
    ChessPos(1, 6, "b_"), ChessPos(1, 7, "b_"))

val slant = listOf(Pair(1, 1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1))