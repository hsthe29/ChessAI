package algorithm

import objects.Move

object TranspositionTable {
    private val BUFFER_SIZE = 600_000
    private val tpScore = hashMapOf<String, Bound>()
    private val tpMove = hashMapOf<String, Move>()

    fun getScore(fen: String, depth: Int): Bound? {
        return this.tpScore["$fen $depth"]
    }

    fun getMove(fen: String): Move? {
        return this.tpMove[fen]
    }

    fun setScore(fen: String, depth: Int, value: Bound) {
        if(this.tpScore.size > BUFFER_SIZE)
            this.tpScore.clear()

        this.tpScore["$fen $depth"] = value
    }

    fun setMove(fen: String, value: Move) {
        if(this.tpMove.size > BUFFER_SIZE)
            this.tpMove.clear()

        this.tpMove[fen] = value
    }

    fun clearTable() {
        tpScore.clear()
        tpMove.clear()
    }
}