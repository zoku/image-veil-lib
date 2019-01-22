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
    if (args.size < 2 || (args.size == 1 && (args[0] == "-h" || args[0] == "--help"))) {
        println("""
            ImageVeil CLI help
            ==================
            Usage: java -jar image-veil-lib.jar IN_FILE OUT_FILE AREA1 AREA2 ...

            Area: [x,y,w,h] e.g. [10,10,250,250]

            At this time only JPEG is officially supported!
        """.trimIndent())
        System.exit(0)
    }

    val areas = arrayListOf<Area>()
    val rawAreas = args.filter { it.startsWith("[") && it.endsWith("]") }
    rawAreas.forEach { rawArea ->
        val editedArea = rawArea.substring(1 until rawArea.length - 1)
        val areaValues = editedArea.split(",")

        areas.add(Area(areaValues[0].trim().toInt(), areaValues[1].trim().toInt(), areaValues[2].trim().toInt(), areaValues[3].trim().toInt()))
    }

    val imageFile = File(args[0])
    val imageMetaData = ImageMetadataReader.readMetadata(imageFile)

    val iv = ImageVeil(imageFile)

    iv.addTransformerToQueue(Rotate(metaData = imageMetaData))
    iv.addTransformerToQueue(Noise(percentageToAdd = .5, intensity = 30))
    iv.addTransformerToQueue(SquareMosaic(areas = areas, squareSize = .02))
    iv.addTransformerToQueue(ScaleDown(maxImageEdgeSize = 1280))

    val anonymisedImage = iv.run()

    ImageIO.write(anonymisedImage, "jpeg", File(args[1]))
}