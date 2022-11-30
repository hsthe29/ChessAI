package objects

import javafx.scene.image.Image


class Piece(val color: Char, val name: String) {
    var hasMoved = false
    val image: Image = Image("pieces/${color}_${name}_1x.png")
}