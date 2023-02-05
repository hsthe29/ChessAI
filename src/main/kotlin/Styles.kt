import javafx.scene.paint.Color
import tornadofx.*

class Styles: Stylesheet() {
    companion object {
        val frame by cssclass()
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
        frame {
            backgroundColor += c("#9e9da6")
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
            strokeWidth = 2.px
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