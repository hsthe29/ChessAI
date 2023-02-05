package algorithm

import Move
import core.*
import kotlinx.coroutines.launch
import objects.chessBoard
import kotlin.math.max
import kotlin.math.min

import kotlin.random.Random
import kotlin.reflect.jvm.internal.impl.utils.CollectionsKt


/** variables segment */
var bestMove: Move? = null
var blk = true

fun findRandomMove(feasibleMoves: List<Move>) = feasibleMoves[Random.nextInt(feasibleMoves.size)]

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

fun performBestMove(move: Move): Move {
    globalSum = evaluateBoard(engine, move.copy(), globalSum, 'b')
    println("Score: $globalSum")
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

    if (engine.turn == BLACK) {
        checkStatus("BLACK")
    } else {
        checkStatus("WHITE");
    }
    return move
}

/** Simplify the minimax algorithm */
//fun negamax(game: Engine, depth: Int, alpha: Int, beta: Int, isAITurn: Boolean): Int {
//    if(depth == 0) return (if(isAITurn) 1 else -1)*evaluateScore(game.board)
//    var observingScore = -CHECK_POINT
//    val feasibleMoves = game.getAllFeasibleMoves(isAITurn)
//    var alpha = alpha
//    feasibleMoves.shuffle()
//    for(move in feasibleMoves) {
//        game.move(move)
//        val score = -negamax(game, depth - 1, -beta, -alpha, !isAITurn)
//        if (score > observingScore) {
//            observingScore = score
//            if (depth == MAX_DEPTH)
//                bestMove = move
//            alpha = max(alpha, observingScore)
//        }
//        game.undo()
//        if(beta <= alpha) break
//    }
//    return observingScore
//}


