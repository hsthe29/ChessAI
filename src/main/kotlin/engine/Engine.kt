package engine

import Styles
import isAlly
import moveFunction
import objects.ChessCell
import objects.Move
import objects.Piece
import objects.Player
import pairOf
import pieceMap
import tornadofx.*

class Engine() {
    val board = arrayOf(
        charArrayOf('R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'),
        charArrayOf('P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'),
        charArrayOf('-', '-', '-', '-', '-', '-', '-', '-'),
        charArrayOf('-', '-', '-', '-', '-', '-', '-', '-'),
        charArrayOf('-', '-', '-', '-', '-', '-', '-', '-'),
        charArrayOf('-', '-', '-', '-', '-', '-', '-', '-'),
        charArrayOf('p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'),
        charArrayOf('r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'),
    )
    val cells = Array(8) {i -> Array(8) { j -> ChessCell(i, j) } }
    val property = GameProperties
    var activeCell: Pair<Int, Int>? = null
    val movesOfClick = mutableListOf<Move>()
    val moveHistory = mutableListOf<Pair<Move, Char>>()
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
        if(activeCell == null) {
            if(cells[row][col].isOccupied && board[row][col].isLowerCase()) {
                activeCell = pairOf(row, col)
                moveFunction[board[row][col]]?.let { it(row, col, board, movesOfClick) }
                showMoves()
            }
        } else {
            if(validMove(row, col)) { // hide moves
                moveWithUI(movesOfClick.first())
                movesOfClick.clear()
                activeCell = null
                printBoard()
                player?.move()
            } else {
                if(board[row][col] != '-' && isAlly(board[row][col], board[activeCell!!.first][activeCell!!.second])) {
                    if(activeCell!!.first != row || activeCell!!.second != col) {
                        activeCell = pairOf(row, col)
                        moveFunction[board[row][col]]?.let { it(row, col, board, movesOfClick) }
                        showMoves()
                    } else activeCell = null
                } else activeCell = null
            }
        }
    }

    private fun printBoard() {
        println("=========================================================")
        println("    ===========================")
        board.forEach {
            print("    = ")
            for(i in 0..7) {
                print("${it[i]}  ")
            }
            println("=")
        }
        println("    ===========================")
        println("=========================================================\n")
    }

    private fun validMove(row: Int, col: Int): Boolean {
        var temp: Move? = null
        for(move in movesOfClick) {
            if(row == move.endRow && col == move.endCol) {
                temp = move
                break
            }
        }
        hideMoves()
        movesOfClick.clear()
        if(temp != null) {
            movesOfClick.add(temp)
        }
        return temp != null
    }

    private fun showMoves() {
        cells[activeCell!!.first][activeCell!!.second].addClass(Styles.chess_cell_active)

        for(move in movesOfClick) {
            with(cells[move.endRow][move.endCol]) {
                addClass(Styles.movable)
            }
        }
    }

    private fun hideMoves() {
        cells[activeCell!!.first][activeCell!!.second].removeClass(Styles.chess_cell_active)
        for(move in movesOfClick) {
            with(cells[move.endRow][move.endCol]) {
                removeClass(Styles.movable)
            }
        }
    }

    fun getAllFeasibleMoves(isAITurn: Boolean): Array<Move> {
        val moves = mutableListOf<Move>()
        for(i in 0 until 8) {
            for(j in 0 until 8) {
                if(isAITurn && board[i][j].isUpperCase()) {
                    moveFunction[board[i][j].lowercaseChar()]?.let { it(i, j, board, moves) }
                }
                if(!isAITurn && board[i][j].isLowerCase()) {
                    moveFunction[board[i][j].lowercaseChar()]?.let { it(i, j, board, moves) }
                }
            }
        }
        return moves.toTypedArray()
    }

    fun move(m: Move) {
        moveHistory.add(pairOf(m, board[m.endRow][m.endCol]))
        board[m.endRow][m.endCol] = board[m.startRow][m.startCol]
        board[m.startRow][m.startCol] = '-'
    }

    fun undo() {
        if(moveHistory.isNotEmpty()) {
            val (m, char) = moveHistory.removeLast()
            board[m.startRow][m.startCol] = board[m.endRow][m.endCol]
            board[m.endRow][m.endCol] = char
        }
        else println("Error!")
    }

    fun moveWithUI(m: Move) {
        move(m)
        if(cells[m.endRow][m.endCol].isOccupied) {
            property.pieceAte.pieceName = cells[m.endRow][m.endCol].piece!!.name
            property.pieceAte.player = !property.aiTurn
            property.ate.value++
        }
        cells[m.endRow][m.endCol].piece = cells[m.startRow][m.startCol].releasePiece()
        moveHistory.clear()
        printBoard()
    }

    fun performFirstMove() {
        player?.move()
    }

    fun render() {
        for(i in 0 until 8) {
            for(j in 0 until 8) {
                if(board[i][j].isLowerCase()) {
                    cells[i][j].piece = pieceMap[board[i][j]]?.let { Piece('w', it) }
                }
                if(board[i][j].isUpperCase()) {
                    cells[i][j].piece = pieceMap[board[i][j]]?.let { Piece('b', it) }
                }
            }
        }
    }
    fun resetUI() {
        for(i in 0 until 8)
            for(j in 0 until 8)
                cells[i][j].releasePiece()
    }
}