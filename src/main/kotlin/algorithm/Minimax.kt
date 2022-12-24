package algorithm

import Move
import engine.Engine
import kotlin.math.max
import kotlin.math.min

import kotlin.random.Random
import kotlin.reflect.jvm.internal.impl.utils.CollectionsKt


/** variables segment */
var bestMove: Move? = null

fun findRandomMove(feasibleMoves: List<Move>) = feasibleMoves[Random.nextInt(feasibleMoves.size)]
//
//fun findBestMoveMinimax(game: Engine, negamax: Boolean = false): Move? {
//    bestMove = null
//    if(negamax) negamax(game, MAX_DEPTH, ALPHA, BETA, true)
//    else minimax(game, MAX_DEPTH, ALPHA, BETA,true)
//    return bestMove
//}
//
//fun minimax(game: Engine, depth: Int, alpha: Int, beta: Int, isAITurn: Boolean): Int {
//    if(depth == 0) return evaluateScore(game.board)
//    var observingScore = if(isAITurn) -CHECK_POINT else CHECK_POINT
//    val feasibleMoves = game.getAllFeasibleMoves(isAITurn)
//    var alpha = alpha
//    var beta = beta
//    feasibleMoves.shuffle()
//    for(move in feasibleMoves) {
//        game.move(move)
//        val score = minimax(game, depth - 1, alpha, beta, !isAITurn)
//        if(isAITurn) {
//            if (score > observingScore) {
//                observingScore = score
//                if (depth == MAX_DEPTH)
//                    bestMove = move
//            }
//            alpha = max(alpha, observingScore)
//        } else {
//            if (score < observingScore) {
//                observingScore = score
//                if (depth == MAX_DEPTH)
//                    bestMove = move
//            }
//            beta = min(beta, observingScore)
//        }
//        game.undo()
//        if(beta <= alpha) break
//    }
//    return observingScore
//}

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


