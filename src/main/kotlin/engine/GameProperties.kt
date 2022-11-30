package engine

import GameMode
import PieceInfo
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import objects.PlayerOrder
import view.GameView

object GameProperties {
    var mode = GameMode.AI_MODE
    var youIsWhite = SimpleBooleanProperty(true)
    val pieceAte = PieceInfo(false, "", 1)
    var isAIMove = false
    var playerIsWin = PlayerOrder.P2
    var aiTurn = true
    val ate = SimpleIntegerProperty(-1000000)

}

