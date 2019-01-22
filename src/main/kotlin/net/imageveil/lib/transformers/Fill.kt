package net.imageveil.lib.transformers

import net.imageveil.lib.domain.Area
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

class Fill(private val areas: List<Area>, private val color: Int) : Transformer {
    private val logger = LoggerFactory.getLogger("Transformers - Fill")

    override fun transform(image: BufferedImage): BufferedImage {
        val g2d = image.graphics as Graphics2D
        g2d.color = Color(color)

        areas.forEach { area ->
            var x = area.x
            var y = area.y
            var width = area.width
            var height = area.height

            if (x + width > image.width) width = x + width - image.width
            if (y + height > image.height) height = y + height - image.height
            if (x < 0) { width += x; x = 0 }
            if (y < 0) { height += y; y = 0 }

            try {
                g2d.fillRect(x, y, width, height)
            } catch (e: Exception) {
                logger.error("""
                        image          : w ${image.width}, h ${image.height}
                        correction     : x $x, y $y, w $width, h $height
                        area (original): x ${area.x}, y ${area.y}, w ${area.width}, h ${area.height}
                    """.trimIndent())
            }
        }
        g2d.dispose()
        return image
    }
}