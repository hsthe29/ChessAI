package objects

import algorithm.boardScore
import algorithm.checkStatus
import algorithm.evalBoardScore
import core.*
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.scene.text.Font
import javafx.scene.text.Text
import kotlinx.coroutines.launch
import tornadofx.*

class Cell(row: Int, col: Int): StackPane() {
    private val effects = mutableSetOf<Node>()
    var piece: Piece? = null
        set(value) {
            if(field != null)
                this.children.remove(field!!.image)
            field = value
            if(value != null) {
                this.add(value.image)
            }
        }

    val occupied: Boolean
        get() = piece != null

    init {
        add(ImageView(if (((row - col) and 1) == 0) lightBackground else darkBackground))
        setPrefSize(80.0, 80.0)
    }

    fun addEffect(eff: ImageView) {
        if(eff !in effects) {
            this.add(eff)
            effects.add(eff)
        }
    }

    fun display(info: PieceInfo) {
        this.piece = Piece(info.color, info.type)
    }

    fun resetState() {
        this.piece = null
        resetEffect()
    }
    private fun resetEffect() {
        children.removeAll(effects)
        effects.clear()
    }

    fun moveTop() {
        if(piece != null) {
            piece!!.image.toFront()
        }
    }
}

val chessBoard = ChessBoard()

class ChessBoard: BorderPane() {
    private var moves = listOf<Move>()
    private var active: Pair<Int, Int>? = null
    private val cells = Array(8) { i -> Array(8) { j -> Cell(i, j) } }
    private val grid = GridPane()
    private val horizontalAxis = GridPane()
    private val verticalAxis = GridPane()
    val carts = Cart()
    init {
        // top right bottom left
        grid.padding = Insets(10.0, 20.0, 0.0, 0.0)
        horizontalAxis.padding = Insets(0.0, 0.0, 0.0, 40.0)
        verticalAxis.padding = Insets(10.0, 0.0, 0.0, 0.0)
        for(i in 0 until 8) {
            for (j in 0 until 8) {
                cells[i][j].setOnMouseClicked {
                    cellClick(i, j)
                }
                this.grid.add(cells[i][j], j, i)
            }
        }
        "abcdefgh".forEachIndexed { i, c ->
            val stp = StackPane()
            stp.add(ImageView("backgrounds/horizontalAxBg.png"))
            stp.add(Text(c.toString()).apply {
                font = Font.font(20.0)
            })
            horizontalAxis.add(stp, i, 0)
        }
        "87654321".forEachIndexed { i, c ->
            val stp = StackPane()
            stp.add(ImageView("backgrounds/verticalAxBg.png"))
            stp.add(Text(c.toString()).apply {
                font = Font.font(20.0)
            })
            verticalAxis.add(stp, 0, i)
        }
        bottom = horizontalAxis
        left = verticalAxis
        center = grid
    }

    private fun cellClick(row: Int, col: Int) {
        if(engine.turn != engine.player || engine.endGame.value || GameMode.COMvsCOM == engine.mode) return
        val loc = LOCATION[row][col]
        if(active == null) {
            if(cells[row][col].occupied) {
                moves = engine.generateMoves(loc)
                active = Pair(row, col)
                update()
            }
        } else {
            val m = validMove(loc)
            if(m != null) {
                active = null
                uiMove(m)
                aiSearch()
            } else {
                if(ally(engine.board[SQUARES[loc]], engine.board[SQUARES[LOCATION[active!!.first][active!!.second]]])) {
                    if(active!!.first != row || active!!.second != col) {
                        active = Pair(row, col)
                        moves = engine.generateMoves(LOCATION[active!!.first][active!!.second])
                    } else active = null
                } else active = null
                update()
            }
        }
    }

    private fun validMove(loc: String): Move? {
        var temp: Move? = null
        for(move in moves) {
            if(SQUARES[loc] == move.to) {
                temp = move
                break
            }
        }
        moves = listOf()
        return temp
    }

    private fun aiSearch() {
        engine.thinking.set("Thinking: ${if(engine.turn == BLACK) "BLACK" else "WHITE"}")
        engine.search()
    }

    private fun uiMove(move: Move) {
        if(engine.board[move.to] != null) {
            val rev = engine.board[move.to]!!.copy()
            chessBoard.carts.updateCart(rev)
        }
        boardScore = evalBoardScore(engine, move.copy(), boardScore, 'b')
        engine.makeMove(move)
        val fen = engine.generateFEN()
            .split(' ')[0]
        engine.thinking.set("Thinking: ${if(engine.turn == BLACK) "BLACK" else "WHITE"}")
        if (engine.turn == BLACK) {
            checkStatus("BLACK")
        } else {
            checkStatus("WHITE")
        }
        pushMove(move, 0.0, 0, fen)
        engine.trace = Pair(
            Pair(rank(move.from), file(move.from)),
            Pair(rank(move.to), file(move.to)))
        update()
        val color = if (engine.turn == BLACK) "BLACK" else "WHITE"
        checkStatus(color)
    }

    fun loadFromFEN(fen: String, from: Int, to: Int) {
        for(i in 0 until 8) {
            for(j in 0 until 8) {
                cells[i][j].resetState()
            }
        }
        var square = 0
        for (element in fen) {
            if (element == '/') {
                square += 8
            } else if (isDigit(element)) {
                square += element.digitToInt()
            } else {
                val color = if (element < 'a') WHITE else BLACK
                val sq = Pair(rank(square), file(square))
                cells[sq.first][sq.second].display(PieceInfo(type = element.lowercaseChar(), color = color))
                square++
            }
        }
        val from = Pair(rank(from), file(from))
        val to = Pair(rank(to), file(to))
        cells[from.first][from.second].addEffect(ImageView(traceImg))
        cells[to.first][to.second].addEffect(ImageView(traceImg))
        cells[from.first][from.second].moveTop()
        cells[to.first][to.second].moveTop()
    }

    fun clearBoard() {
        carts.reset()
        for(i in 0 until 8) {
            for(j in 0 until 8) {
                cells[i][j].resetState()
            }
        }
    }

    fun update() {
        val board = engine.board
        for(i in 0 until 8) {
            for(j in 0 until 8) {
                val loc = LOCATION[i][j]
                cells[i][j].resetState()
                try {
                    val piece = board[SQUARES[loc]] ?: continue
                    cells[i][j].display(piece)
                } catch (e: Exception) {
                    println("Loc: $loc, ${board[SQUARES[loc]]}")
                }
            }
        }

        engine.trace?.let{
            cells[it.first.first][it.first.second].addEffect(ImageView(traceImg))
            cells[it.second.first][it.second.second].addEffect(ImageView(traceImg))
        }

        for(move in moves) {
            val row = rank(move.to)
            val col = file(move.to)
            cells[row][col].addEffect(ImageView(movableImg).apply{ opacity = 0.8 })
            if(cells[row][col].occupied)
                cells[row][col].addEffect(ImageView(attackedImg).apply{ opacity = 0.6 })
        }

        active?.let {
            cells[it.first][it.second].addEffect(ImageView(activeImg))
        }

        for(i in 0 until 8) {
            for(j in 0 until 8) {
                cells[i][j].moveTop()
            }
        }
    }
}
