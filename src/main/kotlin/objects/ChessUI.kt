package objects

import CellPos
import Styles
import engine.Engine
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
    val pieceColor: Char
        get() = if(this.piece != null) this.piece!!.color else '-'
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

class ChessUI(isWhite: Boolean, val game: Engine): GridPane() {
    init {
        this.padding = Insets(10.0, 10.0, 10.0, 10.0)
        for(i in 0 until 8) {
            for (j in 0 until 8) {
                this.add(game.cells[i][j], j, i)
            }
        }
    }
}

inline fun EventTarget.chessUI(isWhite: Boolean, game: Engine, op: ChessUI.() -> Unit = {}) = opcr(this, ChessUI(isWhite, game), op)