package net.imageveil.lib.transformers

import net.imageveil.lib.domain.Area
import net.imageveil.lib.Config
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

class SquareMosaic(private val areas: List<Area>, private val scaleX: Float = 1f, private val scaleY: Float = 1f) : Transformer {
    private val logger = LoggerFactory.getLogger("Transformers - SquareMosaic")

    override fun transform(image: BufferedImage): BufferedImage {
        val squareLength = ((if (image.width > image.height) image.width else image.height) * Config.transformers_masks_squareMosaic_squareSize).roundToInt()

        val squaredImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        val squaredG2D = squaredImage.graphics as Graphics2D
        for (sqX in 0 until squaredImage.width - squareLength step squareLength) {
            for (sqY in 0 until squaredImage.height - squareLength step squareLength) {
                var r = 0
                var g = 0
                var b = 0
                var pixels = 0

                for(imX in sqX until sqX + squareLength - 1) {
                    for(imY in sqY until sqY + squareLength - 1) {
                        val currentPixel = Color(image.getRGB(imX, imY))
                        r += currentPixel.red
                        g += currentPixel.green
                        b += currentPixel.blue
                        pixels++
                    }
                }

                val medianR = r / pixels
                val medianG = g / pixels
                val medianB = b / pixels

                squaredG2D.color = Color(medianR, medianG, medianB)
                squaredG2D.fillRect(sqX, sqY, squareLength, squareLength)
            }
        }
        squaredG2D.dispose()

        val g2d = image.graphics as Graphics2D
        areas.forEach { area ->
            if (area.width > 0 && area.height > 0) {
                var x = (area.x * scaleX).roundToInt()
                var y = (area.y * scaleY).roundToInt()
                var width = (area.width * scaleX).roundToInt()
                var height = (area.height * scaleY).roundToInt()

                if (x + width > squaredImage.width) width = squaredImage.width - x
                if (y + height > squaredImage.height) height = squaredImage.height - y
                if (x < 0) { width += x; x = 0 }
                if (y < 0) { height += y; y = 0 }

                try {
                    val areaImage = squaredImage.getSubimage(x, y, width, height)
                    g2d.drawImage(areaImage, x, y, null)
                } catch (e: Exception) {
                    logger.error("""${"\n"}
                        scale          : x $scaleX, y $scaleY
                        image          : w ${image.width}, h ${image.height}
                        squareImage    : w ${squaredImage.width}, h ${squaredImage.height}
                        correction     : x $x, y $y, w $width, h $height
                        area (original): x ${area.x}, y ${area.y}, w ${area.width}, h ${area.height}
                        area (scaled)  : x ${(area.x * scaleX).roundToInt()}, y ${(area.y * scaleY).roundToInt()}, w ${(area.width * scaleX).roundToInt()}, h ${(area.height * scaleY).roundToInt()}, maxX ${(area.width * scaleX + area.x * scaleX).roundToInt()}, maxY ${(area.height * scaleY + area.y * scaleY).roundToInt()}
                    """.trimIndent())
                }
            }
        }
        g2d.dispose()

        return image
    }
}