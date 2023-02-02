package core

//val originalData = arrayOf(
//    charArrayOf('R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'),
//    charArrayOf('P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'),
//    charArrayOf('-', '-', '-', '-', '-', '-', '-', '-'),
//    charArrayOf('-', '-', '-', '-', '-', '-', '-', '-'),
//    charArrayOf('-', '-', '-', '-', '-', '-', '-', '-'),
//    charArrayOf('-', '-', '-', '-', '-', '-', '-', '-'),
//    charArrayOf('p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'),
//    charArrayOf('r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'),
//)
//
//val specs = SpecialMoves(enPassant = null, wk = true, wq = true, bk = true, bq = false)
//
//class Engine() {
//    private var board = Board(originalData, 0L, specs,
//        Pair(7, 4), Pair(0, 4))
//
//    val cells = Array(8) {i -> Array(8) { j -> Cell(i, j, originalData[i][j]) } }
//    private val property = GameProperties
//    private var activeCell: CPair<Int, Int>? = null
//    var score = 0
//    private var trace: Pair<Pair<Int, Int>, Pair<Int, Int>>? = null
//    private var clickMoves: List<Move>? = null
//    var player: Player? = null
//
//    init {
//        for(i in 0 until 8) {
//            for(j in 0 until 8) {
//                cells[i][j].setOnMouseClicked {
//                    performCellClick(i, j)
//                }
//            }
//        }
//    }
//
//    private fun performCellClick(row: Int, col: Int) {
//        println("clicked")
//        GameProperties.aiTurn = false
//        if(activeCell == null) {
//            if(cells[row][col].occupied && cells[row][col].data.isLowerCase()) {
//                val temp = board.getLegalMoves(false)
//                clickMoves = temp.filter { it.fromRow == row && it.fromCol == col }
//                activeCell = pairOf(row, col)
//                render()
//            }
//        } else {
//            val m = validMove(row, col)
//            if(m != null) {
//                moveWithUI(m, false) // rendered
//                activeCell = null
//                GameProperties.aiTurn = true
//                search()
//            } else {
//                if(isAlly(board[row, col], board[activeCell!!.first, activeCell!!.second])) {
//                    if(activeCell!!.first != row || activeCell!!.second != col) {
//                        activeCell = pairOf(row, col)
//                        val temp = board.getLegalMoves(false)
//                        clickMoves = temp.filter { it.fromRow == row && it.fromCol == col }
//                    } else activeCell = null
//                } else activeCell = null
//                render()
//            }
//        }
//    }
//
//
//    private fun search() {
//        val root = TreeNode(board, score, true)
//        player?.move(root, depth=1)
//    }
//
//    private fun printBoard() {
//        println("=========================================================")
//        println("    ===========================")
//        for(i in 0 until 8) {
//            print("    = ")
//            for(j in 0..7) {
//                print("${board[i, j]}  ")
//            }
//            println("=")
//        }
//        println("    ===========================")
//        println("=========================================================\n")
//    }
//
//    private fun validMove(row: Int, col: Int): Move? {
//        var temp: Move? = null
//        for(move in clickMoves!!) {
//            if(row == move.toRow && col == move.toCol) {
//                temp = move
//                break
//            }
//        }
//        clickMoves = null
//        return temp
//    }
//
//    private fun updateBoard(m: Move, aiMove: Boolean) {
//        val temp = move(board, m, aiMove)
//        board = temp.board
//        score = temp.score
//        for(i in 0 until 8)
//            for(j in 0 until 8) {
//                cells[i][j].data = board[i, j]
//            }
//    }
//
//    fun moveWithUI(m: Move, aiMove: Boolean) {
//        updateBoard(m, aiMove)
//        if(cells[m.toRow][m.toCol].occupied) {
//            property.pieceAte.pieceName = cells[m.toRow][m.toCol].piece!!.name
//            property.pieceAte.player = !property.aiTurn
//            println("eat: ${property.pieceAte.player}")
//            property.ate.value++
//        }
//        trace = Pair(m.fromRow, m.fromCol) to Pair(m.toRow, m.toCol)
//        render()
//        printBoard()
//    }
//
//    fun undoWithUI() {
////        undo()
////        render()
//    }
//
//    fun performFirstMove() {
//        search()
//    }
//
//    fun render() {
//        for(i in 0 until 8) {
//            for(j in 0 until 8) {
//                cells[i][j].resetState()
//            }
//        }
//
//        clickMoves?.let{
//            for (m in it) {
//                println("highlight $m")
//                cells[m.toRow][m.toCol].addEffect(ImageView(movableImg).apply{ opacity = 0.8 })
//                if (cells[m.toRow][m.toCol].occupied)
//                    cells[m.toRow][m.toCol].addEffect(ImageView(attackedImg).apply{ opacity = 0.6 })
//            }
//        }
//
//        trace?.let{
//            cells[it.first.first][it.first.second].addEffect(ImageView(traceImg).apply{ opacity = 0.65 })
//            cells[it.second.first][it.second.second].addEffect(ImageView(traceImg).apply{ opacity = 0.65 })
//        }
//        activeCell?.let{ cells[it.first][it.second].addEffect (ImageView(traceImg).apply{ opacity = 0.8 }) }
//
//        for(i in 0 until 8) {
//            for(j in 0 until 8) {
//                cells[i][j].display()
//            }
//        }
//    }
//}

/*

fun isCellUnderAttacked(row: Int, col: Int, moves: MutableList<Move>): Boolean {
    for(move in moves) {
        if(row == move.toRow && col == move.toCol)
            return true
    }
    return false
}

fun printBitboard(bitboard: Long) {
    for(i in 0..7) {
        for(j in 0..7) {
            print("${if(isAttacked(bitboard, i, j)) 1 else 0} ")
        }

        println()
    }
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
                } else if(isEnemy(board[r][c], board[endRow][endCol])) {
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
                } else if(isEnemy(board[r][c], board[endRow][endCol])) {
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
            else if(isEnemy(board[r][c], board[endRow][endCol]) && board[endRow][endCol] in "kK")
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
            else if(isEnemy(board[r][c], board[endRow][endCol]) && board[endRow][endCol] in "kK")
                bitboard = setBit(bitboard, endRow, endCol)
            else if(isAlly(board[r][c], board[endRow][endCol]))
                bitboard = setBit(bitboard, endRow, endCol)
        }
    }
    return bitboard
}

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
                if(board[row-1, col - 1] != '-' && isEnemy(board[row-1, col - 1], board[row, col]))
                    moves.add(Move(row, col, (row - 1), (col - 1)))
                if(board[row-1, col - 1] == '-' && Pair(row-1, col-1) == board.specialMoves.enPassant) {
                    moves.add(Move(row, col, (row - 1), (col - 1), isEnPassantMove = true))
                }
            }
            if (col < 7) {
                if(board[row-1, col+1] != '-' && isEnemy(board[row-1, col+1], board[row, col]))
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
                if(board[row+1, col-1] != '-' && isEnemy(board[row+1, col-1], board[row, col]))
                    moves.add(Move(row, col, (row + 1), (col - 1)))
                if(board[row+1, col-1] == '-' && Pair(row+1, col-1) == board.specialMoves.enPassant) {
                    moves.add(Move(row, col, (row + 1), (col - 1), isEnPassantMove = true))
                }
            }
            if (col < 7) {
                if(board[row+1, col+1] != '-' && isEnemy(board[row+1, col+1], board[row, col]))
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
                } else if(isEnemy(board[row, col], board[endRow, endCol])) {
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
                } else if(isEnemy(board[row, col], board[endRow, endCol])) {
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
            if(board[endRow, endCol] == '-' || isEnemy(board[row, col], board[endRow, endCol])) {
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
            if(board[endRow, endCol] == '-' || isEnemy(board[row, col], board[endRow, endCol])) {
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
    val kingPos = if(black) board.bkLoc else board.wkLoc
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

* */