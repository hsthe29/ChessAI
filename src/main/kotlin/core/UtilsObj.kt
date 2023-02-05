package core

import javafx.scene.image.ImageView
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

enum class GameMode {
    PvsCOM, COMvsCOM
}

enum class EndType {
    NORMAL, DRAW
}

open class Object<T> {
    private val properties = hashMapOf<String, KProperty1<Object<T>, T>>()

    fun mapping() {
        this::class.memberProperties.forEach{ properties[it.name] = it as KProperty1<Object<T>, T> }
    }

    operator fun get(name: String): T = properties[name]!!.get(this)
    operator fun get(name: Char): T = this[name.toString()]

    operator fun contains(name: String) = name in properties.keys

    override fun toString()
            = properties.map { "${it.key}: ${it.value.get(this)}" }
        .joinToString(", ")
}
open class MutableObject<T> {
    private val properties = hashMapOf<String, KMutableProperty1<MutableObject<T>, T>>()

    fun mapping() {
        this::class.memberProperties.forEach{ properties[it.name] = it as KMutableProperty1<MutableObject<T>, T> }
    }

    operator fun get(name: String): T = properties[name]!!.get(this)
    operator fun get(name: Char): T = this[name.toString()]

    operator fun set(name: String, value: T) {
        properties[name]?.set(this, value)
    }
    operator fun set(name: Char, value: T) {
        this[name.toString()] = value
    }

    override fun toString()
            = properties.map { "${it.key}: ${it.value.get(this)}" }
        .joinToString(", ")
}

data class MoveData(val piece: Char,
                    val move: String,
                    val captured: Char,
                    val color: Char,
                    val eval: Double,
                    val nodeVisited: Int,
                    val pFen: String,
                    val from: Int,
                    val to: Int)
data class Statistic(val color: String, var score: Int, var moves: Int, var totalTime: Double, var avgTime: Double)

class Piece(color: Char, type: Char) {
    val image = ImageView("pieces/${color}_${type}.png")
}
data class PieceInfo(var color: Char, var type: Char): MutableObject<Char>(){
    init { mapping() }
}

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
