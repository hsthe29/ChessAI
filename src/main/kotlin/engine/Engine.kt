package engine

import Move
import SpecialMoves
import algorithm.TreeNode
import algorithm.move
import kotlinx.coroutines.runBlocking
import objects.ChessCell
import objects.Piece
import objects.Player
import tornadofx.*
import kotlin.math.abs

val charBoard = arrayOf(
    charArrayOf('R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'),
    charArrayOf('P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'),
    charArrayOf('-', '-', '-', '-', '-', '-', '-', '-'),
    charArrayOf('-', '-', '-', '-', '-', '-', '-', '-'),
    charArrayOf('-', '-', '-', '-', '-', '-', '-', '-'),
    charArrayOf('-', '-', '-', '-', '-', '-', '-', '-'),
    charArrayOf('p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'),
    charArrayOf('r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'),
)

class Engine() {

    val specialMoves = SpecialMoves(enPassant = null, wk = true, wq = true, bk = true, bq = false)
    var board = Board(charBoard, 0L, specialMoves,
        Pair(7, 4), Pair(0, 4))

    val cells = Array(8) {i -> Array(8) { j -> ChessCell(i, j) } }
    val property = GameProperties
    var activeCell: CPair<Int, Int>? = null
    var score = 0

    val movesOfClick = mutableListOf<Move>()
    val moveTrace = arrayOf(pairOf(-1, -1), pairOf(0, 0))
    var player: Player? = null

    init {
        for(i in 0 until 8) {
            for(j in 0 until 8) {
                cells[i][j].setOnMouseClicked {
                    performCellClick(i, j)
                }
            }
        }
    }

    private fun performCellClick(row: Int, col: Int) {
        println("clicked")
        GameProperties.aiTurn = false
        if(activeCell == null) {
            if(cells[row][col].isOccupied && board[row, col].isLowerCase()) {
                val temp = board.getLegalMoves(false)
                this.movesOfClick.clear()
                for(m in temp) {
                    if(m.fromRow == row && m.fromCol == col) {
                        this.movesOfClick.add(m)
                    }
                }
                activeCell = pairOf(row, col)

//                for(m in this.movesOfClick)
//                    println(m)
                render()
            }
        } else {
            val m = validMove(row, col)
            if(m != null) {
                moveWithUI(m) // rendered
                activeCell = null
                printBoard()
                GameProperties.aiTurn = true
                search()
            } else {
                if(isAlly(board[row, col], board[activeCell!!.first, activeCell!!.second])) {
                    if(activeCell!!.first != row || activeCell!!.second != col) {
                        activeCell = pairOf(row, col)
                        val temp = board.getLegalMoves(false)
                        for(m in temp) {
                            if(m.fromRow == row && m.fromCol == col) {
                                this.movesOfClick.add(m)
                            }
                        }
                    } else activeCell = null
                } else activeCell = null
                render()
            }
        }
    }

    private fun search() {
        val root = TreeNode(board, score, true)
        player?.move(root, depth=4)
    }

    private fun printBoard() {
        println("=========================================================")
        println("    ===========================")
        for(i in 0 until 8) {
            print("    = ")
            for(j in 0..7) {
                print("${board[i, j]}  ")
            }
            println("=")
        }
        println("    ===========================")
        println("=========================================================\n")
    }

    private fun validMove(row: Int, col: Int): Move? {
        var temp: Move? = null
        for(move in this.movesOfClick) {
            if(row == move.toRow && col == move.toCol) {
                temp = move
                break
            }
        }
        this.movesOfClick.clear()
        return temp
    }

    private fun updateBoard(m: Move) {
        val temp = move(board, m, false)
        board = temp.board
        score = temp.score
    }

    fun moveWithUI(m: Move) {
        updateBoard(m)
        if(cells[m.toRow][m.toCol].isOccupied) {
            property.pieceAte.pieceName = cells[m.toRow][m.toCol].piece!!.name
            property.pieceAte.player = !property.aiTurn
            println("eat: ${property.pieceAte.player}")
            property.ate.value++
        }
        moveTrace[0] = pairOf(m.fromRow, m.fromCol)
        moveTrace[1] = pairOf(m.toRow, m.toCol)
        render()
        printBoard()
    }

    fun undoWithUI() {
//        undo()
//        render()
    }

    fun performFirstMove() {
        search()
    }

    fun render() {
        for(i in 0 until 8) {
            for(j in 0 until 8) {
                cells[i][j].resetState()
                if(board[i, j].isLowerCase()) {
                    cells[i][j].piece = pieceMap[board[i, j]]?.let { Piece('w', it) }
                }
                if(board[i, j].isUpperCase()) {
                    cells[i][j].piece = pieceMap[board[i, j]]?.let { Piece('b', it) }
                }
            }
        }
        activeCell?.let{ cells[it.first][it.second].activeEffect.show() }
        if(moveTrace[0].first > -1) {
            cells[moveTrace[0].first][moveTrace[0].second].traceEffect.show()
            cells[moveTrace[1].first][moveTrace[1].second].traceEffect.show()
        }
//        if(whiteCheckmate)
//            cells[whiteKingPos.first][whiteKingPos.second].underAttackedEffect.show()
//        if(blackCheckmate)
//            cells[blackKingPos.first][blackKingPos.second].underAttackedEffect.show()
        for(m in this.movesOfClick) {
            if(cells[m.toRow][m.toCol].isOccupied)
                cells[m.toRow][m.toCol].underAttackedEffect.show()
            cells[m.toRow][m.toCol].movableEffect.show()
        }
    }

//    fun zipBoard(): Board {
//
//    }
}