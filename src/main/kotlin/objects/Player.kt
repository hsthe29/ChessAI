package objects

import algorithm.MTDSearch
import algorithm.checkStatus
import algorithm.performBestMove
import core.*
import kotlinx.coroutines.*

class Player {
    private val searcher = MTDSearch()

    suspend fun searchBestMove() {
        delay(500)
        engine.timer.startTimer()
        val (move, piece) = withContext(COMPUTING.coroutineContext) {
            // computation move here
            performBestMove(searcher.search(wbDepth[engine.turn])!!)
        }
        val time = engine.timer.stopTimer()

        engine.trace = Pair(
            Pair(8 - sqLoc(move.from)[1].digitToInt(), sqLoc(move.from).codePointAt(0) - 'a'.code),
            Pair(8 - sqLoc(move.to)[1].digitToInt(), sqLoc(move.to).codePointAt(0) - 'a'.code))
        val fen = engine.generateFEN()
            .split(' ')[0]
        engine.thinking.set("Thinking: ${if(engine.turn == BLACK) "BLACK" else "WHITE"}")
        if (engine.turn == BLACK) {
            checkStatus("BLACK")
        } else {
            checkStatus("WHITE")
        }
        UI.launch {
            chessBoard.update()
            if(piece != null) chessBoard.carts.updateCart(piece)
            pushMove(move, time/1000.0, searcher.nodesVisited, fen)
        }
    }
}