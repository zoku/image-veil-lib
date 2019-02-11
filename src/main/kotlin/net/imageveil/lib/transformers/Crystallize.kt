package net.imageveil.lib.transformers

import net.imageveil.lib.domain.Area
import org.imgscalr.Scalr
import org.slf4j.LoggerFactory
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.random.Random

class Crystallize(private val areas: List<Area>, private val cells: Int = 60, private val scaleFactor: Int = 3) : Transformer {
    private val logger = LoggerFactory.getLogger("Transformers - Crystallize")
    private val r = Random

    override fun transform(image: BufferedImage): BufferedImage {
        val start = System.currentTimeMillis()

        val scaledSource = Scalr.resize(image, image.width / scaleFactor, image.height / scaleFactor)
        val scaledSourceWidth = scaledSource.width
        val scaledSourceHeight = scaledSource.height
        var crystals = BufferedImage(scaledSourceWidth, scaledSourceHeight, BufferedImage.TYPE_INT_RGB)

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
        logger.debug("Calculate points: " + (System.currentTimeMillis() - start4) / 1000f)

        val start3 = System.currentTimeMillis()
        for (x in 0 until scaledSourceWidth) {
            for (y in 0 until scaledSourceHeight) {
                var n = 0

                val xRange = x - spacingX .. x + spacingX
                val yRange = y - spacingY .. y + spacingY

                val adjacentPoints = points.filter { it.first in xRange && it.second in yRange }

                for (i in 0 until adjacentPoints.size) {
                    if (distSq(adjacentPoints[i].first, x, adjacentPoints[i].second, y) < distSq(adjacentPoints[n].first, x, adjacentPoints[n].second, y)) n = i
                }
                crystals.setRGB(x, y, scaledSource.getRGB(adjacentPoints[n].first, adjacentPoints[n].second))
            }
        }
        logger.debug("Get colours and render: " + (System.currentTimeMillis() - start3) / 1000f)

        crystals = Scalr.resize(crystals, Scalr.Method.ULTRA_QUALITY, image.width, image.height, Scalr.OP_ANTIALIAS)
        logger.debug("Finished ($cells² cells): " + (System.currentTimeMillis() - start) / 1000f)

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