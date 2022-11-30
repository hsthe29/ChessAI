package view

import Styles
import engine.Engine
import engine.GameProperties
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
//         label(if(view.controller.playerIsWin == PlayerOrder.P2) "You win!" else "You lose!")
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
    val property = GameProperties
    lateinit var chessUI: ChessUI
    val game = Engine()
    private lateinit var notifyEndGame: Pane
    lateinit var glass: Pane
    private val carts = hashMapOf<Boolean, Cart>()
    private var secondCount = 0
    private val time = SimpleStringProperty("Computation time: ${secondCount}.000 seconds")
    private lateinit var timeLabel: Text
    val timer = TimeCounter(this)

    override val root = stackpane {
        setPrefSize(1200.0, 820.0)
        vbox {
            hbox {
                stackpane {
                    chessUI = chessUI(property.youIsWhite.value, game)
                    glass = pane {
                        style = "-fx-background-color: rgba(255, 255, 255, 0.0); -fx-background-radius: 0;"
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
                                }
                                if(property.isAIMove)
                                    game.performFirstMove()
                            }
                        }
                        button("New Game") {
                            onLeftClick {
                                find<Config>().openWindow(modality = Modality.APPLICATION_MODAL, block = true, resizable = false)
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
                carts[true] = cart(opponentIsWhite = !property.youIsWhite.value, this@GameView) {
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
                carts[false] = cart(opponentIsWhite = property.youIsWhite.value, this@GameView) {
                    paddingLeft = 40
                    paddingRight = 30
                    paddingTop = 20
                }
            }
        }
        notifyEndGame = notifyTable(this, this@GameView)
    }

    init {
        currentWindow?.setOnCloseRequest {Platform.exit()}
        game.player = Player(this)
        property.ate.onChange {
            carts[property.pieceAte.player]?.updateCart(property.pieceAte.pieceName, property.pieceAte.mode)
        }
        game.render()
    }
    private fun startGame() {
        find<Config>().openWindow(modality = Modality.APPLICATION_MODAL, block = true, resizable = false)
        glass.hide()
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
//        delay(100)
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
        }
    }

    internal fun stopTimer() {
        now = System.currentTimeMillis()
        view.updateFinalTime(now - last)
        this.timer?.cancel()
    }
}

