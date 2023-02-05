package core

import javafx.scene.image.Image
import javafx.scene.image.ImageView

enum class GameMode {
    PvsCOM, COMvsCOM
}

enum class EndType {
    NORMAL, DRAW
}

object EndMessage {
    var type = EndType.NORMAL
    var color = "WHITE"
}
data class MoveData(val piece: Char, val move: String, val captured: Char, val color: Char, val eval: Double, val nodeVisited: Int)

class Piece(val color: Char, val type: Char) {
    val image = ImageView("pieces/${color}_${type}.png")
}
data class PieceInfo(var color: Char, var type: Char): MutableObject<Char>(){
    init { mapping() }
}

data class Entry<A, B>(var lower: A, var upper: B)

val darkBackground = Image("backgrounds/dark_color.png")
val lightBackground = Image("backgrounds/light_color.png")
val movableImg = Image("backgrounds/movable.png")
val attackedImg = Image("backgrounds/under_attacked.png")
val traceImg = Image("backgrounds/trace.png")

fun isEnemy(from: Char, to: Char) = (from.isUpperCase() && to.isLowerCase()) || (from.isLowerCase() && to.isUpperCase())

fun isAlly(a: Char, b: Char) = (a.isLowerCase() && b.isLowerCase()) || (a.isUpperCase() && b.isUpperCase())
fun ally(a: PieceInfo?, b: PieceInfo?): Boolean {
    if(a == null || b == null) return false
    return a.color == b.color
}

fun enemy(a: PieceInfo?, b: PieceInfo?): Boolean {
    if(a == null || b == null) return false
    return a.color != b.color
}

fun swapColor(color: Char) = if(color == WHITE) BLACK else WHITE
fun isDigit(c: Char) = c in "0123456789"

fun rank(i: Int) = i shr 4

fun file(i: Int) = i and 15

fun sqLoc(order: Int): String {
    val f = file(order)
    val r = rank(order)
    return "abcdefgh".substring(f, f + 1) + "87654321".substring(r, r + 1)
}
