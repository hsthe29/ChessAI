package algorithm

val pieceScore = mapOf('K' to 10000, 'Q' to 150, 'R' to 50, 'B' to 40, 'N' to 30, 'P' to 10,
    'k' to -10000, 'q' to -150, 'r' to -50, 'b' to -40, 'n' to -30, 'p' to -10, '-' to 0)

val MOBILIT_YSCORE = 1

/** Constants | Immutable objects segments */
const val MAX_DEPTH = 4
const val CHECK_POINT = 15000
const val ALPHA = -10000
const val BETA = 10000

const val TABLE_SIZE = 1e7

// Constants for tuning search
const val QS_LIMIT = 219
const val EVAL_ROUGHNESS = 10 //13

fun evaluateScore(board: Array<CharArray>): Int {
    var score = 0
    for(i in 0 until 8) {
        for(j in 0 until 8) {
            score += pieceScore[board[i][j]]!!
        }
    }
    return score
}

fun materialScore() {

}

fun mobilityScore() {

}