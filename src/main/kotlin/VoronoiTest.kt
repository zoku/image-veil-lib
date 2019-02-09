import org.imgscalr.Scalr
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JFrame

fun distSq(x1: Int, x2: Int, y1: Int, y2: Int): Int {
    return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)
}

class Voronoi : JFrame("Voronoi Diagram") {
    private var canvas = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
    private val r = Random()

    private fun foo(cells: Int, source: BufferedImage, scaleFactor: Int): BufferedImage {
        val start = System.currentTimeMillis()

        val scaledSource = Scalr.resize(source, source.width / scaleFactor, source.height / scaleFactor)
        val scaledSourceWidth = scaledSource.width
        val scaledSourceHeight = scaledSource.height
        val crystals = BufferedImage(scaledSourceWidth, scaledSourceHeight, BufferedImage.TYPE_INT_RGB)

        val spacingX = scaledSourceWidth / cells
        val spacingY = scaledSourceHeight / cells

        val points = arrayListOf<Pair<Int, Int>>()

        val start4 = System.currentTimeMillis()
        for (x in 0 until scaledSourceWidth step spacingX) {
            for (y in 0 until scaledSourceHeight step spacingY) {

                val factor = 2

                var xr = x + (if (r.nextBoolean()) r.nextInt(spacingX / factor) else -r.nextInt(spacingX / factor)) + spacingX / 2
                var yr = y + (if (r.nextBoolean()) r.nextInt(spacingY / factor) else -r.nextInt(spacingY / factor)) + spacingY / 2

                if (xr >= scaledSourceWidth) xr = scaledSourceWidth - 1
                if (xr < 0) xr = 0
                if (yr >= scaledSourceHeight) yr = scaledSourceHeight - 1
                if (yr < 0) xr = 0

                points.add(Pair(xr, yr))
            }
        }
        println("Calculate ${points.size} points: " + (System.currentTimeMillis() - start4) / 1000f + "s")

        val start3 = System.currentTimeMillis()
        var ms = 0f
        for (x in 0 until scaledSourceWidth) {
            for (y in 0 until scaledSourceHeight) {
                var n = 0

                val start5 = System.nanoTime()
                val xRange = x - spacingX .. x + spacingX
                val yRange = y - spacingY .. y + spacingY

                val adjacentPoints = points.filter { xRange.contains(it.first) && yRange.contains(it.second) }
                ms += (System.nanoTime() - start5)

                for (i in 0 until adjacentPoints.size) {
                    if (distSq(adjacentPoints[i].first, x, adjacentPoints[i].second, y) < distSq(adjacentPoints[n].first, x, adjacentPoints[n].second, y)) n = i
                }
                crystals.setRGB(x, y, scaledSource.getRGB(adjacentPoints[n].first, adjacentPoints[n].second))
            }
        }
        println("Crunch: ${ms / 1_000_000f} ms")
        println("Get colours and render: " + (System.currentTimeMillis() - start3) / 1000f + "s")
        println("Finished ($cells² cells): " + (System.currentTimeMillis() - start) / 1000f + "s")

        return Scalr.resize(crystals, Scalr.Method.ULTRA_QUALITY, source.width, source.height, Scalr.OP_ANTIALIAS)
    }

    init {
        val source = ImageIO.read(File("C:\\Users\\Sero Tonin\\Downloads\\IMG_20190204_212846.jpg"))
        val windowWidth = source.width / 3
        val windowHeight = source.height / 3

        setBounds(0, 0, windowWidth, windowHeight)
        defaultCloseOperation = EXIT_ON_CLOSE
        this.isVisible = true

        for (i in 52 downTo 48) {
            val scaleFactor = 3
            println("\n\nScale factor: $scaleFactor, Cells: $i")
            canvas = Scalr.resize(
                foo(i, source, scaleFactor),
                Scalr.Method.ULTRA_QUALITY,
                windowWidth,
                windowHeight,
                Scalr.OP_ANTIALIAS
            )
            this.repaint()
        }
    }

    override fun paint(g: Graphics) {
        g.drawImage(canvas, 0, 0, this)
    }
}

fun main() {
    Voronoi()
}