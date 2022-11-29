package objects

import CellPos
import Styles
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import tornadofx.*
import view.GameView

class ChessCell(row: Int, column: Int): StackPane() {
    val cellPos = CellPos(row, column)
    var piece: Piece? = null
        set(value) {
            field = value
            if(this.children.isNotEmpty()) this.children.removeLast()
            if(value != null) {
                this.add(ImageView(value.image))
            }
        }
    val pieceColor: String
        get() = if(this.piece != null) this.piece!!.colorName else ""
    val isOccupied: Boolean
        get() = piece != null

    init {
        addClass(if (((row - column) and 1) == 0) Styles.board_cell_light else Styles.board_cell_dark)
    }

    fun releasePiece(): Piece? {
        val temp = this.piece
        this.piece = null
        return temp
    }

}

class ChesssBoard(isWhite: Boolean, val view: GameView): GridPane() {

    val cells = Array(8) {i -> Array(8) { j -> ChessCell(i, j)} }
    private val movableList = mutableListOf<CellPos>()
    var activeCell: ChessCell? = null
        set(value) {
            if(field != null) {
                field!!.removeClass(Styles.chess_cell_active)
            }
            field = value
            if(field != null) {
                field!!.addClass(Styles.chess_cell_active)
            }
        }

    init {
        this.padding = Insets(10.0, 10.0, 10.0, 10.0)
        for(i in 0 until 8)
            for(j in 0 until 8) {
                if(isWhite) {
                    this.add(cells[i][j], j, i)
                } else this.add(cells[i][j], 7 - j, 7 - i)
                cells[i][j].setOnMouseClicked {
                        onCellClick(i, j)
                }
            }
        resetBoardState()
    }

    fun resetBoardState() {
        for(i in 0 until 8)
            for(j in 0 until 8)
                cells[i][j].releasePiece()

        this.cells[0][0].piece = Rook(false)
        this.cells[0][1].piece = Knight(false)
        this.cells[0][2].piece = Bishop(false)
        this.cells[0][3].piece = Queen(false)
        this.cells[0][4].piece = King(false)
        this.cells[0][5].piece = Bishop(false)
        this.cells[0][6].piece = Knight(false)
        this.cells[0][7].piece = Rook(false)
        for(i in 0 until 8)
            this.cells[1][i].piece = Pawn(false)

        this.cells[7][0].piece = Rook(true)
        this.cells[7][1].piece = Knight(true)
        this.cells[7][2].piece = Bishop(true)
        this.cells[7][3].piece = Queen(true)
        this.cells[7][4].piece = King(true)
        this.cells[7][5].piece = Bishop(true)
        this.cells[7][6].piece = Knight(true)
        this.cells[7][7].piece = Rook(true)
        for(i in 0 until 8)
            this.cells[6][i].piece = Pawn(true)
    }

    fun onCellClick(row: Int, col: Int) {
        val clickedCell= this.cells[row][col]
        // if piece is selected && user didn't click on allied piece

        if (activeCell != null && activeCell!!.piece != null && clickedCell.pieceColor != activeCell!!.pieceColor) {

            val p = MoveInfo(activeCell!!.cellPos.rowId, activeCell!!.cellPos.columnId, row, col)

            if(this.performMove(p)) {
                with(view.controller) {
                    this.isAIMove = !this.isAIMove
                    this.currentColor = if(this.currentColor == "w") "b" else "w"
                }
                this.hideFeasibleMoves()
                this.activeCell = null
//                view.controller.turn.value = !view.controller.turn.value
            } else {
                this.hideFeasibleMoves()
                this.activeCell = null
            }
            if(view.controller.isAIMove)
                view.aiPlayer.move()
        } else {
            this.hideFeasibleMoves()
            //if there's a piece on the selected square when no active square
            if (this.cells[row][col].piece != null) {
                if(this.cells[row][col].piece!!.colorName == view.controller.currentColor) {
                    //make active square clicked square
                    this.activeCell = this.cells[row][col]
                    this.displayFeasibleMoves(row, col)
                }
            }
        }
    }

    private fun validMove(p: MoveInfo?): Boolean {
        // TODO:
        //  -Check if player's king is put into check
        //  -Pawn logic (Possibly implement as part of pawn's movelist?)
        //  -Castling logic

        // Check for null move
        if (p == null) {
            return false
        }
        // Note: Ideally we would check the space coordinates
        //       beforehand, but the try-catch blocks below were
        //       easier to implement.

        // Check if oldSpace in range
        val oldSpace: ChessCell = try {
            cells[p.oldRow][p.oldCol]
        } catch (e: NullPointerException) {
            return false
        }
        // Check if newSpace in range
        val newSpace: ChessCell = try {
            cells[p.newRow][p.newCol]
        } catch (e: NullPointerException) {
            return false
        }
        // Check if oldSpace is empty; (no movable piece)
        if (!oldSpace.isOccupied) {
            return false
        }
        // Check piece's move list
        val piece = oldSpace.piece

        for(cp in this.movableList) {
            if(p.newRow == cp.rowId && p.newCol == cp.columnId) {
                val tempCell = this.cells[p.newRow][p.newCol]
                if(tempCell.isOccupied) {
                    // 11 p1
                    view.controller.ediblePieces.player = !view.controller.isAIMove
                    view.controller.ediblePieces.pieceName = tempCell.piece!!.name
                    view.controller.ediblePieces.mode = 1
                    view.controller.isMessaged.value = !view.controller.isMessaged.value
                }
                piece!!.hasMoved = true

                return true
            }
        }
        return false
    }

    private fun performMove(p: MoveInfo): Boolean {
        return if (validMove(p)) {
            val oldCell = cells[p.oldRow][p.oldCol]
            val newCell = cells[p.newRow][p.newCol]
            newCell.piece = oldCell.releasePiece()
            true
        } else false
    }

    private fun hideFeasibleMoves() {
        if(this.movableList.isNotEmpty()) {
            this.movableList.forEach {
                this.cells[it.rowId][it.columnId].removeClass(Styles.movable);
                this.cells[it.rowId][it.columnId].removeClass(Styles.attack)
            }
            this.movableList.clear()
        }
    }

    private fun displayFeasibleMoves(row: Int, col: Int) {
        val cell = cells[row][col]
        val piece = cell.piece
        val moves = piece!!.moveList
        val rvs = mutableListOf<MoveList>()
        var multiMoveCount: Int
        var stretchedMoveX: Int
        var stretchedMoveY: Int
        if(piece.name == "pawn") {
            for(m in moves) {
                if(m == MoveList.UP_LEFT || m == MoveList.UP_RIGHT || m == MoveList.DOWN_LEFT || m == MoveList.DOWN_RIGHT) {
                    val newRow = row + m.row
                    val newCol = col + m.col
                    rvs.add(m)
                    if(newRow in 0..7 && newCol in 0..7) {
                        val tempCell = this.cells[newRow][newCol]
                        if(tempCell.isOccupied) {
                            if(tempCell.piece!!.colorName != cell.piece!!.colorName) {
                                tempCell.addClass(Styles.attack)
                                this.movableList.add(CellPos(newRow, newCol))
                            }
                        }
                    }
                }
            }
        }
        moves.removeAll(rvs)
        for (m in moves) {
            multiMoveCount = 1
            if (!piece.isSingleMove()) { multiMoveCount = 8 }
            var hasCollided = false
            for (c in 1..multiMoveCount) {
                //if the prior run hit a piece of opponent's color, done with this move
                if (hasCollided) {
                    break
                }
                //stretches a base move out to see if it matches the move made
                stretchedMoveX = m.row * c
                stretchedMoveY = m.col * c
                val newRow = row + stretchedMoveX
                val newCol = col + stretchedMoveY
                if(newRow in 0..7 && newCol in 0..7) {
                    val tempCell = this.cells[newRow][newCol]
                    if(tempCell.isOccupied) {
                        hasCollided = true
                        if(tempCell.piece!!.colorName != cell.piece!!.colorName && piece.name != "pawn") {
                            tempCell.addClass(Styles.attack)
                            this.movableList.add(CellPos(newRow, newCol))
                        }
                    } else {
                        tempCell.addClass(Styles.movable)
                        this.movableList.add(CellPos(newRow, newCol))
                    }
                }
            }
        }
    }
}

inline fun EventTarget.chessBoard(isWhite: Boolean, view: GameView, op: ChesssBoard.() -> Unit = {}) = opcr(this, ChesssBoard(isWhite, view), op)