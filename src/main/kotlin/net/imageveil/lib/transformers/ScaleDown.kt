package net.imageveil.lib.transformers

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

class ScaleDown(private val maxImageEdgeSize: Int = 1280) : Transformer {
    override fun transform(image: BufferedImage): BufferedImage {
        val scale = if (image.width > image.height) {
            maxImageEdgeSize.toDouble() / image.width
        } else {
            maxImageEdgeSize.toDouble() / image.height
        }

        val scaledImage = BufferedImage((image.width * scale).roundToInt(), (image.height * scale).roundToInt(), BufferedImage.TYPE_INT_RGB)

        val g2d = scaledImage.graphics as Graphics2D

        g2d.drawImage(image, 0, 0, scaledImage.width, scaledImage.height, 0, 0, image.width, image.height, null)

        g2d.dispose()

        return scaledImage
    }
}