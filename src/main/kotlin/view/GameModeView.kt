package view

import Styles
import core.BLACK
import core.GameMode
import core.engine
import core.wbDepth
import javafx.geometry.Pos
import javafx.scene.control.ToggleGroup
import tornadofx.*


class GameModeView : View() {
    private lateinit var mode: ToggleGroup
    private lateinit var youFirst: ToggleGroup
    var wDepth = 3
    var bDepth = 4

    override val root = vbox(alignment = Pos.CENTER, spacing = 10) {
            setPrefSize(350.0, 200.0)
            vbox {
                paddingLeft = 30
                hbox(spacing = 10) {
                    label("Select mode: ") { paddingTop = 5 }
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

                vbox {
                    label("Select depth: ") {
                        paddingTop = 5
                    }
                    val wlb = label("White: 3")
                    slider(2, 8, 3) {
                        blockIncrement = 1.0
                    }.valueProperty().onChange {
                        wbDepth.w = it.toInt()
                        wlb.text = "White: ${it.toInt()}"
                    }
                    val blb = label("Black: 4")
                    slider(2, 8, 4) {
                        blockIncrement = 1.0
                    }.valueProperty().onChange {
                        wbDepth.b = it.toInt()
                        blb.text = "Black: ${it.toInt()}"
                    }
                }
                hbox(spacing = 10) {
                    label("Which move first: ") {
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
            this.setPrefSize(50.0, 30.0)
            action {
                engine.mode = mode.selectedToggle.userData as GameMode
                engine.turn = youFirst.selectedToggle.userData as Char
                engine.thinking.set("Thinking: ${if(engine.turn == BLACK) "BLACK" else "WHITE"}")
                this@GameModeView.close()
            }
        }
    }

    override fun onDock() {
        currentWindow?.setOnCloseRequest {
            println("Closing")
        }
    }
}
