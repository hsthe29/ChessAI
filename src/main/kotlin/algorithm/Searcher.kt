package algorithm

import core.BLACK
import core.ChessEngine
import core.engine
import objects.MTD
import objects.Move
import objects.SearchMode
import kotlin.math.max
import kotlin.math.min

data class Bound(var lower: Int, var upper: Int)

class Searcher {
    var nodesVisited = 0
    private var totalNode = 0
    private var lower = Int.MIN_VALUE
    private var upper = Int.MAX_VALUE
    fun search(depth: Int): Move? {
        nodesVisited = 0
        totalNode = 0
        val move = if(SearchMode[engine.turn] == MTD) {
            mtd(100, depth, engine.turn)
        } else findBestMoveMinimax(engine, depth)

        println("""| End MTD(f): 
                   |        Visited Nodes: $nodesVisited
                   |        Cutoff: ${totalNode - nodesVisited} branches""".trimMargin("|"))
        return move
    }

    private fun mtd(f: Int, depth: Int, color: Char): Move? {
        lower = Int.MIN_VALUE
        upper = Int.MAX_VALUE
        var iter = 50
        val currentSum = if (color == BLACK) boardScore else -boardScore
        var score = f + currentSum
        while(iter-- > 0 && (lower < upper)) {
            val beta = if(score == lower) score+1 else score
            score = memoryEnhancedAlphaBeta(engine, depth,beta-1, beta, true, color, currentSum, null, root = true)
            if(score < beta) upper = score else lower = score
            println("       MTD(f): lower: $lower, upper: $upper")
        }

        val fen = engine.generateFEN()
            .split(' ')
            .slice(0 until 4)
            .joinToString(" ") + true

        return TranspositionTable.getMove(fen)
    }

    private fun memoryEnhancedAlphaBeta(engine: ChessEngine, depth : Int, _alpha: Int, _beta: Int, isMaximizing: Boolean, color: Char, prevSum: Int, move: Move?, root: Boolean = false): Int {
        var alpha = _alpha
        var beta = _beta
        nodesVisited++

        val fen = engine.generateFEN()
            .split(' ')
            .slice(0 until 4)
            .joinToString(" ") + root

        var score = TranspositionTable.getScore(fen, depth)
        if(score != null) {
            if(score.lower >= beta) return score.lower
            if(score.upper <= alpha) return score.upper
            alpha = max(alpha, score.lower)
            beta = min(beta, score.upper)
        } else score = Bound(lower, upper)
        val moves = engine.allMoves()
        moves.shuffle()

        // ---------------------------
        val inCheckmate = engine.inCheck && engine.lenMoves == 0
        val inStalemate = !engine.inCheck && engine.lenMoves == 0
        val inDraw = engine.halfMoves >= 100 || inStalemate || engine.insufficientMaterial() || engine.inThreefoldRepetition()
        var sum = prevSum
        if (inCheckmate) {
            // Opponent is in checkmate (good for us)
            sum = if (move!!.color == color) 100000 + depth*100
            // Our king's in checkmate (bad for us)
            else -100000 - depth*100
        }
        else if (inDraw) {
            sum = if(move == null) 0
            else if(move.color == color) -depth*15 else depth*15
        }
        // ----------------------------
        val cut = inCheckmate || inDraw
        if(!cut) if(move != null) sum = evaluateBoard(engine, move.copy(), prevSum, color)
        var result = sum
        var bestMove: Move? = null
        if(!cut && depth > 0) {
            totalNode += moves.size
            if(isMaximizing) {
                result = Int.MIN_VALUE
                var a = alpha
                for(mv in moves) {
                    engine.makeMove(mv)
//                    val newSum = evaluateBoard(engine, move.copy(), sum, color)
                    result = memoryEnhancedAlphaBeta(engine, depth - 1, a, beta, false, color, sum, mv)
                    engine.undoMove()
                    a = max(a, result)
                    if(result >= beta) {
                        bestMove = mv
                        break
                    }
                }
            }
            else {
                result = Int.MAX_VALUE
                var b = beta
                for(mv in moves) {
                    engine.makeMove(mv)
//                    val newSum = evaluateBoard(engine, move.copy(), sum, color)
                    result = memoryEnhancedAlphaBeta(engine, depth - 1, alpha, b, true, color, sum, mv)
                    engine.undoMove()
                    b = min(b, result)
                    if(result <= alpha) {
                        bestMove = mv
                        break
                    }
                }
            }
        }

        if(bestMove != null) {
            TranspositionTable.setMove(fen, value = bestMove)
        }

        if(result <= alpha) score.upper = result
        if(result > alpha && result < beta)
            score = Bound(result, result)
        if(result >= beta) score.lower = result

        TranspositionTable.setScore(fen, depth, value = score)

        return result
    }

    private fun findBestMoveMinimax(engine: ChessEngine, depth: Int, alphaBeta: Boolean = true) =
        if (alphaBeta) minimaxWithAlphaBeta(engine, depth, ALPHA, BETA, isMaximizing = true).first
        else minimax(engine, depth, isMaximizing = true).first

    private fun minimaxWithAlphaBeta(engine: ChessEngine, depth: Int, alpha: Int, beta: Int, isMaximizing: Boolean): Pair<Move?, Int> {
        nodesVisited++
        val moves = engine.generateMoves()
        var alpha = alpha
        var beta = beta
        moves.shuffle()
        var bestMove: Move? = null
        var observingScore = if(isMaximizing) Int.MIN_VALUE else Int.MAX_VALUE

        if(depth == 0 || moves.isEmpty()) {
            observingScore = basicEval(engine)
        } else {
            totalNode += moves.size
            for (move in moves) {
                engine.makeMove(move)
                val (_, score) = minimaxWithAlphaBeta(engine, depth - 1, alpha, beta, !isMaximizing)
                if (isMaximizing) {
                    if (score > observingScore) {
                        observingScore = score
                        bestMove = move
                    }
                    alpha = max(alpha, observingScore)
                } else {
                    if (score < observingScore) {
                        observingScore = score
                        bestMove = move
                    }
                    beta = min(beta, observingScore)
                }
                engine.undoMove()
                if (beta <= alpha) break
            }
        }
        return Pair(bestMove, observingScore)
    }

    private fun minimax(game: ChessEngine, depth: Int, isMaximizing: Boolean): Pair<Move, Int> {
        nodesVisited++
        val moves = game.generateMoves()
        moves.shuffle()
        var bestMove: Move? = null
        var observingScore = if(isMaximizing) Int.MIN_VALUE else Int.MAX_VALUE

        if(depth == 0 || moves.isEmpty()) {
            observingScore = basicEval(engine)
        } else {
            totalNode += moves.size
            for (move in moves) {
                game.makeMove(move)
                val (_, score) = minimax(game, depth - 1, !isMaximizing)
                if (isMaximizing) {
                    if (score > observingScore) {
                        observingScore = score
                        bestMove = move
                    }
                } else {
                    if (score < observingScore) {
                        observingScore = score
                        bestMove = move
                    }
                }
                game.undoMove()
            }
        }
        return Pair(bestMove!!, observingScore)
    }
}
