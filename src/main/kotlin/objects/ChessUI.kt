package objects

import engine.*
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import tornadofx.add
import tornadofx.hide
import tornadofx.imageview
import tornadofx.opcr

class ChessCell(val row: Int, val col: Int): StackPane() {
    var piece: Piece? = null
        set(value) {
            if(field != null)
                this.children.remove(field!!.image)
            field = value
            if(value != null) {
                this.add(value.image)
            }
        }
    val movableEffect: ImageView
    val underAttackedEffect: ImageView
//    val checkmateEffect: ImageView
    val traceEffect: ImageView
    val activeEffect: ImageView
    val pieceColor: Char
        get() = if(this.piece != null) this.piece!!.color else '-'
    val isOccupied: Boolean
        get() = piece != null

    init {
        add(ImageView(if (((row - col) and 1) == 0) lightBackground else darkBackground))
        movableEffect = imageview(movableBackground) {
            opacity = 0.8
        }
        underAttackedEffect = imageview(underAttackedBackground) {
            opacity = 0.6
        }
        traceEffect = imageview(traceBackground)
        activeEffect = imageview(traceBackground) {
            opacity = 0.8
        }
        resetEffect()
        setPrefSize(80.0, 80.0)
    }

    fun resetState() {
        this.piece = null
        resetEffect()
    }
    fun resetEffect() {
        this.movableEffect.hide()
        this.underAttackedEffect.hide()
        this.traceEffect.hide()
        this.activeEffect.hide()
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