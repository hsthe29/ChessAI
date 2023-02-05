package algorithm

import Move
import core.*
import kotlinx.coroutines.launch
import objects.chessBoard


/** variables segment */

/*
 * Performs the minimax algorithm to choose the best move: https://en.wikipedia.org/wiki/Minimax (pseudocode provided)
 * Recursively explores all possible moves up to a given depth, and evaluates the game board at the leaves.
 *
 * Basic idea: maximize the minimum value of the position resulting from the opponent's possible following moves.
 * Optimization: alpha-beta pruning: https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning (pseudocode provided)
 *
 * Inputs:
 *  - game:                 the game object.
 *  - depth:                the depth of the recursive tree of all possible moves (i.e. height limit).
 *  - isMaximizingPlayer:   true if the current layer is maximizing, false otherwise.
 *  - sum:                  the sum (evaluation) so far at the current layer.
 *  - color:                the color of the current player.
 *
 * Output:
 *  the best move at the root of the current subtree.
 */

/*fun findBestMove(engine: ChessEngine, color: Char, currSum: Int): Pair<Move?, Int> {
    positionCount = 0

    val depth = wbDepth[color]
    val (bestMove, bestMoveValue) = minimax3(engine, depth, Int.MIN_VALUE, Int.MAX_VALUE, true, currSum, color)

    println(positionCount)

    return Pair(bestMove, bestMoveValue)
}

// * Makes the best legal move for the given color.
fun performBestMove(color: Char): Move {
    val (move, value) = if (color == BLACK) {
        findBestMove(engine, color, globalSum)
    } else {
        findBestMove(engine, color, -globalSum)
    }
    globalSum = evaluateBoard(engine, move!!, globalSum, 'b')

    if(engine.board[move.to] != null) {
        val rev = engine.board[move.to]!!.copy()
        UI.launch {
            chessBoard.carts.updateCart(rev)
        }
    }
    engine.trace = Pair(
        Pair(8 - sqLoc(move.from)[1].digitToInt(), sqLoc(move.from).codePointAt(0) - 'a'.code),
        Pair(8 - sqLoc(move.to)[1].digitToInt(), sqLoc(move.to).codePointAt(0) - 'a'.code))
    engine.makeMove(move)
    engine.thinking.set("Thinking: ${if(engine.turn == BLACK) "BLACK" else "WHITE"}")
    return move
}*/

fun performBestMove(move: Move): Pair<Move, PieceInfo?> {
    boardScore = evalBoardScore(engine, move.copy(), boardScore, 'b')
    val piece = engine.board[move.to]
    engine.makeMove(move)
    return Pair(move, piece)
}

/** Simplify the minimax algorithm */



