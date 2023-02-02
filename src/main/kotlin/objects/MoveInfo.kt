import core.*

data class SpecialMoves(val enPassant: Pair<Int, Int>?, var wk: Boolean, var wq: Boolean, var bk: Boolean, var bq: Boolean)

data class Move(var color: Char, var from: Int,
           var to: Int, var flags: Int, var piece: Char,
           var promotion: Char? = null,
           var captured: Char? = null): MutableObject<Any>() {
    init { mapping() }
}

class MoveInfo(val move: Move,
               val kings: WBMark<Int>,
               val turn: Char,
               val castling: WBMark<Int>,
               val epSquare: Int,
               val halfMoves: Int,
               val moveNumber: Int) : Object<Any>() {
    init { mapping() }
}