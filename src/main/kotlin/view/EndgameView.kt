package view

import core.*
import javafx.geometry.Pos
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import tornadofx.View
import tornadofx.text
import tornadofx.vbox

class EndgameView: View() {
    override val root = vbox {
        println("generate Time: $genTime")
        println("length: ${dataHistory.size} - ${engine.history.size}")
        setPrefSize(550.0, 150.0)
        val color = EndMessage.color
        val title = if(EndMessage.type == EndType.NORMAL) "$color Win!" else "Draw! " + EndMessage.kind
        engine.thinking.set(title)
        text(title) {
            font = Font.font("Helvetica", FontWeight.BOLD, 40.0)
            alignment = Pos.CENTER
        }
    }
}