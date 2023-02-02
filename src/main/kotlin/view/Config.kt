package view

import core.ChessEngine
import core.GameMode
import core.engine
import javafx.geometry.Pos
import javafx.scene.control.ToggleGroup
import tornadofx.*

class Config() : View() {
    private lateinit var mode: ToggleGroup
    private lateinit var youFirst: ToggleGroup

    override val root = vbox(alignment = Pos.CENTER, spacing = 10) {
            setPrefSize(350.0, 160.0)
            vbox {
                paddingLeft = 30
                hbox(spacing = 10) {
                    label("Select mode: ") {
                        paddingTop = 5
                    }
                    mode = togglegroup {
                        radiobutton("vs AI") {
                            paddingTop = 5
                            isSelected = true
                            userData = GameMode.PvsCOM
                        }
                        radiobutton("AI vs AI") {
                            paddingTop = 5
                            paddingRight = 50
                            userData = GameMode.COMvsCOM
                        }
                    }
                }
                hbox(spacing = 10) {
                    label("First: ") {
                        paddingTop = 5
                    }
                    youFirst = togglegroup {
                        radiobutton("WHITE") {
                            paddingTop = 5
                            isSelected = true
                            userData = 'w'
                        }
                        radiobutton("BLACK") {
                            paddingTop = 5
                            userData = 'b'
                        }
                    }
                }
            }
        line {
            startX = 0.0
            startY = 0.0
            endX = 300.0
            endY = 0.0
            addClass(Styles.line)
        }
        button("Start") {
            translateY = 8.0
            this.setPrefSize(50.0, 30.0)
            action {
                engine.mode = mode.selectedToggle.userData as GameMode
                engine.turn = youFirst.selectedToggle.userData as Char
                this@Config.close()
            }
        }
    }
}
