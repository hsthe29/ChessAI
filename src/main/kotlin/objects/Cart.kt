package objects

import javafx.beans.property.SimpleIntegerProperty
import javafx.event.EventTarget
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.scene.text.TextAlignment
import tornadofx.*
import view.GameView

class CartCell(url: String?): StackPane() {
    init {
        if(url != null)
            this.add(imageview("pieces/" + url + "_1x.png") {
                scaleX = 0.6
                scaleY = 0.6
            })
        addClass(Styles.cart_cell)
        if(url != null)
            opacity = 0.3
    }
}

class Cart(opponentIsWhite: Boolean, val view: GameView): GridPane() {
    val cells = hashMapOf<String, CartCell>()
    val count = hashMapOf(
        "pawn" to SimpleIntegerProperty(0),
        "knight" to SimpleIntegerProperty(0),
        "rook" to SimpleIntegerProperty(0),
        "bishop" to SimpleIntegerProperty(0),
        "queen" to SimpleIntegerProperty(0),
        "king" to SimpleIntegerProperty(0)
    )
    private val color: String
    init {
        color = if(opponentIsWhite) "w" else "b"
        cells["${color}_pawn"] = CartCell("${color}_pawn").also { this.add(it, 0, 0) }
        cells["${color}_knight"] = CartCell("${color}_knight").also { this.add(it, 1, 0) }
        cells["${color}_rook"] = CartCell("${color}_rook").also { this.add(it, 2, 0) }
        cells["${color}_bishop"] = CartCell("${color}_bishop").also { this.add(it, 3, 0) }
        cells["${color}_queen"] = CartCell("${color}_queen").also { this.add(it, 4, 0) }
        cells["${color}_king"] = CartCell("${color}_king").also { this.add(it, 5, 0) }

        this.add(CartCell(null).apply {
            this.add(Label().apply{
                textProperty().bind(count["pawn"]!!.asString())
                textAlignment = TextAlignment.JUSTIFY
            })
        }, 0, 1)
        this.add(CartCell(null).apply {
            opacity = 1.0
            this.add(Label().apply{
                textProperty().bind(count["knight"]!!.asString())
                textAlignment = TextAlignment.JUSTIFY
            })
        }, 1, 1)
        this.add(CartCell(null).apply {
            opacity = 1.0
            this.add(Label().apply{
                textProperty().bind(count["rook"]!!.asString())
                textAlignment = TextAlignment.JUSTIFY
            })
        }, 2, 1)
        this.add(CartCell(null).apply {
            opacity = 1.0
            this.add(Label().apply{
                textProperty().bind(count["bishop"]!!.asString())
                textAlignment = TextAlignment.JUSTIFY
            })
        }, 3, 1)
        this.add(CartCell(null).apply {
            opacity = 1.0
            this.add(Label().apply{
                textProperty().bind(count["queen"]!!.asString())
                textAlignment = TextAlignment.JUSTIFY
            })
        }, 4, 1)
        this.add(CartCell(null).apply {
            opacity = 1.0
            this.add(Label().apply{
                textProperty().bind(count["king"]!!.asString())
                textAlignment = TextAlignment.JUSTIFY
            })
        }, 5, 1)
    }

    fun updateCart(pieceName: String, mode: Int) {
        if(mode == 1) {
            cells["${color}_$pieceName"]?.opacity = 1.0
            count[pieceName]?.apply {
                set(value + 1)
            }
            if(pieceName == "king") {
                view.showNotification(if(this.color == "b") "Congratulations!. You Win!" else "Unfortunately!. You Lose!")
            }
        } else {

        }
    }
}

inline fun EventTarget.cart(opponentIsWhite: Boolean, view: GameView, op: Cart.() -> Unit = {}) = opcr(this, Cart(opponentIsWhite, view), op)