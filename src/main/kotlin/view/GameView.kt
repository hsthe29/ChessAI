package view

import GameController
import Styles
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Modality
import kotlinx.coroutines.*
import objects.*
import tornadofx.*


fun notifyTable(parent: StackPane, view: GameView): VBox {
     return VBox().apply {
         setMaxSize(250.0, 100.0)
         alignment = Pos.CENTER
         this.hide()
         this.addClass(Styles.notify_table)
         label(if(view.controller.playerIsWin == PlayerOrder.P2) "You win!" else "You lose!")
         hbox(spacing = 30) {
             paddingLeft = 40
             button("New Game") {
                 onLeftClick {
                     find<ChessUI>().openWindow(owner = null, resizable = false)
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

class GameView : View() {
    val controller: GameController by inject()
    lateinit var chesssBoard: ChesssBoard
    private lateinit var notifyEndGame: Pane
    lateinit var glass: Pane
    private val carts = hashMapOf<Boolean, Cart>()
    val MAX_TIME = 600
    private var secondCount = 0
    private val time = SimpleStringProperty("Computation time: ${secondCount}.000 seconds")
    private lateinit var timeLabel: Text
    val timer = TimeCounter(this)

    override val root = stackpane {
        setPrefSize(1200.0, 820.0)
        vbox {
            hbox {
                stackpane {
                    chesssBoard = chessBoard(controller.isWhite.value, this@GameView)
                    glass = pane {
                        style = "-fx-background-color: rgba(255, 255, 255, 0.0); -fx-background-radius: 0;"
                        hide()
                    }
                }
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
                                } else {
                                    text = "Start"
                                    chesssBoard.resetBoardState()
                                }
                                println(controller.mode)
                                if(controller.isAIMove)
                                    performAIFirstMove()
                            }
                        }
                        button("New Game") {
                            onLeftClick {
                                find<ChessUI>().openWindow(owner = null, resizable = false)
                                this@GameView.close()
                            }
                        }
                        button("Quit") {
                            onLeftClick { this@GameView.close() }
                        }
                    }
                    timeLabel = text(time).apply {
                        paddingLeft = 20
                        this.translateY = 20.0
                        font = Font.font("Helvetica", FontWeight.BOLD, 30.0);
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
                carts[true] = cart(opponentIsWhite = !controller.isWhite.value, this@GameView) {
                    paddingLeft = 40
                    paddingRight = 30
                    paddingTop = 20
                }

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
                carts[false] = cart(opponentIsWhite = controller.isWhite.value, this@GameView) {
                    paddingLeft = 40
                    paddingRight = 30
                    paddingTop = 20
                }
            }
        }
        notifyEndGame = notifyTable(this, this@GameView)
    }
    val aiPlayer: Player

    init {
        currentWindow?.setOnCloseRequest {Platform.exit()}
        aiPlayer = Player(this)
    }
    private fun startGame() {
        find<ChessUI>().openWindow(modality = Modality.APPLICATION_MODAL, block = true)
    }
    fun performAIFirstMove() {
        aiPlayer.move()
    }

    internal fun showComputationTime() {
        this.secondCount = 0
        this.time.value = "Computation time: 0.000 seconds"
    }

    internal fun updateComputationTime() {
        time.value = "Computation time: ${secondCount++}.xxx seconds"
    }

    internal fun updateFinalTime(millis: Long) {
        time.value = "Computation time: ${millis/1000.0} seconds"
    }
}

class TimeCounter(val view: GameView) {
    private var timer: Job? = null
    private var last = 0L
    private var now = 0L

    inline fun start(cycleCount: Int = 600, crossinline action: () -> Unit) = GlobalScope.launch {
        var temp = 0
        while (temp++ < cycleCount) {
            withContext(Dispatchers.Main) {
                action()
            }
            delay(1000L)
        }
    }

    internal fun startTimer() {
        view.showComputationTime()
        last = System.currentTimeMillis()
        timer = start {
                view.updateComputationTime()
                println("HIHI")
        }
    }

    internal fun stopTimer() {
        now = System.currentTimeMillis()
        view.updateFinalTime(now - last)
        this.timer?.cancel()
    }
}

