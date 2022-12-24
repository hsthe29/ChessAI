
/*

var enPassant: CPair<Int, Int>? = null
val enPassantLog = mutableListOf<CPair<Int, Int>?>(null)
var castle = CastleInfo(true, true, true, true)
val castleLog = mutableListOf(CastleInfo(true, true, true, true))
fun checkCastleMoves(m: Move) {
    when(board[m.startRow][m.startCol]) {
        'K' -> {
            this.castle.leftBlack = false
            this.castle.rightBlack = false
        }
        'k' -> {
            this.castle.leftWhite = false
            this.castle.rightWhite = false
        }
        'R' -> {
            if(m.startRow == 0 && m.startCol == 0)
                this.castle.leftBlack = false
            if(m.startRow == 0 && m.startCol == 7)
                this.castle.rightBlack = false
        }
        'r' -> {
            if(m.startRow == 7 && m.startCol == 0)
                this.castle.leftWhite = false
            if(m.startRow == 7 && m.startCol == 7)
                this.castle.rightWhite = false
        }
    }
    if(m.endRow == 0 && m.endCol == 0) this.castle.leftBlack = false
    else if(m.endRow == 0 && m.endCol == 7) this.castle.rightBlack = false
    else if(m.endRow == 7 && m.endCol == 0) this.castle.leftWhite = false
    else if(m.endRow == 7 && m.endCol == 7) this.castle.rightWhite = false
}
fun move(m: Move) {
    checkCastleMoves(m)
    castleLog.add(CastleInfo(castle.leftWhite, castle.rightWhite, castle.leftBlack, castle.rightBlack))
    when(board[m.startRow][m.startCol]) {
        'k' -> {
            whiteKingPos.set(m.endRow, m.endCol)
        }
        'K' -> {
            blackKingPos.set(m.endRow, m.endCol)
        }
    }
    if(board[m.startRow][m.startCol].lowercaseChar() == 'p' && abs(m.startRow - m.endRow) == 2)
        this.enPassant = pairOf((m.startRow + m.endRow)/2, m.startCol)
    else this.enPassant = null

    this.enPassantLog.add(this.enPassant)

    if(m.isEnPassantMove) {
        moveHistory.add(pairOf(m, board[m.startRow][m.endCol]))
        board[m.startRow][m.endCol] = '-'
        board[m.endRow][m.endCol] = board[m.startRow][m.startCol]
        board[m.startRow][m.startCol] = '-'
    } else if(m.isCastleMove) {
        board[m.endRow][m.endCol] = board[m.startRow][m.startCol]
        board[m.startRow][m.startCol] = '-'
        if(m.endCol - m.startCol == 2) {
            moveHistory.add(pairOf(m, board[m.endRow][m.endCol+1]))
            board[m.endRow][m.endCol-1] = board[m.endRow][m.endCol+1]
            board[m.endRow][m.endCol+1] = '-'
        } else {
            moveHistory.add(pairOf(m, board[m.endRow][m.endCol-2]))
            board[m.endRow][m.endCol+1] = board[m.endRow][m.endCol-2]
            board[m.endRow][m.endCol-2] = '-'
        }
    } else {
        moveHistory.add(pairOf(m, board[m.endRow][m.endCol]))
        */
/** Check En passant move *//*

        board[m.endRow][m.endCol] = board[m.startRow][m.startCol]
        board[m.startRow][m.startCol] = '-'
    }

    val opponent = getAllPossibleMoves(true)
    val mine = getAllPossibleMoves(false)
    whiteCheckmate = false
    blackCheckmate = false
    for(mv in opponent) {
        if(whiteKingPos.first == mv.endRow && whiteKingPos.second == mv.endCol) {
            whiteCheckmate = true
            break
        }
    }
    for(mv in mine) {
        if(blackKingPos.first == mv.endRow && blackKingPos.second == mv.endCol) {
            blackCheckmate = true
            break
        }
    }
}

fun undo() {
    if(moveHistory.isNotEmpty()) {
        val (m, char) = moveHistory.removeLast()
        when(board[m.endRow][m.endCol]) {
            'k' -> whiteKingPos.set(m.startRow, m.startCol)

            'K' -> blackKingPos.set(m.startRow, m.startCol)
        }
        if(m.isEnPassantMove) {
            board[m.startRow][m.startCol] = board[m.endRow][m.endCol]
            board[m.startRow][m.endCol] = char
            board[m.endRow][m.endCol] = '-'
        } else if(m.isCastleMove) {
            board[m.startRow][m.startCol] = board[m.endRow][m.endCol]
            board[m.endRow][m.endCol] = '-'
            if(m.endCol - m.startCol == 2) {
                board[m.endRow][m.endCol+1] = board[m.endRow][m.endCol-1]
                board[m.endRow][m.endCol-1] = '-'
            } else {
                board[m.endRow][m.endCol-2] = board[m.endRow][m.endCol+1]
                board[m.endRow][m.endCol+1] = '-'
            }
        } else {
            board[m.startRow][m.startCol] = board[m.endRow][m.endCol]
            board[m.endRow][m.endCol] = char
        }
        this.enPassantLog.removeLast()
        this.enPassant = this.enPassantLog.last()
        this.castleLog.removeLast()
        this.castle = this.castleLog.last()
    }
    else println("Error!")
}*/
