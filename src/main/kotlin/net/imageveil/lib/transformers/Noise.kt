package net.imageveil.lib.transformers

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

class Noise(private val percentageToAdd: Double, private val intensity: Int) : Transformer {
    override fun transform(image: BufferedImage): BufferedImage {
        val pixelCount = image.width * image.height
        val randomiseCount = (pixelCount * percentageToAdd).roundToInt()

        val allPixels = arrayListOf<Pixel>()
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                allPixels.add(Pixel(x, y))
            }
        }
        allPixels.shuffle()

        val randomPixels = allPixels.subList(0, randomiseCount)

        randomPixels.forEach { pixel ->
            val originalColor = Color(image.getRGB(pixel.x, pixel.y))
            val channel = arrayOf("R", "G", "B").random()

            val originalChannelColor = when (channel) {
                "R" -> originalColor.red
                "G" -> originalColor.green
                "B" -> originalColor.blue
                else -> originalColor.red
            }

            val newChannelColor = Random.nextInt(
                    from = max(a = originalChannelColor - intensity, b = 0),
                    until = min(a = originalChannelColor + intensity, b = 255)
            )

            val newColor = Color(
                    if (channel == "R") newChannelColor else originalColor.red,
                    if (channel == "G") newChannelColor else originalColor.green,
                    if (channel == "B") newChannelColor else originalColor.blue
            )

            image.setRGB(pixel.x, pixel.y, newColor.rgb)
        }

        return image
    }

    private data class Pixel(
            val x: Int,
            val y: Int
    )
}