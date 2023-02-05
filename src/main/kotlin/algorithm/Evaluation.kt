package algorithm

import Move
import core.*

fun Array<IntArray>.copy() = Array(size) { get(it).clone() }

val weights = hashMapOf('p' to 100, 'n' to 280, 'b' to 320, 'r' to 479, 'q' to 929, 'k' to 60000, 'e' to 60000)
val pst_w = hashMapOf(
    'p' to arrayOf(
        intArrayOf(100, 100, 100, 100, 105, 100, 100, 100),
        intArrayOf(78, 83, 86, 73, 102, 82, 85, 90),
        intArrayOf(7, 29, 21, 44, 40, 31, 44, 7),
        intArrayOf(-17, 16, -2, 15, 14, 0, 15, -13),
        intArrayOf(-26, 3, 10, 9, 6, 1, 0, -23),
        intArrayOf(-22, 9, 5, -11, -10, -2, 3, -19),
        intArrayOf(-31, 8, -7, -37, -36, -14, 3, -31),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
    ),
    'n' to arrayOf(
        intArrayOf(-66, -53, -75, -75, -10, -55, -58, -70),
        intArrayOf(-3, -6, 100, -36, 4, 62, -4, -14),
        intArrayOf(10, 67, 1, 74, 73, 27, 62, -2),
        intArrayOf(24, 24, 45, 37, 33, 41, 25, 17),
        intArrayOf(-1, 5, 31, 21, 22, 35, 2, 0),
        intArrayOf(-18, 10, 13, 22, 18, 15, 11, -14),
        intArrayOf(-23, -15, 2, 0, 2, 0, -23, -20),
        intArrayOf(-74, -23, -26, -24, -19, -35, -22, -69),
    ),
    'b' to arrayOf(
        intArrayOf(-59, -78, -82, -76, -23, -107, -37, -50),
        intArrayOf(-11, 20, 35, -42, -39, 31, 2, -22),
        intArrayOf(-9, 39, -32, 41, 52, -10, 28, -14),
        intArrayOf(25, 17, 20, 34, 26, 25, 15, 10),
        intArrayOf(13, 10, 17, 23, 17, 16, 0, 7),
        intArrayOf(14, 25, 24, 15, 8, 25, 20, 15),
        intArrayOf(19, 20, 11, 6, 7, 6, 20, 16),
        intArrayOf(-7, 2, -15, -12, -14, -15, -10, -10)
    ),
    'r' to arrayOf(
        intArrayOf(35, 29, 33, 4, 37, 33, 56, 50),
        intArrayOf(55, 29, 56, 67, 55, 62, 34, 60),
        intArrayOf(19, 35, 28, 33, 45, 27, 25, 15),
        intArrayOf(0, 5, 16, 13, 18, -4, -9, -6),
        intArrayOf(-28, -35, -16, -21, -13, -29, -46, -30),
        intArrayOf(-42, -28, -42, -25, -25, -35, -26, -46),
        intArrayOf(-53, -38, -31, -26, -29, -43, -44, -53),
        intArrayOf(-30, -24, -18, 5, -2, -18, -31, -32)
    ),
    'q' to arrayOf(
        intArrayOf(6, 1, -8, -104, 69, 24, 88, 26),
        intArrayOf(14, 32, 60, -10, 20, 76, 57, 24),
        intArrayOf(-2, 43, 32, 60, 72, 63, 43, 2),
        intArrayOf(1, -16, 22, 17, 25, 20, -13, -6),
        intArrayOf(-14, -15, -2, -5, -1, -10, -20, -22),
        intArrayOf(-30, -6, -13, -11, -16, -11, -16, -27),
        intArrayOf(-36, -18, 0, -19, -15, -15, -21, -38),
        intArrayOf(-39, -30, -31, -13, -31, -36, -34, -42)
    ),
    'k' to arrayOf(
        intArrayOf(4, 54, 47, -99, -99, 60, 83, -62),
        intArrayOf(-32, 10, 55, 56, 56, 55, 10, 3),
        intArrayOf(-62, 12, -57, 44, -67, 28, 37, -31),
        intArrayOf(-55, 50, 11, -4, -19, 13, 0, -49),
        intArrayOf(-55, -43, -52, -28, -51, -47, -8, -50),
        intArrayOf(-47, -42, -43, -79, -64, -32, -29, -32),
        intArrayOf(-4, 3, -14, -50, -57, -18, 13, 4),
        intArrayOf(17, 30, -3, -14, 6, -1, 40, 18),
    ),

    // Endgame King Table
    'e' to arrayOf(
        intArrayOf(-50, -40, -30, -20, -20, -30, -40, -50),
        intArrayOf(-30, -20, -10, 0, 0, -10, -20, -30),
        intArrayOf(-30, -10, 20, 30, 30, 20, -10, -30),
        intArrayOf(-30, -10, 30, 40, 40, 30, -10, -30),
        intArrayOf(-30, -10, 30, 40, 40, 30, -10, -30),
        intArrayOf(-30, -10, 20, 30, 30, 20, -10, -30),
        intArrayOf(-30, -30, 0, 0, 0, 0, -30, -30),
        intArrayOf(-50, -30, -30, -30, -30, -30, -30, -50),
    )
)
val pst_b = hashMapOf(
    'p' to pst_w['p']!!.copy().reversedArray(),
    'n' to pst_w['n']!!.copy().reversedArray(),
    'b' to pst_w['b']!!.copy().reversedArray(),
    'r' to pst_w['r']!!.copy().reversedArray(),
    'q' to pst_w['q']!!.copy().reversedArray(),
    'k' to pst_w['k']!!.copy().reversedArray(),
    'e' to pst_w['e']!!.copy().reversedArray(),
)

var pstOpponent = hashMapOf('w' to pst_b, 'b' to pst_w)
var pstYou = hashMapOf('w' to pst_w, 'b' to pst_b)
var globalSum = 0

/*
 * Evaluates the board at this point in time,
 * using the material weights and piece square tables.
 */
fun evaluateBoard(engine: ChessEngine, move: Move, prevSum: Int, color: Char): Int {
    var sum = prevSum

    val inCheckmate = engine.inCheck && engine.lenMoves == 0
    val inStalemate = !engine.inCheck && engine.lenMoves == 0
    val inDraw = engine.halfMoves >= 100 || inStalemate || engine.insufficientMaterial() || engine.inThreefoldRepetition()
    if (inCheckmate) {
        // Opponent is in checkmate (good for us)
        return if (move.color == color) 100000
        // Our king's in checkmate (bad for us)
        else -100000
    }
    if (inDraw) {
        println("in draw")
        return 0 }
    if (engine.inCheck) {
        // Opponent is in check (good for us)
        sum += if (move.color == color) 50
        // Our king's in check (bad for us)
        else -50
    }

    val from = intArrayOf(
        8 - sqLoc(move.from)[1].digitToInt(),
        sqLoc(move.from).codePointAt(0) - 'a'.code
    )
    val to = intArrayOf(
        8 - sqLoc(move.to)[1].digitToInt(),
        sqLoc(move.to).codePointAt(0) - 'a'.code
    )

    // Change endgame behavior for kings
    if (sum < -1500) {
        if (move.piece == 'k') {
            move.piece = 'e';
        }
        // Kings can never be captured
         else if (move.captured == 'k') {
           move.captured = 'e';
         }
    }

    if (move.captured != null) {
        // Opponent piece was captured (good for us)
        if (move.color == color) {
            sum +=
                weights[move.captured]!! +
                        pstOpponent[move.color]!![move.captured]!![to[0]][to[1]];
        }
        // Our piece was captured (bad for us)
        else {
            sum -=
                weights[move.captured]!! +
                        pstYou[move.color]!![move.captured]!![to[0]][to[1]];
        }
    }

    if ((BITS.PROMOTION and move.flags) != 0) {
        // NOTE: promote to queen for simplicity
        move.promotion = 'q';

        // Our piece was promoted (good for us)
        if (move.color == color) {
            sum -= weights[move.piece]!! + pstYou[move.color]!![move.piece]!![from[0]][from[1]];
            sum += weights[move.promotion]!! + pstYou[move.color]!![move.promotion]!![to[0]][to[1]];
        }
        // Opponent piece was promoted (bad for us)
        else {
            sum += weights[move.piece]!! + pstYou[move.color]!![move.piece]!![from[0]][from[1]];
            sum -= weights[move.promotion]!! + pstYou[move.color]!![move.promotion]!![to[0]][to[1]];
        }
    } else {
        // The moved piece still exists on the updated board, so we only need to update the position value
        if (move.color != color) {
            sum += pstYou[move.color]!![move.piece]!![from[0]][from[1]];
            sum -= pstYou[move.color]!![move.piece]!![to[0]][to[1]];
        } else {
            sum -= pstYou[move.color]!![move.piece]!![from[0]][from[1]];
            sum += pstYou[move.color]!![move.piece]!![to[0]][to[1]];
        }
    }
    return sum;
}

fun checkStatus(color: String): Boolean {
    if (engine.inCheckmate()) {
        println("Checkmate! Oops, $color lost.")
        EndMessage.type = EndType.NORMAL
        EndMessage.color = if(color == "WHITE") "BLACK" else "WHITE"
    } else if (engine.insufficientMaterial()) {
        println("It's a draw! (Insufficient Material)")
        EndMessage.type = EndType.DRAW
    } else if (engine.inThreefoldRepetition()) {
        println("It's a draw! (Threefold Repetition)")
        EndMessage.type = EndType.DRAW
    } else if (engine.inStalemate()) {
        println("It's a draw! (Stalemate)")
        EndMessage.type = EndType.DRAW
    } else if (engine.inDraw()) {
        println("It's a draw! (50-move Rule)")
        EndMessage.type = EndType.DRAW
    } else if (engine.inCheck()) {
        println("Oops, $color is in check!")
        return false
    } else {
        println("No check, checkmate, or draw.");
        return false;
    }
    engine.endGame.set(true)
    return true;
}