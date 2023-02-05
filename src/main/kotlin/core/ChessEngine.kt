package core

import Move
import MoveInfo
import algorithm.checkStatus
import algorithm.boardScore
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.*
import objects.ChessTimer
import objects.Player
import java.util.*

val engine = ChessEngine()
var AI = Player()
class ChessEngine {
    val board = Array<PieceInfo?>(128) { null }
    var mode = GameMode.PvsCOM
    private var kings = WBMark(w =  EMPTY, b = EMPTY)
    var turn = '-'
    var player = WHITE
    private var castling = WBMark(w = 0, b = 0)
    var trace: Pair<Pair<Int, Int>, Pair<Int, Int>>? = null
    private var epSquare = EMPTY
    var halfMoves = 0
    private var moveNumber = 1
    var history = mutableListOf<MoveInfo>()
    private var secs = 0
    val time = SimpleStringProperty("Computation time: 0.000 seconds")
    val thinking = SimpleStringProperty("Thinking: ")
    val timer = ChessTimer(this)
    private val positions = hashMapOf<String, Int>()
    private val threeRep = hashSetOf<String>()
    var inCheck = false
    var lenMoves = 0
    var endGame = SimpleBooleanProperty(false)
    init {
        loadFEN(DEFAULT_POSITION)
    }

    private fun loadFEN(fen:String) {
        val tokens = fen.split("\\s+".toRegex())
        val position = tokens[0]
        var square = 0
        for (element in position) {
            if (element == '/') {
                square += 8
            } else if (isDigit(element)) {
                square += element.digitToInt()
            } else {
                val color = if (element < 'a') WHITE else BLACK
                put(PieceInfo(type = element.lowercaseChar(), color = color ), sqLoc(square))
                square++
            }
        }

        if ('K' in tokens[2]) {
            castling.w = castling.w or BITS.KSIDE_CASTLE
        }
        if ('Q' in tokens[2]) {
            castling.w = castling.w or BITS.QSIDE_CASTLE
        }
        if ('k' in tokens[2]) {
            castling.b = castling.b or BITS.KSIDE_CASTLE
        }
        if ('q' in tokens[2]) {
            castling.b = castling.b or BITS.QSIDE_CASTLE
        }

        epSquare = if(tokens[3][0] == '-') EMPTY else SQUARES[tokens[3]]
        halfMoves = tokens[4].toInt()
        moveNumber = tokens[5].toInt()
    }

    private fun put(piece: PieceInfo, square: String): Boolean {
        /* check for valid square */
        if (square !in SQUARES) { return false }

        val sq = SQUARES[square]

        /* don't let the user place more than one king */
        if (piece.type == KING &&
            !(kings[piece.color] == EMPTY || kings[piece.color] == sq)
        ) { return false }

        board[sq] = PieceInfo(type = piece.type, color = piece.color)
        if (piece.type == KING) {
            kings[piece.color] = sq
        }
        return true
    }

    private fun buildMove(board: Array<PieceInfo?>, from: Int, to: Int, flags: Int, promotion: Char? = null): Move {
        val move = Move(
            color = turn,
            from = from,
            to = to,
            flags = flags,
            piece = board[from]!!.type
        )

        if (promotion != null) {
            move.flags = move.flags or BITS.PROMOTION
            move.promotion = promotion
        }

        if (board[to] != null) {
            move.captured = board[to]?.type
        } else if (flags and BITS.EP_CAPTURE != 0) {
            move.captured = PAWN
        }
        return move
    }

    private fun addMove(board: Array<PieceInfo?>, moves: MutableList<Move>, from: Int, to: Int, flags: Int) {
        /* if pawn promotion */
        if (board[from]?.type == PAWN && (rank(to) == RANK_8 || rank(to) == RANK_1)) {
            val pieces = charArrayOf(QUEEN, ROOK, BISHOP, KNIGHT)
            for(p in pieces) {
                moves.add(buildMove(board, from, to, flags, p))
            }
        } else {
            moves.add(buildMove(board, from, to, flags))
        }
    }

    fun generateMoves(options: Any? = null): MutableList<Move> {
        val st = System.currentTimeMillis()
        val moves = mutableListOf<Move>()
        val us = turn
        val them = swapColor(us)
        val secondRank = WBMark(b = RANK_7, w = RANK_2)

        var firstSq = SQUARES.a8
        var lastSq = SQUARES.h1
        var singleSquare = false

        /* are we generating moves for a single square? */
        if (options is String) {
            if (options in SQUARES) {
                firstSq = SQUARES[options]
                lastSq = SQUARES[options]
                singleSquare = true
            } else {
                /* invalid square */
                return mutableListOf()
            }
        }
        var i = firstSq-1
        while(++i <= lastSq) {
            /* did we run off the end of the board */
            if (i and 0x88 != 0) {
                i += 7
                continue
            }
            val piece = board[i]
            if (piece == null || piece.color != us) {
                continue
            }
            if (piece.type == PAWN) {
                /* single square, non-capturing */
                val square = i + PAWN_OFFSETS[us][0]
                if (board[square] == null) {
                    addMove(board, moves, i, square, BITS.NORMAL)

                    /* double square */
                    val square = i + PAWN_OFFSETS[us][1]
                    if (secondRank[us] == rank(i) && board[square] == null) {
                        addMove(board, moves, i, square, BITS.BIG_PAWN)
                    }
                }

                /* pawn captures */
                for (j in 2 until 4) {
                    val square = i + PAWN_OFFSETS[us][j]
                    if (square and 0x88 != 0) continue

                    if (board[square] != null && board[square]?.color == them) {
                        addMove(board, moves, i, square, BITS.CAPTURE)
                    } else if (square == epSquare) {
                        addMove(board, moves, i, epSquare, BITS.EP_CAPTURE)
                    }
                }
            } else {
                for (offset in PIECE_OFFSETS[piece.type]!!) {
                    var square = i
                    while (true) {
                        square += offset
                        if (square and 0x88 != 0) break
                        if (board[square] == null) {
                            addMove(board, moves, i, square, BITS.NORMAL)
                        } else {
                            if (board[square]!!.color == us) break
                            addMove(board, moves, i, square, BITS.CAPTURE)
                            break
                        }
                        /* break, if knight or king */
                        if (piece.type == KNIGHT || piece.type == KING) break
                    }
                }
            }
        }
        /* check for castling if: a) we're generating all moves, or b) we're doing
         * single square move generation on the king's square
         */
        if (!singleSquare || lastSq == kings[us]) {
            /* king-side castling */
            if (castling[us] and BITS.KSIDE_CASTLE != 0) {
                val castlingFrom = kings[us]
                val castlingTo = castlingFrom + 2

                if (board[castlingFrom + 1] == null &&
                    board[castlingTo] == null &&
                    !attacked(them, kings[us]) &&
                    !attacked(them, castlingFrom + 1) &&
                    !attacked(them, castlingTo)
                ) {
                    try {
                        addMove(board, moves, kings[us], castlingTo, BITS.KSIDE_CASTLE)
                    } catch(e: Exception) {
                        println(kings[us])
                        Platform.exit()
                    }
                }
            }

            /* queen-side castling */
            if (castling[us] and BITS.QSIDE_CASTLE != 0) {
                val castlingFrom = kings[us]
                val castlingTo = castlingFrom - 2

                if (
                    board[castlingFrom - 1] == null &&
                    board[castlingFrom - 2] == null &&
                    board[castlingFrom - 3] == null &&
                    !attacked(them, kings[us]) &&
                    !attacked(them, castlingFrom - 1) &&
                    !attacked(them, castlingTo)
                ) {
                    addMove(board, moves, kings[us], castlingTo, BITS.QSIDE_CASTLE)
                }
            }
        }

        /* return all pseudo-legal moves (this includes moves that allow the king
         * to be captured)
         */

        /* filter out illegal moves */
        val legalMoves = mutableListOf<Move>()
        for (move in moves) {
            makeMove(move, true)
            if (!kingAttacked(us)) {
                legalMoves.add(move)
            }
            undoMove(true)
        }
        genTime += System.currentTimeMillis() - st
        return legalMoves
    }

    fun allMoves(): MutableList<Move> {
        val mvs = generateMoves()
        inCheck = inCheck()
        lenMoves = mvs.size
        return mvs
    }

    private fun attacked(color: Char, square: Int): Boolean {
        var i = SQUARES.a8-1
        while(++i <= SQUARES.h1) {
            /* did we run off the end of the board */
            if (i and 0x88 != 0) {
                i += 7
                continue
            }
            /* if empty square or wrong color */
            if (board[i] == null || board[i]!!.color != color) continue

            val piece = board[i]
            val difference = i - square
            val index = difference + 119

                if (ATTACKS[index] and (1 shl SHIFTS[piece!!.type]) != 0) {
                    if (piece.type == PAWN) {
                        if (difference > 0) {
                            if (piece.color == WHITE) return true
                        } else {
                            if (piece.color == BLACK) return true
                        }
                        continue
                    }

                    /* if the piece is a knight or a king */
                    if (piece.type == KNIGHT || piece.type == KING) return true

                    val offset = RAYS[index]
                    var j = i + offset

                    var blocked = false
                    while (j != square) {
                        if (board[j] != null) {
                            blocked = true
                            break
                        }
                        j += offset
                    }
                    if (!blocked) return true
                }
        }
        return false
    }

    private fun storeHistory(move: Move) {
        history.add(
            MoveInfo(
                move = move,
                kings = WBMark(b = kings.b, w = kings.w),
                turn = turn,
                castling = WBMark(b = castling.b, w = castling.w),
                epSquare = epSquare,
                halfMoves = halfMoves,
                moveNumber = moveNumber
            )
        )
    }

    fun makeMove(move: Move, shallow: Boolean = false) {
        val us = turn
        val them = swapColor(us)
        storeHistory(move)
        board[move.to] = board[move.from]
        board[move.from] = null
        /* if ep capture, remove the captured pawn */
        if (move.flags and BITS.EP_CAPTURE != 0) {
            if (turn == BLACK) {
                board[move.to - 16] = null
            } else {
                board[move.to + 16] = null
            }
        }
        /* if pawn promotion, replace with new piece */
        if (move.flags and BITS.PROMOTION != 0) {
            board[move.to] = PieceInfo(type = move.promotion!!, color = us)
        }
        /* if we moved the king */
        if (board[move.to]?.type == KING) {
            kings[board[move.to]!!.color] = move.to
            /* if we castled, move the rook next to the king */
            if(move.flags and BITS.KSIDE_CASTLE != 0) {
                val castlingTo = move.to - 1
                val castlingFrom = move.to + 1
                board[castlingTo] = board[castlingFrom]
                board[castlingFrom] = null
            } else if (move.flags and BITS.QSIDE_CASTLE != 0) {
                val castlingTo = move.to + 1
                val castlingFrom = move.to - 2
                board[castlingTo] = board[castlingFrom]
                board[castlingFrom] = null
            }

            /* turn off castling */
            castling[us] = 0
        }
        /* turn off castling if we move a rook */
        if (castling[us] != 0) {
            for (info in ROOKS[us]) {
                if (move.from == info.square && (castling[us] and info.flag) != 0) {
                    castling[us] = castling[us] xor info.flag
                    break
                }
            }
        }
        /* turn off castling if we capture a rook */
        if (castling[them] != 0) {
            for (info in ROOKS[them]) {
                if (move.to == info.square && (castling[them] and info.flag) != 0) {
                    castling[them] = castling[them] xor info.flag
                    break
                }
            }
        }
        /* if big pawn move, update the en passant square */
        epSquare = if (move.flags and BITS.BIG_PAWN != 0) {
            if(turn == BLACK) move.to-16 else move.to+16
        } else EMPTY

        /* reset the 50 move counter if a pawn is moved or a piece is captured */
        if (move.piece == PAWN) {
            halfMoves = 0
        } else if (move.flags and (BITS.CAPTURE or BITS.EP_CAPTURE) != 0) {
            halfMoves = 0
        } else {
            halfMoves++
        }
        if (turn == BLACK) {
            moveNumber++
        }
        turn = swapColor(turn)
        if(shallow) return
        val fen = generateFEN()
            .split(' ')
            .slice(0 until 4)
            .joinToString(" ")
        positions[fen] = if(fen in positions) positions[fen]!! + 1 else 1
        if(positions[fen] == 3) threeRep.add(fen)
    }

    fun undoMove(shallow: Boolean = false): Move? {
        val old = history.removeLastOrNull() ?: return null

        if(!shallow) {
            val fen = generateFEN()
                .split(' ')
                .slice(0 until 4)
                .joinToString(" ")
            positions[fen] = positions[fen]!! - 1
            if (positions[fen] == 2) threeRep.remove(fen)
        }

        val move = old.move
        kings = old.kings
        turn = old.turn
        castling = old.castling
        epSquare = old.epSquare
        halfMoves = old.halfMoves
        moveNumber = old.moveNumber

        val us = turn
        val them = swapColor(turn)

        board[move.from] = board[move.to]
        board[move.from]!!.type = move.piece // to undo any promotions
        board[move.to] = null

        if (move.flags and BITS.CAPTURE != 0) {
            board[move.to] = PieceInfo(type = move.captured!!, color = them)
        } else if (move.flags and BITS.EP_CAPTURE != 0) {
            val index = if (us == BLACK) move.to-16 else move.to+16
            board[index] = PieceInfo(type = PAWN, color = them)
        }

        if (move.flags and (BITS.KSIDE_CASTLE or BITS.QSIDE_CASTLE) != 0) {
            var castlingTo: Int = -1
            var castlingFrom: Int = -1
            if (move.flags and BITS.KSIDE_CASTLE != 0) {
                castlingTo = move.to + 1
                castlingFrom = move.to - 1
            } else if (move.flags and BITS.QSIDE_CASTLE != 0) {
                castlingTo = move.to - 2
                castlingFrom = move.to + 1
            }
            if(castlingTo > -1 && castlingFrom > -1) {
                board[castlingTo] = board[castlingFrom]
                board[castlingFrom] = null
            }
        }

        return move
    }

    fun generateFEN(): String {
        var empty = 0
        var fen = ""

        var i = SQUARES.a8 - 1
        while(++i <= SQUARES.h1) {
            if (board[i] == null) {
                empty++
            } else {
                if (empty > 0) {
                    fen += empty
                    empty = 0
                }
                val color = board[i]!!.color
                val piece = board[i]!!.type
                fen += if(color == WHITE) piece.uppercaseChar() else piece.lowercaseChar()
            }

            if ((i + 1) and 0x88 != 0) {
                if (empty > 0) {
                    fen += empty
                }
                if (i != SQUARES.h1) {
                    fen += '/'
                }
                empty = 0
               i += 8
            }
        }

        var cflags = ""
        if (castling[WHITE] and BITS.KSIDE_CASTLE != 0) {
            cflags += 'K'
        }
        if (castling[WHITE] and BITS.QSIDE_CASTLE != 0) {
            cflags += 'Q'
        }
        if (castling[BLACK] and BITS.KSIDE_CASTLE != 0) {
            cflags += 'k'
        }
        if (castling[BLACK] and BITS.QSIDE_CASTLE != 0) {
            cflags += 'q'
        }

        /* do we have an empty castling flag? */
        if(cflags == "")
            cflags = "-"
        val epflags = if(epSquare == EMPTY) "-" else sqLoc(epSquare)

        return listOf(fen, turn, cflags, epflags, halfMoves, moveNumber).joinToString(" ")
    }

    private fun kingAttacked(color: Char) = attacked(swapColor(color), kings[color])

    fun inCheck() = kingAttacked(turn)

    fun inCheckmate() = inCheck() && generateMoves().size == 0

    fun inStalemate() = !inCheck() && generateMoves().size == 0

    fun insufficientMaterial(): Boolean {
        val pieces = hashMapOf<Char, Int>()
        val bishops = mutableListOf<Int>()
        var numPieces = 0
        var sqColor = 0

        var i = SQUARES.a8-1
        while (++i <= SQUARES.h1) {
            sqColor = (sqColor + 1) % 2
            if ((i and 0x88) != 0) {
                i += 7
                continue
            }
            val piece = board[i]
            if (piece != null) {
                pieces[piece.type] = if(piece.type in pieces) pieces[piece.type]!! + 1 else 1
                if (piece.type == BISHOP) {
                    bishops.add(sqColor)
                }
                numPieces++
            }
        }

        /* k vs. k */
        if (numPieces == 2) {
            return true
        } else if (
        /* k vs. kn .... or .... k vs. kb */
            numPieces == 3 &&
            (pieces[BISHOP] == 1 || pieces[KNIGHT] == 1)
        ) {
            return true
        } else if (numPieces - 2 == pieces[BISHOP]) {
            /* kb vs. kb where any number of bishops are all on the same color */
            var sum = 0
            val len = bishops.size
            for (bs in bishops) {
                sum += bs
            }
            if (sum == 0 || sum == len) {
                return true
            }
        }

        return false
    }

    fun inThreefoldRepetition(): Boolean {
        return threeRep.isNotEmpty()
    }

    fun inDraw() = halfMoves >= 100 ||
                        inStalemate() ||
                        insufficientMaterial() ||
                        inThreefoldRepetition()

    fun display(): String {
        var s = "   +------------------------+\n"
        var i = SQUARES.a8
        while(i <= SQUARES.h1) {
            /* display the rank */
            if (file(i) == 0) {
                s += " ${"87654321"[rank(i)]} |"
            }

            /* empty piece */
            s += if (board[i] == null) {
                " . "
            } else {
                val piece = board[i]!!.type
                val color = board[i]!!.color
                val symbol = if(color == WHITE) piece.uppercaseChar() else piece.lowercaseChar()
                " $symbol "
            }

            if ((i + 1) and 0x88 != 0) {
                s += "|\n"
                i += 8
            }
            i++
        }
        s += "   +------------------------+\n"
        s += "     a  b  c  d  e  f  g  h\n"

        return s
    }

    internal fun showComputationTime() {
        this.secs = 0
        this.time.value = "Computation time: 0.000 seconds"
    }

    internal fun updateComputationTime() {
        time.value = "Computation time: ${secs++}.___ seconds"
    }

    internal fun updateFinalTime(millis: Long) {
        time.value = "Computation time: ${millis/1000.0} seconds"
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun search() {
        GlobalScope.launch {
            if (mode == GameMode.PvsCOM) {
                if (turn == BLACK) {
                    AI.searchBestMove()
                    val color = if (turn == BLACK) "BLACK" else "WHITE"
                    checkStatus(color)
                }
            } else {
                while(true) {
                    val color = if (turn == BLACK) "BLACK" else "WHITE"
                    if (checkStatus(color)) break
                    AI.searchBestMove()
                }
            }
        }
    }
}

fun pushMove(move: Move, time: Double, nodeVisited: Int, fen: String) {
    dataHistory.add(0, MoveData(move.piece, "${sqLoc(move.from)} -> ${sqLoc(move.to)}", captured = move.captured?: '-', color = move.color, eval = time, nodeVisited = nodeVisited, pFen = fen, move.from, move.to))
    val index = if(move.color == 'w') WHITE_ORD else BLACK_ORD
    val score = if(move.color == 'b' ) boardScore else -boardScore

    with(stats[index]) {
        this.score = score
        moves++
        totalTime += time
        avgTime = totalTime/moves
        stats[index] = Statistic(color, score, moves, (totalTime*1000).toInt()/1000.0, (avgTime*1000).toInt()/1000.0)
    }
}