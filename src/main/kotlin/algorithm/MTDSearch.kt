package algorithm

import SpecialMoves
import Move
import core.Entry
import core.Board
import kotlin.math.max
import kotlin.math.min
import kotlin.math.abs
import core.*

data class TreeNode(val board: Board, val score: Int, val isMaxNode: Boolean, val move: Move? = null) {

    fun getAllMoves() = board.getLegalMoves(isMaxNode)
    fun getChildren() = board.getLegalMoves(isMaxNode)
        .map{ move(this.board, it, isMaxNode) }
        .sortedBy { it.score*(if(isMaxNode) -1 else 1) }
}

fun move(board: Board, m: Move, isMaxNode: Boolean): TreeNode {
    // di chuyển quân cờ
    val data = board.data()
    /*if(m.isEnPassantMove) {
        data[m.fromRow][m.toCol] = '-'
        data[m.toRow][m.toCol] = data[m.fromRow][m.fromCol]
    }
    else if(m.isCastleMove) {
        data[m.toRow][m.toCol] = data[m.fromRow][m.fromCol]
        if(m.toCol - m.fromCol == 2) {
            data[m.toRow][m.toCol-1] = data[m.toRow][m.toCol+1]
            data[m.toRow][m.toCol+1] = '-'
        } else {
            data[m.toRow][m.toCol+1] = data[m.toRow][m.toCol-2]
            data[m.toRow][m.toCol-2] = '-'
        }
    }
    else { data[m.toRow][m.toCol] = data[m.fromRow][m.fromCol] }
    data[m.fromRow][m.fromCol] = '-'

    // cập nhật vị trí của quân vua
    var wKPos = board.wkLoc
    var bKPos = board.bkLoc
    when(board[m.fromRow, m.fromCol]) {
        'k' -> wKPos = Pair(m.toRow, m.toCol)
        'K' -> bKPos =Pair(m.toRow, m.toCol)
    }

    // cập nhật en passant
    val enPassant = if(board[m.fromRow, m.fromCol].lowercaseChar() == 'p' && abs(m.fromRow - m.toRow) == 2)
        Pair((m.fromRow + m.toRow)/2, m.fromCol)
    else  null

    val specs = SpecialMoves(enPassant, board.specialMoves.wk, board.specialMoves.wq, board.specialMoves.bk, board.specialMoves.bq)

    // cập nhật castle move
    when(board[m.fromRow, m.fromCol]) {
        'k' -> {
            specs.wk = false
            specs.wq = false
        }
        'K' -> {
            specs.bk = false
            specs.bq = false
        }
    }

    when(board[m.fromRow, m.fromCol]) {
        in "rR" -> {
            if(m.fromRow == 0 && m.fromCol == 0) {
                specs.bq = false
            }
            if(m.fromRow == 0 && m.fromCol == 7) {
                specs.bk = false
            }
            if(m.fromRow == 7 && m.fromCol == 0) {
                specs.wq = false
            }
            if(m.fromRow == 7 && m.fromCol == 7) {
                specs.wk = false
            }
        }
    }

    when(board[m.toRow, m.toCol]) {
        in "rR" -> {
            if(m.fromRow == 0 && m.fromCol == 0) {
                specs.bq = false
            }
            if(m.fromRow == 0 && m.fromCol == 7) {
                specs.bk = false
            }
            if(m.fromRow == 7 && m.fromCol == 0) {
                specs.wq = false
            }
            if(m.fromRow == 7 && m.fromCol == 7) {
                specs.wk = false
            }
        }
    }

    // cập nhật bitboard
    var bitboard = bitboardOf(data, isMaxNode)
    val searchBoard = Board(data, bitboard, specs, wKPos, bKPos)
//    println(" ---->  Bitboard: ")
//    printBitboard(bitboard)
//    println()*/
    return TreeNode(board, evaluateScore(data), !isMaxNode, m)
}

enum class SearchingMode {
    MTD_BI,
    LIMITED_TIME,
    MINIMAX
}

class MTDSearch(var timeLimit: Int = 0) {

    var nodesVisited = 0
    val tp = TranspositionTable

    /** Iterative deepening MTD-bi search */
    fun search(node: TreeNode, depth: Int = 4, mode: SearchingMode = SearchingMode.MTD_BI): kotlin.Triple<Int, Move?, Int> {
        val hash = computeHash(node.board)
        this.nodesVisited = 0
        when(mode) {
            SearchingMode.MTD_BI -> MTD_biSearch(node, depth)
            SearchingMode.MINIMAX -> 1
            SearchingMode.LIMITED_TIME -> iterativeDeepeningMTD(node, depth)
        }
        println("Move: ${this.tp.getMove(hash, true)}")
        return kotlin.Triple(depth, this.tp.getMove(hash, true), this.tp.getScore(hash, true, depth, true)!!.lower)
    }

    private fun MTD_biSearch(node: TreeNode, depth: Int) {
        var lower = -CHECK_POINT
        var upper = CHECK_POINT
        val eps = 10
        var iter = 20
        while(iter-- > 0) {
            val gamma = (lower + upper + eps) / 2
            val score = this.baseSearch(node, gamma - eps, gamma, depth)
            if((gamma - eps < score) && (score < gamma))
                break
            if (score < gamma)  upper = score else lower = score
        }
    }

    private fun iterativeDeepeningMTD(node: TreeNode, depth: Int) {
        val start = System.currentTimeMillis()
        for(d in 0 until depth) {
            var lower = -CHECK_POINT
            var upper = CHECK_POINT
            val eps = 10
            var iter = 50
            while(iter-- > 0) {
                val gamma = (lower + upper + eps) / 2
                val score = this.baseSearch(node, gamma - eps, gamma, d+1)
                if(System.currentTimeMillis() - start > timeLimit)
                    return
                if((gamma - eps < score) && (score < gamma))
                    break
                if (score < gamma)  upper = score else lower = score
            }
        }
    }

    private fun baseSearch(node: TreeNode, alpha: Int, beta: Int, depth : Int, root: Boolean = true): Int {
        this.nodesVisited++

        val hash = computeHash(node.board)
//        val indent = "|" + "---|".repeat(4 - depth) + ">>>>"
//        val indent2 = "$indent    |"
//        println("$indent Search for depth = $depth, score = ${node.score}")
        var score = 0
        var entry = this.tp.getScore(hash, node.isMaxNode, depth, root)
//        println("$indent2 Retrieve ($node, $depth, $root) is $entry")
        if(entry == null)
            entry = Entry(-CHECK_POINT, CHECK_POINT)
        if(entry.lower >= beta)
            return entry.lower
        if(entry.upper <= alpha)
            return entry.upper

        val _alpha = max(alpha, entry.lower)
        val _beta = min(beta, entry.upper)

//        println("$indent2 alpha = $_alpha, beta = $_beta")

        var bestMove: Move? = null
        if(depth == 0) {
            score = node.score
        } else if(node.isMaxNode){
            score = -CHECK_POINT
            var a = _alpha
            val children = node.getChildren()
//            moves.forEach{ println(it) }
            for(child in children) {
//                println("$indent2 Move from [${m.fromRow}, ${m.fromCol}] to [${m.toRow}, ${m.toCol}]")
                val temp = baseSearch(child, a, _beta, depth - 1, root = false)
                if(temp > score) {
//                    println("$indent2 \u001B[32mNew score: $temp")
                    score = temp
//                    println("$indent2 Max score: $score of depth: $depth, move: $m\u001B[0m")
                    a = max(a, score)
                    if(score >= _beta) {
                        bestMove = child.move
                        break
                    }
                } else {
//                    println("$indent2 Do re-search")
                }
            }
        } else {
            score = CHECK_POINT
            var b = _beta
            val children = node.getChildren()
            for(child in children) {
//                println("$indent2 Move from [${m.fromRow}, ${m.fromCol}] to [${m.toRow}, ${m.toCol}]")
                val temp = baseSearch(child, _alpha, b, depth - 1, root = false)
                if(temp < score) {
//                    println("$indent2 \u001B[32mNew score: $temp")
                    score = temp
//                    println("$indent2 Min score: $score of depth: $depth, move: $m\u001B[0m")
                    b = min(b, score)
                    if(score <= _alpha) {
                        bestMove = child.move
                        break
                    }
                }
            }
        }
//        println("$indent2 After")
        if(bestMove != null) {
//            println("$indent2 Store move: \u001B[32m$node = $bestMove\u001B[0m")
            this.tp.setMove(hash, node.isMaxNode, value = bestMove)
        }

//        println("$indent2 Score: $score, alpha: $alpha, beta: $beta")

        // Traditional transposition table storing of bounds */
        /* Fail low result implies an upper bound */
        if(score <= alpha) {
            entry.upper = score
            this.tp.setScore(hash, node.isMaxNode, depth, root, value = entry)
        }
//        /* Found an accurate minimax value – will not occur if called with zero window */
        if(score > alpha && score < beta) {
            entry.lower = score
            entry.upper = score
            this.tp.setScore(hash, node.isMaxNode, depth, root, value = entry)
        }
//        /* Fail high result implies a lower bound */
        if(score >= beta) {
            entry.lower = score
            this.tp.setScore(hash, node.isMaxNode, depth, root, value = entry)
        }
//        println("$indent2 Store score: \u001B[32m($node, $depth, $root) = $entry\u001B[0m")
//        println(indent)

        return score
    }
}