package objects

import core.ChessEngine
import kotlinx.coroutines.*

class ChessTimer(private val engine: ChessEngine) {
    private var timer: Job? = null
    private var last = 0L
    private var now = 0L

    private inline fun start(cycleCount: Int = 600, crossinline action: () -> Unit) = GlobalScope.launch {
//        delay(100)
        var temp = 0
        while (temp++ < cycleCount) {
            withContext(Dispatchers.Main) {
                action()
            }
            delay(1000L)
        }
    }

    internal fun startTimer() {
        engine.showComputationTime()
        last = System.currentTimeMillis()
        timer = start {
            engine.updateComputationTime()
        }
    }

    internal fun stopTimer() {
        now = System.currentTimeMillis()
        engine.updateFinalTime(now - last)
        this.timer!!.cancel()
    }


}