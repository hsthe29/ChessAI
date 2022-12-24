import javafx.geometry.Pos
import javafx.scene.paint.Color
import tornadofx.*

class Styles: Stylesheet() {
    companion object {
        val board_cell_light by cssclass()
        val board_cell_dark by cssclass()
        val cart by cssclass()
        val cart_cell by cssclass()
        val line by cssclass()
        val movable by cssclass()
        val attack by cssclass()
        val chess_cell_active by cssclass()
        val trace_cell by cssclass()
        val notify_table by cssclass()
    }
    init {
        board_cell_dark {
            minWidth = 80.px
            minHeight = 80.px
            backgroundColor += c("#454545")
        }

        board_cell_light {
            minWidth = 80.px
            minHeight = 80.px
            backgroundColor += c("#ff6a00")
        }

        cart {
            borderColor += box(Color.BLUE)
            borderWidth += box(2.px)
        }

        cart_cell {
            minWidth = 40.px
            minHeight = 40.px
            borderColor += box(Color.BLUE)
            borderWidth += box(2.px)
            borderRadius += box(7.px)
            backgroundColor += c("#ffffff")
        }

        line {
            strokeWidth = 4.px
        }

        chess_cell_active {
            backgroundColor += c("#62fa5f")
        }

        movable {
            borderColor += box(Color.WHITE)
            borderWidth += box(4.px)
            borderRadius += box(5.px)
            opacity = 0.65
        }
        attack {
            borderColor += box(Color.BLUE)
            borderWidth += box(4.px)
            borderRadius += box(5.px)
            opacity = 0.8
        }
        trace_cell {
            borderColor += box(c("#2cfc03"))
            borderWidth += box(4.px)
//            backgroundColor += c("#2cfc03", 0.5)
            borderRadius += box(40.px)
        }

        notify_table {
            borderColor += box(Color.BLUE)
            borderWidth += box(2.px)
            borderRadius += box(7.px)
            backgroundRadius += box(10.px)
            backgroundColor += c("#dce6da")
        }
    }
}