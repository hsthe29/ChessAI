package algorithm

import Move
import core.BLACK
import core.ChessEngine
import core.engine
import core.sqLoc
import kotlinx.coroutines.launch
import objects.chessBoard
import kotlin.math.max
import kotlin.math.min

import kotlin.random.Random
import kotlin.reflect.jvm.internal.impl.utils.CollectionsKt


/** variables segment */
var bestMove: Move? = null

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
fun minimax(engine: ChessEngine, depth: Int, alpha: Int, beta: Int, isMaximizingPlayer: Boolean, sum: Int, color: Char): Pair<Move?, Int> {
    var alpha = alpha
    var beta = beta
    positionCount++
    val children = engine.generateMoves()
    children.shuffle()

    var currMove: Move;
    // Maximum depth exceeded or node is a terminal node (no children)
    if (depth == 0 || children.isEmpty()) {
        return Pair(null, sum)
    }

    // Find maximum/minimum from list of 'children' (possible moves)
    var maxValue = Int.MIN_VALUE
    var minValue = Int.MAX_VALUE
    var bestMove: Move? = null
    for (child in children) {
        currMove = child

        // Note: in our case, the 'children' are simply modified game states
        engine.makeMove(currMove);
        val newSum = evaluateBoard(engine, currMove, sum, color);
        val (childBestMove, childValue) = minimax(engine, depth - 1, alpha, beta, !isMaximizingPlayer, newSum, color)

        engine.undoMove();

        if (isMaximizingPlayer) {
            if (childValue > maxValue) {
                maxValue = childValue;
                bestMove = currMove;
            }
            if (childValue > alpha) {
                alpha = childValue;
            }
        } else {
            if (childValue < minValue) {
                minValue = childValue;
                bestMove = currMove;
            }
            if (childValue < beta) {
                beta = childValue;
            }
        }
        // Alpha-beta pruning
        if (alpha >= beta) {
            break;
        }
    }

    return if (isMaximizingPlayer) Pair(bestMove, maxValue) else Pair(bestMove, minValue)
}

fun findBestMove(engine: ChessEngine, color: Char, currSum: Int): Pair<Move?, Int> {
    positionCount = 0

    val depth = if (color == 'b') 2 else 2
    val (bestMove, bestMoveValue) = minimax(engine, depth, Int.MIN_VALUE, Int.MAX_VALUE, true, currSum, color)

    println(positionCount)

    return Pair(bestMove, bestMoveValue)
}

// * Makes the best legal move for the given color.
fun performBestMove(color: Char) {
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

    if (color == BLACK) {
        checkStatus("BLACK")
    } else {
        checkStatus("WHITE");
    }
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


