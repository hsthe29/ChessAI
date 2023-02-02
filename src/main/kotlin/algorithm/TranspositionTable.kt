package algorithm

import Move
import core.Board
import core.Entry
import kotlin.random.Random

val ZobristTable = Array(8) { Array(8) {LongArray(12)} }

fun indexOf(piece: Char) = when (piece) {
    'P' -> 0
    'N' -> 1
    'B' -> 2
    'R' -> 3
    'Q' -> 4
    'K' -> 5
    'p' -> 6
    'n' -> 7
    'b' -> 8
    'r' -> 9
    'q' -> 10
    'k' -> 11
    else -> -1
}

fun initTable() {
    for (i in 0..7)
        for (j in 0..7)
            for (k in 0..11)
                ZobristTable[i][j][k] = Random.nextLong(Long.MIN_VALUE, Long.MAX_VALUE)
}

fun computeHash(board: Array<CharArray>): Long {
    var h = 0L
    for (i in 0 until 8) {
        for (j in 0 until 8) {
            if (board[i][j] != '-') {
                val piece = indexOf(board[i][j])
                h = h xor ZobristTable[i][j][piece]
            }
        }
    }
    return h;
}

fun computeHash(board: Board): Long {
    var h = 0L
    for (i in 0 until 8) {
        for (j in 0 until 8) {
            if (board[i, j] != '-') {
                val piece = indexOf(board[i, j])
                h = h xor ZobristTable[i][j][piece]
            }
        }
    }
    return h;
}
object TranspositionTable {
    private val tpScore = hashMapOf<Long, Entry<Int, Int>>()
    private val tpMove = hashMapOf<Long, Move>()
    private val blackMoves = Random.nextLong(Long.MIN_VALUE, Long.MAX_VALUE)

    init {
        initTable()
    }

    fun getScore(zobristCode: Long, isMaxMove: Boolean, depth: Int, root: Boolean): Entry<Int, Int>? {
        val entry = if(isMaxMove)
            zobristCode xor blackMoves
        else zobristCode
        val offset = if(root) {
            29012002L*depth
        } else ((29012002L*depth).inv() % (1e9+7).toLong())

        return this.tpScore[zobristCode xor offset]
    }

    fun getMove(zobristCode: Long, isMaxMove: Boolean): Move? {
        val entry = if(isMaxMove)
            zobristCode xor blackMoves
        else zobristCode

        return this.tpMove[zobristCode]
    }

    fun setScore(zobristCode: Long, isMaxMove: Boolean, depth: Int, root: Boolean, value: Entry<Int, Int>) {
        val entry = if(isMaxMove)
            zobristCode xor blackMoves
        else zobristCode
        val offset = if(root) {
            29012002L*depth
        } else ((29012002L*depth).inv() % (1e12+7).toLong())

        if(this.tpScore.size > TABLE_SIZE)
            this.tpScore.clear()

        this.tpScore[zobristCode xor offset] = value
    }

    fun setMove(zobristCode: Long, isMaxMove: Boolean, value: Move) {
        val entry = if(isMaxMove)
            zobristCode xor blackMoves
        else zobristCode

        if(this.tpMove.size > TABLE_SIZE)
            this.tpMove.clear()

        this.tpMove[zobristCode] = value
    }
}