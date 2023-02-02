package objects

import core.*
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import tornadofx.*

class CartCell(url: String?): StackPane() {
    init {
        if(url != null) {
            this.add(imageview("pieces/$url.png") {
                scaleX = 0.6
                scaleY = 0.6
            })
            opacity = 0.3

        }
        addClass(Styles.cart_cell)
    }
}

class Cart(val inverse: Boolean = false ) {
    val yourItems = GridPane()
    val opponentItems = GridPane()
    private val cells = hashMapOf<String, CartCell>()
    private val count = hashMapOf(
        "wp" to SimpleIntegerProperty(0),
        "wn" to SimpleIntegerProperty(0),
        "wr" to SimpleIntegerProperty(0),
        "wb" to SimpleIntegerProperty(0),
        "wq" to SimpleIntegerProperty(0),
        "wk" to SimpleIntegerProperty(0),
        "bp" to SimpleIntegerProperty(0),
        "bn" to SimpleIntegerProperty(0),
        "br" to SimpleIntegerProperty(0),
        "bb" to SimpleIntegerProperty(0),
        "bq" to SimpleIntegerProperty(0),
        "bk" to SimpleIntegerProperty(0)
    )

    init {
        load(WHITE, BLACK, yourItems)
        load(BLACK, WHITE, opponentItems)
    }

    private fun load(c1: Char, c2: Char, shelf: GridPane) {
        cells["${c1}p"] = CartCell("${c2}_p").apply { shelf.add(this, 0, 0) }
        cells["${c1}n"] = CartCell("${c2}_n").apply { shelf.add(this, 1, 0) }
        cells["${c1}r"] = CartCell("${c2}_r").apply { shelf.add(this, 2, 0) }
        cells["${c1}b"] = CartCell("${c2}_b").apply { shelf.add(this, 3, 0) }
        cells["${c1}q"] = CartCell("${c2}_q").apply { shelf.add(this, 4, 0) }
        cells["${c1}k"] = CartCell("${c2}_k").apply { shelf.add(this, 5, 0) }

        shelf.add(Label().apply{
            textProperty().bind(count["${c1}p"]!!.asString())
            paddingLeft = 25
            font = Font(20.0)
        }, 0, 1)
        shelf.add(Label().apply{
            textProperty().bind(count["${c1}n"]!!.asString())
            paddingLeft = 25
            font = Font(20.0)
        }, 1, 1)
        shelf.add(Label().apply{
            textProperty().bind(count["${c1}r"]!!.asString())
            paddingLeft = 25
            font = Font(20.0)
        }, 2, 1)
        shelf.add(Label().apply{
            textProperty().bind(count["${c1}b"]!!.asString())
            paddingLeft = 25
            font = Font(20.0)
        }, 3, 1)
        shelf.add(Label().apply{
            textProperty().bind(count["${c1}q"]!!.asString())
            paddingLeft = 25
            font = Font(20.0)
        }, 4, 1)
        shelf.add(Label().apply{
            textProperty().bind(count["${c1}k"]!!.asString())
            paddingLeft = 25
            font = Font(20.0)
        }, 5, 1)
    }

    fun updateCart(info: PieceInfo) {
        val key = "${swapColor(info.color)}${info.type}"
        println("key: $key")
        cells[key]!!.opacity = 1.0
        count[key]?.apply {
            set(value + 1)
        }
    }
}

inline fun cart(inverse: Boolean = false, op: Cart.() -> Unit = {}) = Cart(inverse).apply(op)