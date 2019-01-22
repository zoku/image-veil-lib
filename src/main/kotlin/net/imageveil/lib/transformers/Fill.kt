package net.imageveil.lib.transformers

import net.imageveil.lib.domain.Area
import net.imageveil.lib.Config
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

class Fill(private val areas: List<Area>, private val scaleX: Float = 1f, private val scaleY: Float = 1f) : Transformer {
    private val logger = LoggerFactory.getLogger("Transformers - Fill")

    override fun transform(image: BufferedImage): BufferedImage {
        val g2d = image.graphics as Graphics2D
        g2d.color = Color(Config.transformers_masks_fill_color)

        areas.forEach { area ->
            var x = (area.x * scaleX).roundToInt()
            var y = (area.y * scaleY).roundToInt()
            var width = (area.width * scaleX).roundToInt()
            var height = (area.height * scaleY).roundToInt()

            if (x + width > image.width) width = x + width - image.width
            if (y + height > image.height) height = y + height - image.height
            if (x < 0) { width += x; x = 0 }
            if (y < 0) { height += y; y = 0 }

            try {
                g2d.fillRect(x, y, width, height)
            } catch (e: Exception) {
                logger.error("""
                        image          : w ${image.width}, h ${image.height}, sX $scaleX, sY $scaleY
                        correction     : x $x, y $y, w $width, h $height
                        area (original): x ${area.x}, y ${area.y}, w ${area.width}, h ${area.height}
                        area (scaled)  : x ${(area.x * scaleX).roundToInt()}, y ${(area.y * scaleY).roundToInt()}, w ${(area.width * scaleX).roundToInt()}, h ${(area.height * scaleY).roundToInt()}
                    """.trimIndent())
            }
        }
        g2d.dispose()
        return image
    }
}