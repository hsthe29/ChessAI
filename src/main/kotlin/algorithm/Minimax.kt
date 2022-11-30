package algorithm

import engine.Engine
import objects.Move
import pieceScore
import kotlin.random.Random

// Constants | Immutable objects segments
const val MAX_DEPTH = 4
const val CHECK_POINT = 1000

// variable segment
var bestMove: Move? = null

// function segment
fun evaluateScore(board: Array<CharArray>): Int {
    var score = 0
    for(i in 0 until 8) {
        for(j in 0 until 8) {
            score += pieceScore[board[i][j]]!!
        }
    }
    return score
}

fun findRandomMove(feasibleMoves: Array<Move>) = feasibleMoves[Random.nextInt(feasibleMoves.size)]

fun findBestMoveMinimax(game: Engine): Move? {
    bestMove = null
    minimax(game, MAX_DEPTH, true)
    return bestMove
}

fun minimax(game: Engine, depth: Int, isAITurn: Boolean): Int {
    if(depth == 0) return evaluateScore(game.board)
    var observingScore = if(isAITurn) -CHECK_POINT else CHECK_POINT
    val feasibleMoves = game.getAllFeasibleMoves(isAITurn)
    feasibleMoves.shuffle()
    for(move in feasibleMoves) {
        game.move(move)
        val score = minimax(game, depth - 1, !isAITurn)
        if(isAITurn) {
            if (score > observingScore) {
                observingScore = score
                if (depth == MAX_DEPTH)
                    bestMove = move
            }
        } else {
            if (score < observingScore) {
                observingScore = score
                if (depth == MAX_DEPTH)
                    bestMove = move
            }
        }
//        println(". Move: $move")
        game.undo()
    }
    return observingScore
}


