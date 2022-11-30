package view

import GameMode
import engine.GameProperties
import javafx.geometry.Pos
import javafx.scene.control.ToggleGroup
import tornadofx.*

class Config : View() {
    val property = GameProperties
    private lateinit var mode: ToggleGroup
    private lateinit var color: ToggleGroup
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
                        radiobutton("AI mode") {
                            paddingTop = 5
                            isSelected = true
                            userData = GameMode.AI_MODE
                        }
                        radiobutton("PvP") {
                            paddingTop = 5
                            userData = GameMode.PVP
                        }
                        radiobutton("AI vs AI") {
                            paddingTop = 5
                            paddingRight = 50
                            userData = GameMode.AI_FULL
                        }
                    }
                }
                hbox(spacing = 10) {
                    label("Select color: ") {
                        paddingTop = 5
                    }
                    color = togglegroup {
                        radiobutton("White") {
                            paddingTop = 5
                            isSelected = true
                            userData = true
                        }
                        radiobutton("Black") {
                            paddingTop = 5
                            userData = false
                        }
                    }
                }.hide()
                hbox(spacing = 10) {
                    label("You move first: ") {
                        paddingTop = 5
                    }
                    youFirst = togglegroup {
                        radiobutton("Yes") {
                            paddingTop = 5
                            isSelected = true
                            userData = true
                        }
                        radiobutton("No") {
                            paddingTop = 5
                            userData = false
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
                property.mode = mode.selectedToggle.userData as GameMode
                property.youIsWhite.set(color.selectedToggle.userData as Boolean)
                property.isAIMove = !(youFirst.selectedToggle.userData as Boolean)
                this@Config.close()
            }
        }
    }

    init {
    }
}
