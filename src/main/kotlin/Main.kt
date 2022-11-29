import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.*
import view.ChessUI
import view.GameView

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

fun main() {
    launch<Main>()
}