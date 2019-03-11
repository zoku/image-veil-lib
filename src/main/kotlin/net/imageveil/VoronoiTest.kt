package net.imageveil

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFrame
import kotlin.math.floor
import kotlin.math.min
import kotlin.random.Random

class VoronoiTest : JFrame() {
    private val catImage: BufferedImage = ImageIO.read(File(VoronoiTest::class.java.getResource("/cat.jpg").toURI()))
    private var canvas = BufferedImage(catImage.width, catImage.height, BufferedImage.TYPE_INT_RGB)

    private fun run(cellsX: Int, cellsY: Int, image: BufferedImage): BufferedImage {
        val startPrepare = System.nanoTime()
        val tmpImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        val cellWidth = image.width / cellsX
        val cellHeight = image.height / cellsY
        val rng = Random(System.currentTimeMillis())

        val xs = IntArray(cellsX * cellsY)
        val ys = IntArray(cellsX * cellsY)
        val endPrepare = System.nanoTime()

        val startGenerate = System.nanoTime()
        for (y in 0 until cellsY) {
            for (x in 0 until cellsX) {
                xs[y * cellsX + x] = x * cellWidth + rng.nextInt(cellWidth)
                ys[y * cellsX + x] = y * cellHeight + rng.nextInt(cellHeight)
            }
        }
        val endGenerate = System.nanoTime()

        val startPaint = System.nanoTime()
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val currentCellX = min(floor(x.toDouble() / cellWidth.toDouble()).toInt(), cellsX - 1)
                val currentCellY = min(floor(y.toDouble() / cellHeight.toDouble()).toInt(), cellsY - 1)

                val nearestPoint = currentCellY * cellsX + currentCellX

                val displacements = intArrayOf(
                    nearestPoint - cellsX - 1,
                    nearestPoint - cellsX,
                    nearestPoint - cellsX + 1,
                    nearestPoint - 1,
                    nearestPoint,
                    nearestPoint + 1,
                    nearestPoint + cellsX - 1,
                    nearestPoint + cellsX,
                    nearestPoint + cellsX + 1
                )

                val adjacentPoints = arrayListOf<Int>()
                displacements.forEach {
                    if (it >= 0 && it <= xs.lastIndex) { adjacentPoints.add(it) }
                }

                var n = 0
                adjacentPoints.forEach {
                    if (distSq(xs[it], x, ys[it], y) < distSq(xs[n], x, ys[n], y)) n = it
                }

                tmpImage.setRGB(x, y, image.getRGB(xs[n], ys[n]))
            }
        }
        val endPaint = System.nanoTime()

        /*
        println("""
            Preparation: ${(endPrepare - startPrepare) / 1_000_000f} ms
            Generation: ${(endGenerate - startGenerate) / 1_000_000f} ms
            Painting: ${(endPaint - startPaint) / 1_000_000f} ms
        """.trimIndent())
        */

        val time = (endPrepare - startPrepare) + (endGenerate - startGenerate) + (endPaint - startPaint)

        val g2d = tmpImage.graphics as Graphics2D
        g2d.color = Color(0xff00ff)
        g2d.drawString("${"%.2f".format(1_000_000_000f / time)} fps", 50, 50)
        g2d.dispose()

        return tmpImage
    }

    init {
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.setSize(catImage.width, catImage.height)
        this.getRootPane().isDoubleBuffered = true

        this.isVisible = true

        for (i in 0 .. Int.MAX_VALUE) {
            canvas = run(40, 30, catImage)
            this.repaint()
        }
    }

    override fun paint(g: Graphics) {
        g.drawImage(canvas, 0, 0, this)
    }

    private fun distSq(x1: Int, x2: Int, y1: Int, y2: Int): Int {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)
    }
}

fun main() {
    VoronoiTest()
}