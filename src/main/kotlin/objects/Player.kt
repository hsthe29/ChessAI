package objects

import Move
import algorithm.MTDSearch
import algorithm.TreeNode
import algorithm.findRandomMove
import engine.GameProperties
import kotlinx.coroutines.*
import tornadofx.*
import view.GameView

enum class PlayerOrder {
    P1, P2
}

class Player(val view: GameView) {
    private val searcher = MTDSearch()
    fun move(node: TreeNode, depth: Int = 6) {
        println("AI: ${GameProperties.aiTurn}")
        GlobalScope.launch {
            view.glass.show()
            delay(100)
            view.timer.startTimer()
            var move: Move? = null
            withContext(Dispatchers.Default) {
                // computation move here
                val (_, _move, score) = searcher.search(node, depth)
                if(_move == null) {
                    move = findRandomMove(node.getAllMoves())
                } else move = _move
            }
            withContext(Dispatchers.Main) {
                // when computation finished, update UI with `click`
                view.game.moveWithUI(move!!, true)
            }
            view.glass.hide()
            view.timer.stopTimer()
        }
    }
}