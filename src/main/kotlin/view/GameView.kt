package view

import Styles
import algorithm.checkStatus
import core.AI
import core.BLACK
import core.GameMode
import core.engine
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Modality
import objects.*
import tornadofx.*

fun notification(parent: StackPane, view: GameView): VBox {
     return VBox().apply {
         spacing = 10.0
         setMaxSize(250.0, 100.0)
         alignment = Pos.CENTER
         this.hide()
         this.addClass(Styles.notify_table)
         label("Them nhan")
         hbox(spacing = 30) {
             paddingLeft = 40
             button("New Game") {
                 onLeftClick {
                     find<Config>().openWindow(owner = null, resizable = false)
                     view.close()
                 }
             }
             button("Cancel") {
                 onLeftClick {
                     this@apply.hide()
                 }
             }
         }
     }.also { parent.add(it) }
}

class GameView : View("ChessAI") {
    private lateinit var winStatus: VBox
    private lateinit var timeLabel: Text

    override val root = stackpane {
        setPrefSize(1200.0, 820.0)
        vbox {
            hbox {
                add(chessBoard)
                line { startX = 0.0; startY = 0.0
                    endX = 0.0; endY = 660.0
                    addClass(Styles.line)
                }
                vbox {
                    hbox(spacing = 30) {
                        paddingLeft = 120
                        paddingTop = 10
                        button("Start") {
                            onLeftClick {
                                if(text == "Start") {
                                    text = "Restart"
                                    startGame()
                                }
                            }
                        }
                        button("New Game") {
                            onLeftClick {
                                find<Config>("core" to engine).openWindow(modality = Modality.APPLICATION_MODAL, block = true, resizable = false)
                            }
                        }
                        button("Quit") {
                            onLeftClick { this@GameView.close() }
                        }
                    }
                    timeLabel = text(engine.time).apply {
                        paddingLeft = 20
                        this.translateY = 20.0
                        font = Font.font("Helvetica", FontWeight.BOLD, 30.0);
                    }
                    button("undo") {
                        onLeftClick {
                            //game.undoWithUI()
                        }
                    }
                }
            }
            line {
                startX = 0.0; startY = 0.0
                endX = 1200.0; endY = 0.0
                addClass(Styles.line)
            }
            hbox {
                label("You: ") {
                    paddingLeft = 130
                    translateY = 50.0
                }
                add(chessBoard.carts.yourItems.apply {
                    paddingLeft = 40
                    paddingRight = 30
                    paddingTop = 20
                })
                line {
                    startX = 0.0; startY = 0.0
                    endX = 0.0; endY = 150.0
                    addClass(Styles.line)
                }
                label("Opponent: ") {
                    minWidth = 50.0
                    paddingLeft = 60
                    translateY = 50.0
                }
                add(chessBoard.carts.opponentItems.apply{
                    paddingLeft = 40
                    paddingRight = 30
                    paddingTop = 20
                })
            }
        }
        winStatus = notification(this, this@GameView)
    }

    init {
        currentWindow?.setOnCloseRequest {Platform.exit()}
    }
    private fun startGame() {
        find<Config>().openWindow(modality = Modality.APPLICATION_MODAL, block = true, resizable = false)
        chessBoard.update()
        engine.search()
    }

//    fun showNotification(s: String) {
//        property.message.value = s
//        this.winStatus.show()
//    }
}



