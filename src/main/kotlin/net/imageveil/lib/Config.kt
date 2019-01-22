package net.imageveil.lib

import java.util.*

object Config {
    private val config = Properties()

    val transformers_scaleDown_maxImageEdgeSize: Int

    val transformers_noise_percentageToAdd: Double
    val transformers_noise_intensityOfNoise: Int

    val transformers_masks_squareMosaic_squareSize: Double

    val transformers_masks_fill_color: Int

    init {
        config.load(Config::class.java.getResourceAsStream("/config.properties"))

        transformers_scaleDown_maxImageEdgeSize = config["transformers.scaleDown.maxImageEdgeSize"].toString().toInt()

        transformers_noise_percentageToAdd = config["transformers.noise.percentageToAdd"].toString().toDouble()
        transformers_noise_intensityOfNoise = config["transformers.noise.intensityOfNoise"].toString().toInt()

        transformers_masks_squareMosaic_squareSize = config["transformers.masks.squareMosaic.squareSize"].toString().toDouble()

        transformers_masks_fill_color = Integer.parseInt(config["transformers.masks.fill.color"].toString(), 16)
    }
}