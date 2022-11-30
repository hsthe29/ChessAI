package objects

data class Move(val startCell: Pair<Int, Int>,
                val endCell: Pair<Int,Int>,
                val board: Array<CharArray>,
                val isEnPassantMove: Boolean = false,
                val isCastleMove: Boolean = false) {
    val startRow: Int; val startCol: Int
    val endRow: Int; val endCol: Int

    init {
        startRow = startCell.first
        startCol = startCell.second
        endRow = endCell.first
        endCol = endCell.second
    }

    override fun toString(): String {
        return "$startCell -> $endCell"
    }
}