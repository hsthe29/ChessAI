package view

import core.EndMessage
import core.EndType
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import tornadofx.View
import tornadofx.text
import tornadofx.vbox
import javax.swing.GroupLayout.Alignment

class EngameView: View() {
    override val root = vbox {
        setPrefSize(350.0, 120.0)
        val color = EndMessage.color
        val title = if(EndMessage.type == EndType.NORMAL) "$color Win!" else "Draw"
        text(title) {
            font = Font.font("Helvetica", FontWeight.BOLD, 40.0)
            alignment = Pos.CENTER
        }
    }
}