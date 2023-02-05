package objects

import Move
import algorithm.MTDSearch
import algorithm.performBestMove
import core.engine
import core.pushMove
import core.wbDepth
import kotlinx.coroutines.*

class Player() {
    private val searcher = MTDSearch()

    suspend fun searchBestMove() {
        delay(150)
        engine.timer.startTimer()
        var move: Move? = null
        withContext(COMPUTING.coroutineContext) {
            // computation move here
            move = performBestMove(searcher.search(wbDepth[engine.turn])!!)
        }
        val time = engine.timer.stopTimer()
        UI.launch {
            // when computation finished, update UI with `click`
            chessBoard.update()
            pushMove(move!!, time/1000.0, searcher.nodesVisited)
        }
    }
}