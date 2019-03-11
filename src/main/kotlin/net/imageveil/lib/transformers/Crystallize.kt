package net.imageveil.lib.transformers

import net.imageveil.lib.domain.Area
import org.imgscalr.Scalr
import org.slf4j.LoggerFactory
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.floor
import kotlin.math.min
import kotlin.random.Random

class Crystallize(private val areas: List<Area>, private val cells: Int = 60) : Transformer {
    private val logger = LoggerFactory.getLogger("Transformers - Crystallize")
    private val r = Random

    override fun transform(image: BufferedImage): BufferedImage {
        val crystals = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)

        // Prepare
        val cellWidth = image.width / cells
        val cellHeight = image.height / cells
        val rng = Random(System.currentTimeMillis())

        val xs = IntArray(cells * cells)
        val ys = IntArray(cells * cells)

        // Generate cells
        for (y in 0 until cells) {
            for (x in 0 until cells) {
                xs[y * cells + x] = x * cellWidth + rng.nextInt(cellWidth)
                ys[y * cells + x] = y * cellHeight + rng.nextInt(cellHeight)
            }
        }

        // Draw
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val currentCellX = min(floor(x.toDouble() / cellWidth.toDouble()).toInt(), cells - 1)
                val currentCellY = min(floor(y.toDouble() / cellHeight.toDouble()).toInt(), cells - 1)

                val nearestPoint = currentCellY * cells + currentCellX

                val displacements = intArrayOf(
                    nearestPoint - cells - 1,
                    nearestPoint - cells,
                    nearestPoint - cells + 1,
                    nearestPoint - 1,
                    nearestPoint,
                    nearestPoint + 1,
                    nearestPoint + cells - 1,
                    nearestPoint + cells,
                    nearestPoint + cells + 1
                )

                val adjacentPoints = arrayListOf<Int>()
                displacements.forEach {
                    if (it >= 0 && it <= xs.lastIndex) { adjacentPoints.add(it) }
                }

                var n = 0
                adjacentPoints.forEach {
                    if (distSq(xs[it], x, ys[it], y) < distSq(xs[n], x, ys[n], y)) n = it
                }

                crystals.setRGB(x, y, image.getRGB(xs[n], ys[n]))
            }
        }

        // Apply to areas of original
        val g2d = image.graphics as Graphics2D
        areas.forEach { area ->
            if (area.width > 0 && area.height > 0) {
                var x = area.x
                var y = area.y
                var width = area.width
                var height = area.height

                if (x + width > crystals.width) width = crystals.width - x
                if (y + height > crystals.height) height = crystals.height - y
                if (x < 0) { width += x; x = 0 }
                if (y < 0) { height += y; y = 0 }

                try {
                    val areaImage = crystals.getSubimage(x, y, width, height)
                    g2d.drawImage(areaImage, x, y, null)
                } catch (e: Exception) {
                    logger.error("""${"\n"}
                        image          : w ${image.width}, h ${image.height}
                        squareImage    : w ${crystals.width}, h ${crystals.height}
                        correction     : x $x, y $y, w $width, h $height
                        area (original): x ${area.x}, y ${area.y}, w ${area.width}, h ${area.height}
                    """.trimIndent())
                }
            }
        }
        g2d.dispose()

        return image
    }

    private fun distSq(x1: Int, x2: Int, y1: Int, y2: Int): Int {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)
    }
}