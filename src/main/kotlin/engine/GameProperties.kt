package engine

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty

object GameProperties {
    var mode = GameMode.AI_MODE
    var youIsWhite = SimpleBooleanProperty(true)
    val pieceAte = PieceInfo(false, "", 1)
    val message = SimpleStringProperty("")
    var aiTurn = true
    val ate = SimpleIntegerProperty(-1000000)
}

