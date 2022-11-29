package objects

import javafx.concurrent.Task
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.*
import view.GameView
import java.awt.MouseInfo
import java.util.concurrent.Executors

enum class PlayerOrder {
    P1, P2
}

class Player(val view: GameView) {

    /** perform click programmatically */
    private fun click(row: Int, col: Int) {
        view.chesssBoard.cells[row][col].fireEvent(
            MouseEvent(
                MouseEvent.MOUSE_CLICKED,
                0.0, 0.0,
                MouseInfo.getPointerInfo().location.getX(), MouseInfo.getPointerInfo().location.getY(),
                MouseButton.PRIMARY,
                1,  // Click count
                false, false, false, false,
                true, false, false,
                false, false, true,
                null
            )
        )
    }

    fun move() {
        GlobalScope.launch {
            view.timer.startTimer()
            view.glass.show()
            GlobalScope.async {
                // computation move here

            }.await()
            withContext(Dispatchers.Main) {
                // when computation finished, update UI with `click`
                // example: click(2, 0)

            }
            view.glass.hide()
            view.timer.stopTimer()
        }
    }
}