package core

import javafx.scene.image.Image

enum class GameMode {
    PvsCOM, COMvsCOM
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

val rookDirections = listOf(1 to 0, 0 to 1, -1 to 0, 0 to -1)
val bishopDirections = listOf(1 to 1, -1 to 1, 1 to -1, -1 to -1)
val knightDirections = listOf(1 to 2, 1 to -2, -1 to 2, -1 to -2, 2 to 1, 2 to -1, -2 to 1, -2 to -1)
val kingDirections = listOf(1 to 0, 0 to 1, 0 to -1, -1 to 0, -1 to -1, -1 to 1, 1 to 1, 1 to -1)

fun onBoard(x: Int, y: Int) = (x in 0 until 8) && (y in 0 until 8)
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
