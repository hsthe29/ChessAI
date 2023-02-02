package objects

import Move
import algorithm.MTDSearch
import algorithm.TreeNode
import algorithm.findRandomMove
import algorithm.performBestMove
import core.ChessEngine
import core.engine
import kotlinx.coroutines.*

enum class PlayerOrder {
    P1, P2
}

class Player() {
    private val searcher = MTDSearch()

    suspend fun searchBestMove(): Int {
        delay(150)
        engine.timer.startTimer()
        withContext(COMPUTING.coroutineContext) {
            // computation move here
            performBestMove(engine.turn)
        }
        UI.launch {
            // when computation finished, update UI with `click`
            chessBoard.update()
        }
        engine.timer.stopTimer()
        return 0
    }
}