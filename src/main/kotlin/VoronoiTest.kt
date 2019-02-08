import java.awt.Color
import java.awt.Graphics
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JFrame
import kotlin.math.min

fun distSq(x1: Int, x2: Int, y1: Int, y2: Int): Int {
    val x = x1 - x2
    val y = y1 - y2
    return x * x + y * y
}

class Voronoi : JFrame("Voronoi Diagram") {
    private val bg = ImageIO.read(File("C:\\Users\\m.helbing\\Downloads\\IMG-20171010-WA0002.jpg"))
    private val bi = BufferedImage(bg.width, bg.height, BufferedImage.TYPE_INT_RGB)
    private val r = Random()

    private fun foo(cells: Int) {
        val start = System.currentTimeMillis()

        val spacingX = bg.width / cells
        val spacingY = bg.height / cells

        val points = arrayListOf<Pair<Int, Int>>()

        for (x in 0 until bg.width step spacingX) {
            for (y in 0 until bg.height step spacingY) {

                val factor = 2

                val xr = x + if (r.nextBoolean()) r.nextInt(spacingX / factor) else -r.nextInt(spacingX / factor)
                val yr = y + if (r.nextBoolean()) r.nextInt(spacingY / factor) else -r.nextInt(spacingY / factor)
                points.add(Pair(xr + spacingX / 2, yr + spacingY / 2))
            }
        }

        val colors = IntArray(points.size)

        for (x in 0 until bg.width) {
            for (y in 0 until bg.height) {
                var n = 0
                for (i in 0 until points.size) {
                    if (
                            distSq(points[i].first, x, points[i].second, y) <
                            distSq(points[n].first, x, points[n].second, y)
                    ) {
                        n = i
                    }
                }
                colors[n] = bg.getRGB(x, y)
            }
        }

        for (x in 0 until bg.width) {
            for (y in 0 until bg.height) {
                var n = 0
                for (i in 0 until points.size) {
                    if (distSq(points[i].first, x, points[i].second, y) < distSq(points[n].first, x, points[n].second, y)) n = i
                }
                bi.setRGB(x, y, colors[n])
            }
        }

        this.repaint()
        println("$cells: " + (System.currentTimeMillis() - start) / 1000f)
    }

    init {
        setBounds(0, 0, bg.width, bg.height)
        defaultCloseOperation = EXIT_ON_CLOSE
        this.isVisible = true

        for (i in 5 until 50) {
            foo(i)
        }
    }

    override fun paint(g: Graphics) {
        g.drawImage(bi, 0, 0, this)
    }
}

fun main() {
    Voronoi()
}