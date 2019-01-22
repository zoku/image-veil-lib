package net.imageveil.lib.transformers

import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifIFD0Directory
import org.imgscalr.Scalr
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage

/**
 * Created by Manuel Helbing on 2018-12-16.
 */
class Rotate(private val metaData: Metadata) : Transformer {
    private val logger = LoggerFactory.getLogger("Transformers - Rotate")

    override fun transform(image: BufferedImage): BufferedImage {
        val exifDir = metaData.getFirstDirectoryOfType(ExifIFD0Directory::class.java)

        if (exifDir != null && exifDir.containsTag(0x112)) {
            val orientation = exifDir.getInt(0x112)

            when (orientation) {
                ImageOrientations.LANDSCAPE_0_O -> return image
                ImageOrientations.LANDSCAPE_0_M -> return image.flip()
                ImageOrientations.LANDSCAPE_90_O -> return image.rotate90()
                ImageOrientations.LANDSCAPE_90_M -> return image.rotate90().flip()
                ImageOrientations.LANDSCAPE_180_O -> return image.rotate180()
                ImageOrientations.LANDSCAPE_180_M -> return image.rotate180().flip()
                ImageOrientations.LANDSCAPE_270_O -> return image.rotate270()
                ImageOrientations.LANDSCAPE_270_M -> return image.rotate270().flip()
                else -> logger.warn("Unknown image orientation: $orientation")
            }
        }
        return image
    }

    private object ImageOrientations {
        const val LANDSCAPE_0_O = 1
        const val LANDSCAPE_0_M = 2

        const val LANDSCAPE_90_O = 6
        const val LANDSCAPE_90_M = 5

        const val LANDSCAPE_180_O = 3
        const val LANDSCAPE_180_M = 4

        const val LANDSCAPE_270_O = 8
        const val LANDSCAPE_270_M = 7
    }

    private fun BufferedImage.flip(): BufferedImage {
        return Scalr.rotate(this, Scalr.Rotation.FLIP_HORZ)
    }

    private fun BufferedImage.rotate90(): BufferedImage {
        return Scalr.rotate(this, Scalr.Rotation.CW_90)
    }

    private fun BufferedImage.rotate180(): BufferedImage {
        return Scalr.rotate(this, Scalr.Rotation.CW_180)
    }

    private fun BufferedImage.rotate270(): BufferedImage {
        return Scalr.rotate(this, Scalr.Rotation.CW_270)
    }
}