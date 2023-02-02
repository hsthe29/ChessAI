import javafx.scene.image.Image
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.*
import view.GameView
import kotlin.coroutines.CoroutineContext

class Main : App(GameView::class, Styles::class) {
    override fun start(stage: Stage) {
        with(stage) {
            isResizable = false
            centerOnScreen()
            super.start(this)
        }
        FX.primaryStage.icons += Image("icons/chess.png")
    }
}

object UI : CoroutineScope {
    override val coroutineContext = Dispatchers.JavaFx
}

object COMPUTING: CoroutineScope {
    override val coroutineContext = Dispatchers.Default
}

fun main() {
    launch<Main>()
}