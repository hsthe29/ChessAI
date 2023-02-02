package objects

import javafx.scene.image.ImageView

class Piece(val color: Char, val type: Char) {
    val image = ImageView("pieces/${color}_${type}.png")
}