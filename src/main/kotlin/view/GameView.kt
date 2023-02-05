package view

import Styles
import UI
import core.*
import javafx.application.Platform
import javafx.scene.control.cell.PropertyValueFactory
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
        setPrefSize(1280.0, 820.0)
        addClass(Styles.frame)
        vbox {
            hbox {
                add(chessBoard)
                line { startX = 0.0; startY = 0.0
                    endX = 0.0; endY = 690.0
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
                        font = Font.font("Helvetica", FontWeight.BOLD, 25.0)
                    }
                    timeLabel = text(engine.time).apply {
                        paddingLeft = 20
                        font = Font.font("Helvetica", FontWeight.BOLD, 30.0)
                    }

                    tableview<MoveData> {
                        maxWidth = 520.0
                        minWidth = 520.0
                        column("Piece", Char::class) {
                            cellValueFactory = PropertyValueFactory("piece")
                            style = "-fx-font-weight:bold;-fx-font-size:16px;-fx-alignment: CENTER"
                        }
                        column("Move", String::class) {
                            minWidth = 65.0
                            cellValueFactory = PropertyValueFactory("move")
                            style = "-fx-font-weight:bold;-fx-font-size:16px;-fx-alignment: CENTER"
                        }
                        column("Captured", Char::class) {
                            cellValueFactory = PropertyValueFactory("captured")
                            style = "-fx-font-weight:bold;-fx-font-size:16px;-fx-alignment: CENTER"
                        }
                        column("Color", Char::class) {
                            cellValueFactory = PropertyValueFactory("color")
                            style = "-fx-font-weight:bold;-fx-font-size:16px;-fx-alignment: CENTER"
                        }
                        column("Time (s)", Double::class) {
                            cellValueFactory = PropertyValueFactory("eval")
                            style = "-fx-font-weight:bold;-fx-font-size:16px;-fx-alignment: CENTER"
                        }
                        column("Visited nodes", Int::class) {
                            minWidth = 120.0
                            maxWidth = 120.0
                            cellValueFactory = PropertyValueFactory("nodeVisited")
                            style = "-fx-font-weight:bold;-fx-font-size:16px;-fx-alignment: CENTER"
                        }
                        onLeftClick {
                            if(engine.endGame.value) {
                                val data = this.selectedItem
                                if (data != null) {
                                    chessBoard.loadFromFEN(data.pFen, data.from, data.to)
                                }
                            }
                        }
                        items = dataHistory
                    }
                    tableview<Statistic> {
                        column("", Char::class) {
                            cellValueFactory = PropertyValueFactory("color")
                            style = "-fx-font-weight:bold;-fx-font-size:16px;-fx-alignment: CENTER"
                        }
                        column("Score", String::class) {
                            minWidth = 70.0
                            maxWidth = 70.0
                            cellValueFactory = PropertyValueFactory("score")
                            style = "-fx-font-weight:bold;-fx-font-size:16px;-fx-alignment: CENTER"
                        }
                        column("Moves", String::class) {
                            minWidth = 70.0
                            maxWidth = 70.0
                            cellValueFactory = PropertyValueFactory("moves")
                            style = "-fx-font-weight:bold;-fx-font-size:16px;-fx-alignment: CENTER"
                        }
                        column("Total Time (s)", Char::class) {
                            minWidth = 120.0
                            maxWidth = 120.0
                            cellValueFactory = PropertyValueFactory("totalTime")
                            style = "-fx-font-weight:bold;-fx-font-size:16px;-fx-alignment: CENTER"
                        }
                        column("Avg Time (s)", Char::class) {
                            minWidth = 120.0
                            maxWidth = 120.0
                            cellValueFactory = PropertyValueFactory("avgTime")
                            style = "-fx-font-weight:bold;-fx-font-size:16px;-fx-alignment: CENTER"
                        }
                        fixedCellSize = 30.0
                        minHeight = 95.0
                        maxHeight = 95.0
                        items = stats
                    }
                    label("")
                }
            }
            line {
                startX = 0.0; startY = 0.0
                endX = 1280.0; endY = 0.0
                addClass(Styles.line)
            }
            hbox {
                label("") { paddingTop = 110 }
                label("WHITE: ") {
                    style = "-fx-font-weight:bold;-fx-font-size:20px"
                    paddingLeft = 130
                    translateY = 50.0
                }
                add(chessBoard.carts.yourItems.apply {
                    paddingLeft = 10
                    paddingRight = 30
                    paddingTop = 10
                })

                label("BLACK: ") {
                    style = "-fx-font-weight:bold;-fx-font-size:20px"
                    paddingLeft = 80
                    translateY = 50.0
                }
                add(chessBoard.carts.opponentItems.apply{
                    paddingLeft = 10
                    paddingTop = 10
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

    private fun onEndGame() {
        find<EndgameView>().openWindow(modality = Modality.APPLICATION_MODAL, block = true, resizable = false)
    }
}



