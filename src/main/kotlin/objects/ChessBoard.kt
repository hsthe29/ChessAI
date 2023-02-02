package objects

import COMPUTING
import Move
import algorithm.findRandomMove
import algorithm.performBestMove
import core.*
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import kotlinx.coroutines.*
import tornadofx.*

class Cell(private val row: Int, private val col: Int): StackPane() {
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

    fun releasePiece(): Piece? {
        val temp = this.piece
        this.piece = null
        return temp
    }

    fun moveTop() {
        if(piece != null) {
            piece!!.image.toFront()
        }
    }
}

val chessBoard = ChessBoard()

class ChessBoard : GridPane() {
    private var moves = listOf<Move>()
    private var active: Pair<Int, Int>? = null
    private val cells = Array(8) { i -> Array(8) { j -> Cell(i, j) } }
    val carts = Cart()

    init {
        this.padding = Insets(10.0, 10.0, 10.0, 10.0)
        for(i in 0 until 8) {
            for (j in 0 until 8) {
                cells[i][j].setOnMouseClicked {
                    cellClick(i, j)
                }
                this.add(cells[i][j], j, i)
            }
        }
    }

    private fun cellClick(row: Int, col: Int) {
        if(engine.turn != engine.player) return
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
                uiMove(m) // rendered
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun aiSearch() {
        engine.search()
    }

    private fun uiMove(move: Move) {
        if(engine.board[move.to] != null) {
            val rev = engine.board[move.to]!!.copy()
            chessBoard.carts.updateCart(rev)
        }
        engine.makeMove(move)
        val row = rank(move.to)
        val col = file(move.to)
        engine.trace = Pair(
            Pair(8 - sqLoc(move.from)[1].digitToInt(), sqLoc(move.from).codePointAt(0) - 'a'.code),
            Pair(8 - sqLoc(move.to)[1].digitToInt(), sqLoc(move.to).codePointAt(0) - 'a'.code))
        update()
    }

    fun update() {
        val board = engine.board
        for(i in 0 until 8) {
            for(j in 0 until 8) {
                val loc = LOCATION[i][j]
                cells[i][j].resetState()
                if(board[SQUARES[loc]] == null)
                    continue
                val piece = board[SQUARES[loc]]!!
                cells[i][j].display(piece)
            }
        }

        for(move in moves) {
            val row = rank(move.to)
            val col = file(move.to)
            cells[row][col].addEffect(ImageView(movableImg).apply{ opacity = 0.8 })
            if(cells[row][col].occupied)
                cells[row][col].addEffect(ImageView(attackedImg).apply{ opacity = 0.6 })
        }
        println("trace: ${engine.trace}")
        engine.trace?.let{
            cells[it.first.first][it.first.second].addEffect(ImageView(traceImg).apply{ opacity = 0.6 })
            cells[it.second.first][it.second.second].addEffect(ImageView(traceImg).apply{ opacity = 0.6 })
        }
        active?.let {
            cells[it.first][it.second].addEffect(ImageView(traceImg))
        }

        for(i in 0 until 8) {
            for(j in 0 until 8) {
                cells[i][j].moveTop()
            }
        }
    }
}

inline fun EventTarget.chessBoard(op: ChessBoard.() -> Unit = {}) =
    opcr(this, ChessBoard(), op)