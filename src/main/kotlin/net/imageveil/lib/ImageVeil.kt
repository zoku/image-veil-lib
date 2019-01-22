package net.imageveil.lib

import com.drew.imaging.ImageMetadataReader
import net.imageveil.lib.domain.Area
import net.imageveil.lib.transformers.*
import net.imageveil.lib.transformers.Transformer
import java.awt.image.BufferedImage
import java.io.*
import javax.imageio.ImageIO

class ImageVeil {
    private var image: BufferedImage

    constructor(imageFile: File) {
        image = ImageIO.read(imageFile) ?: throw Exception("Image needs to be valid.")
    }

    constructor(imageStream: InputStream) {
        image = ImageIO.read(imageStream) ?: throw Exception("Image needs to be valid.")
    }

    private val transformers = arrayListOf<Transformer>()

    fun addTransformerToQueue(transformer: Transformer) {
        transformers.add(transformer)
    }

    fun run(): BufferedImage {
        transformers.forEach { transformer ->
            image = transformer.transform(image)
        }

        return image
    }
}

fun main(args: Array<String>) {
    if (args.size < 2) {
        throw Exception("Arguments must be input file's and output file's paths.")
    }

    val imageFile = File(args[0])
    val imageMetaData = ImageMetadataReader.readMetadata(imageFile)

    val areas = arrayListOf(Area(x = 250, y = 250, width = 250, height = 250))

    val iv = ImageVeil(imageFile)

    iv.addTransformerToQueue(Rotate(imageMetaData))
    iv.addTransformerToQueue(Noise(percentageToAdd = .5, intensity = 30))
    iv.addTransformerToQueue(SquareMosaic(areas))
    iv.addTransformerToQueue(ScaleDown())

    val anonymisedImage = iv.run()

    ImageIO.write(anonymisedImage, "jpeg", File(args[1]))
}