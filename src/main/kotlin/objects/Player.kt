package objects

import algorithm.findBestMoveMinimax
import algorithm.findRandomMove
import kotlinx.coroutines.*
import tornadofx.*
import view.GameView

enum class PlayerOrder {
    P1, P2
}

class Player(val view: GameView) {
    fun move() {
        GlobalScope.launch {
            view.glass.show()
            delay(100)
            view.timer.startTimer()
            var move: Move? = null
            withContext(Dispatchers.Default) {
                // computation move here
                move =  findBestMoveMinimax(view.game)
                if(move == null)
                    move = findRandomMove(view.game.getAllFeasibleMoves(true))
            }
            withContext(Dispatchers.Main) {
                // when computation finished, update UI with `click`
//                 example: click(2, 0)
                view.game.moveWithUI(move!!)
            }
            view.glass.hide()
            view.timer.stopTimer()
        }
    }
}