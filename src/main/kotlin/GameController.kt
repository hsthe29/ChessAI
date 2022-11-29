import javafx.animation.AnimationTimer
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import objects.PlayerOrder
import tornadofx.*
import view.GameView
import kotlin.time.Duration

class GameController: Controller() {
    var mode = GameMode.AI_MODE
    val isWhite = SimpleBooleanProperty(true)
    val ediblePieces = Edible(false, "", 0)
    var isAIMove = false
    var running = false
    var currentColor = ""
    val isMessaged = SimpleBooleanProperty(true)
    var playerIsWin = PlayerOrder.P2
    val turn = SimpleBooleanProperty(true)

}

