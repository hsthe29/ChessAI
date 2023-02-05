package view

import Styles
import core.MoveData
import core.dataHistory
import core.engine
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Modality
import kotlinx.coroutines.launch
import objects.chessBoard
import tornadofx.*

class GameView : View("ChessAI") {
    private lateinit var timeLabel: Text
    private lateinit var turnLabel: Text

    override val root = stackpane {
        setPrefSize(1200.0, 820.0)
        vbox {
            hbox {
                add(chessBoard)
                line { startX = 0.0; startY = 0.0
                    endX = 0.0; endY = 660.0
                    addClass(Styles.line)
                }
                vbox(spacing = 20) {
                    hbox(spacing = 30) {
                        paddingLeft = 120
                        paddingTop = 10
                        button("New Game") {
                            onLeftClick {
                                startGame()
                            }
                        }
                        button("Quit") {
                            onLeftClick { this@GameView.close() }
                        }
                    }
                    turnLabel = text(engine.thinking).apply {
                        paddingLeft = 20
//                        this.translateY = 20.0
                        font = Font.font("Helvetica", FontWeight.BOLD, 25.0)
                    }
                    timeLabel = text(engine.time).apply {
                        paddingLeft = 20
//                        this.translateY = 30.0
                        font = Font.font("Helvetica", FontWeight.BOLD, 30.0)
                    }

                    tableview<MoveData> {
                        column("Piece", Char::class) {
                            setCellValueFactory(PropertyValueFactory("piece"))
                        }
                        column("Move", String::class) {
                            minWidth = 65.0
                            setCellValueFactory(PropertyValueFactory("move"))
                        }
                        column("Captured", Char::class) {
                            setCellValueFactory(PropertyValueFactory("captured"))
                        }
                        column("Color", Char::class) {
                            setCellValueFactory(PropertyValueFactory("color"))
                        }
                        column("Time (s)", Double::class) {
                            setCellValueFactory(PropertyValueFactory("eval"))
                        }
                        column("Visited nodes", Int::class) {
                            setCellValueFactory(PropertyValueFactory("nodeVisited"))
                        }
                        items = dataHistory
                    }
                }
            }
            line {
                startX = 0.0; startY = 0.0
                endX = 1200.0; endY = 0.0
                addClass(Styles.line)
            }
            hbox {
                label("WHITE: ") {
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
                label("BLACK: ") {
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
    }

    init {
        currentWindow?.setOnCloseRequest { Platform.exit() }
        engine.endGame.onChange {
            UI.launch {
                onEndGame()
            }
        }
    }
    private fun startGame() {
        find<Config>().openWindow(modality = Modality.APPLICATION_MODAL, block = true, resizable = false)
        if(engine.turn != '-') {
            chessBoard.update()
            engine.search()
        }
    }

    fun onEndGame() {
        find<EngameView>().openWindow(modality = Modality.APPLICATION_MODAL, block = true, resizable = false)
    }
}



