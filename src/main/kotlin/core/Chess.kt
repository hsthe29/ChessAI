package core

const val BLACK = 'b'
const val WHITE = 'w'

const val EMPTY = -1

const val PAWN = 'p'
const val KNIGHT = 'n'
const val BISHOP = 'b'
const val ROOK = 'r'
const val QUEEN = 'q'
const val KING = 'k'

const val SYMBOLS = "pnbrqkPNBRQK"

const val DEFAULT_POSITION =
    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

val POSSIBLE_RESULTS = arrayOf("1-0", "0-1", "1/2-1/2", "*")



val PIECE_OFFSETS = hashMapOf(
    'n' to intArrayOf(-18, -33, -31, -14, 18, 33, 31, 14),
    'b' to intArrayOf(-17, -15, 17, 15),
    'r' to intArrayOf(-16, 1, 16, -1),
    'q' to intArrayOf(-17, -16, -15, 1, 17, 16, 15, -1),
    'k' to intArrayOf(-17, -16, -15, 1, 17, 16, 15, -1)
)

// prettier-ignore
var ATTACKS = intArrayOf(
    20, 0, 0, 0, 0, 0, 0, 24,  0, 0, 0, 0, 0, 0,20, 0,
    0,20, 0, 0, 0, 0, 0, 24,  0, 0, 0, 0, 0,20, 0, 0,
    0, 0,20, 0, 0, 0, 0, 24,  0, 0, 0, 0,20, 0, 0, 0,
    0, 0, 0,20, 0, 0, 0, 24,  0, 0, 0,20, 0, 0, 0, 0,
    0, 0, 0, 0,20, 0, 0, 24,  0, 0,20, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0,20, 2, 24,  2,20, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 2,53, 56, 53, 2, 0, 0, 0, 0, 0, 0,
    24,24,24,24,24,24,56,  0, 56,24,24,24,24,24,24, 0,
    0, 0, 0, 0, 0, 2,53, 56, 53, 2, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0,20, 2, 24,  2,20, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0,20, 0, 0, 24,  0, 0,20, 0, 0, 0, 0, 0,
    0, 0, 0,20, 0, 0, 0, 24,  0, 0, 0,20, 0, 0, 0, 0,
    0, 0,20, 0, 0, 0, 0, 24,  0, 0, 0, 0,20, 0, 0, 0,
    0,20, 0, 0, 0, 0, 0, 24,  0, 0, 0, 0, 0,20, 0, 0,
    20, 0, 0, 0, 0, 0, 0, 24,  0, 0, 0, 0, 0, 0,20
)

// prettier-ignore
val RAYS = intArrayOf(
    17,  0,  0,  0,  0,  0,  0, 16,  0,  0,  0,  0,  0,  0, 15, 0,
    0, 17,  0,  0,  0,  0,  0, 16,  0,  0,  0,  0,  0, 15,  0, 0,
    0,  0, 17,  0,  0,  0,  0, 16,  0,  0,  0,  0, 15,  0,  0, 0,
    0,  0,  0, 17,  0,  0,  0, 16,  0,  0,  0, 15,  0,  0,  0, 0,
    0,  0,  0,  0, 17,  0,  0, 16,  0,  0, 15,  0,  0,  0,  0, 0,
    0,  0,  0,  0,  0, 17,  0, 16,  0, 15,  0,  0,  0,  0,  0, 0,
    0,  0,  0,  0,  0,  0, 17, 16, 15,  0,  0,  0,  0,  0,  0, 0,
    1,  1,  1,  1,  1,  1,  1,  0, -1, -1,  -1,-1, -1, -1, -1, 0,
    0,  0,  0,  0,  0,  0,-15,-16,-17,  0,  0,  0,  0,  0,  0, 0,
    0,  0,  0,  0,  0,-15,  0,-16,  0,-17,  0,  0,  0,  0,  0, 0,
    0,  0,  0,  0,-15,  0,  0,-16,  0,  0,-17,  0,  0,  0,  0, 0,
    0,  0,  0,-15,  0,  0,  0,-16,  0,  0,  0,-17,  0,  0,  0, 0,
    0,  0,-15,  0,  0,  0,  0,-16,  0,  0,  0,  0,-17,  0,  0, 0,
    0,-15,  0,  0,  0,  0,  0,-16,  0,  0,  0,  0,  0,-17,  0, 0,
    -15,  0,  0,  0,  0,  0,  0,-16,  0,  0,  0,  0,  0,  0,-17
)

object SHIFTS: Object<Int>() {
    val p = 0
    val n = 1
    val b = 2
    val r = 3
    val q = 4
    val k = 5

    init { mapping() }
}

object FLAGS: Object<Char>() {
    val NORMAL = 'n'
    val CAPTURE = 'c'
    val BIG_PAWN = 'b'
    val EP_CAPTURE = 'e'
    val PROMOTION = 'p'
    val KSIDE_CASTLE = 'k'
    val QSIDE_CASTLE = 'q'
    init { mapping() }
}

object BITS: Object<Int>() {
    val NORMAL = 1
    val CAPTURE = 2
    val BIG_PAWN = 4
    val EP_CAPTURE = 8
    val PROMOTION = 16
    val KSIDE_CASTLE = 32
    val QSIDE_CASTLE = 64

    init { mapping() }
}

const val RANK_1 = 7
const val RANK_2 = 6
const val RANK_3 = 5
const val RANK_4 = 4
const val RANK_5 = 3
const val RANK_6 = 2
const val RANK_7 = 1
const val RANK_8 = 0

// prettier-ignore

val LOCATION = arrayOf(
    arrayOf("a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"),
    arrayOf("a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7"),
    arrayOf("a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6"),
    arrayOf("a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5"),
    arrayOf("a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4"),
    arrayOf("a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3"),
    arrayOf("a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2"),
    arrayOf("a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"),
)
object SQUARES: Object<Int>() {
    val a8 =   0; val b8 =   1; val c8 =   2; val d8 =   3; val e8 =   4; val f8 =   5; val g8 =    6; val h8 =   7;
    val a7 =  16; val b7 =  17; val c7 =  18; val d7 =  19; val e7 =  20; val f7 =  21; val g7 =   22; val h7 =  23;
    val a6 =  32; val b6 =  33; val c6 =  34; val d6 =  35; val e6 =  36; val f6 =  37; val g6 =   38; val h6 =  39;
    val a5 =  48; val b5 =  49; val c5 =  50; val d5 =  51; val e5 =  52; val f5 =  53; val g5 =   54; val h5 =  55;
    val a4 =  64; val b4 =  65; val c4 =  66; val d4 =  67; val e4 =  68; val f4 =  69; val g4 =   70; val h4 =  71;
    val a3 =  80; val b3 =  81; val c3 =  82; val d3 =  83; val e3 =  84; val f3 =  85; val g3 =   86; val h3 =  87;
    val a2 =  96; val b2 =  97; val c2 =  98; val d2 =  99; val e2 = 100; val f2 = 101; val g2 =  102; val h2 = 103;
    val a1 = 112; val b1 = 113; val c1 = 114; val d1 = 115; val e1 = 116; val f1 = 117; val g1 =  118; val h1 = 119

    init { mapping() }
}





class WBMark<T>(var w: T, var b: T): MutableObject<T>() {
    init { mapping() }
}

class PLoc(val square: Int, val flag: Int): Object<Int>() {
    init { mapping() }
}

val wbDepth = WBMark(w = 3, b = 4)

val ROOKS = WBMark(
    w = arrayOf(
        PLoc(square = SQUARES.a1, flag = BITS.QSIDE_CASTLE),
        PLoc(square = SQUARES.h1, flag = BITS.KSIDE_CASTLE)
    ),
    b = arrayOf(
        PLoc(square = SQUARES.a8, flag = BITS.QSIDE_CASTLE),
        PLoc(square = SQUARES.h8, flag = BITS.KSIDE_CASTLE)
    )
)

val PAWN_OFFSETS = WBMark(
    b = intArrayOf(16, 32, 17, 15),
    w = intArrayOf(-16, -32, -17, -15)
)