package objects

import javafx.scene.image.Image


abstract class Piece(protected val color: Boolean) {
    var hasMoved = false
    abstract val moveList: MutableList<MoveList>
    val image: Image = Image("pieces/${colorName}_${name}_1x.png")
    val colorName: String
        get() = if(this.color) "w" else "b"
    abstract val name: String
    abstract fun isSingleMove(): Boolean
}

class Bishop(color: Boolean): Piece(color) {
    override val moveList = mutableListOf(
        MoveList.UP_RIGHT,
        MoveList.DOWN_RIGHT,
        MoveList.DOWN_LEFT,
        MoveList.UP_LEFT
    )

    override val name: String
        get() = "bishop"

    override fun isSingleMove() = false
}

class King(color: Boolean): Piece(color) {
    override val moveList = mutableListOf(
        MoveList.UP,
        MoveList.UP_RIGHT,
        MoveList.RIGHT,
        MoveList.DOWN_RIGHT,
        MoveList.DOWN,
        MoveList.DOWN_LEFT,
        MoveList.LEFT,
        MoveList.UP_LEFT)
    override val name: String
        get() = "king"

    override fun isSingleMove() = true
}

class Queen(color: Boolean): Piece(color) {
    override val moveList = mutableListOf(
        MoveList.UP,
        MoveList.UP_RIGHT,
        MoveList.RIGHT,
        MoveList.DOWN_RIGHT,
        MoveList.DOWN,
        MoveList.DOWN_LEFT,
        MoveList.LEFT,
        MoveList.UP_LEFT
    )

    override val name: String
        get() = "queen"

    override fun isSingleMove() = false
}

class Knight(color: Boolean): Piece(color) {

    override val moveList = mutableListOf(
        MoveList.KNIGHT_LEFT_UP,
        MoveList.KNIGHT_UP_LEFT,
        MoveList.KNIGHT_UP_RIGHT,
        MoveList.KNIGHT_RIGHT_UP,
        MoveList.KNIGHT_RIGHT_DOWN,
        MoveList.KNIGHT_DOWN_RIGHT,
        MoveList.KNIGHT_DOWN_LEFT,
        MoveList.KNIGHT_LEFT_DOWN
    )

    override val name: String
        get() = "knight"

    override fun isSingleMove() = true
}

class Pawn(color: Boolean): Piece(color) {
    override val moveList: MutableList<MoveList>
        get() {
            /*
         * Pawn movement is HIGHLY conditional, so this branches.
         * The list ensures correct direction and two-space movement.
         * All the board-dependent things (like diagonal iff capturing) are ChessBoard's job.
        */
            /*
         * Pawn movement is HIGHLY conditional, so this branches.
         * The list ensures correct direction and two-space movement.
         * All the board-dependent things (like diagonal iff capturing) are ChessBoard's job.
        */
            val isWhite = color

            //braces ensure toArray() works later, see ArrayList docs for why

            //braces ensure toArray() works later, see ArrayList docs for why
            return if (isWhite) {
                val whiteMoves = mutableListOf<MoveList>()

                //standard straight, can't capture using this
                whiteMoves.add(MoveList.UP)

                //diagonals, can and must capture using this
                whiteMoves.add(MoveList.UP_RIGHT)
                whiteMoves.add(MoveList.UP_LEFT)

                //if hasn't moved, UP is valid board move, can't capture using this
                if (!hasMoved) {
                    whiteMoves.add(MoveList.DOUBLE_UP)
                }
                whiteMoves
            } else {
                val blackMoves = mutableListOf<MoveList>()

                //standard straight, can't capture
                blackMoves.add(MoveList.DOWN)

                //diagonals, can and must capture using this
                blackMoves.add(MoveList.DOWN_RIGHT)
                blackMoves.add(MoveList.DOWN_LEFT)

                //if hasn't moved, DOWN is valid board move, can't capture using this
                if (!hasMoved) {
                    blackMoves.add(MoveList.DOUBLE_DOWN)
                }
                blackMoves
            }
        }

    override val name: String
        get() = "pawn"

    override fun isSingleMove() = true
}

class Rook(color: Boolean): Piece(color) {
    override val moveList = mutableListOf(
        MoveList.UP,
        MoveList.RIGHT,
        MoveList.DOWN,
        MoveList.LEFT
    )

    override val name: String
        get() = "rook"

    override fun isSingleMove() = false
}