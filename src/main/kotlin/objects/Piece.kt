package objects

import javafx.scene.image.ImageView


class Piece(val color: Char, val name: String) {
    val image = ImageView("pieces/${color}_${name}_1x.png")
}