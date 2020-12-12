package wav.boop.sample

import kotlin.math.ceil
import kotlin.math.pow

/**
 * Generates a list of points from which to render a sample.
 * These points will fill as much space within canvas as possible.
 * @param rawData - PCM sample to generate. Assumes that sample data is normalized to [-1, 1].
 * @param width - width of canvas/space on which sample is to be drawn.
 * @param height - height of canvas/space on which sample is to be drawn.
 * @param sampleRate - how often to take an audio sample to use for the visual sample.
 * By default it calculates the appropriate step value to scale inversely to the size of the sample.
 */
fun renderedSample(rawData: FloatArray, width: Int, height: Int, sampleRate: Int? = null): List<Pair<Float, Float>> {
    val firstSilence = rawData.indexOfLast { it != 0f } + 1
    val withoutSilence = rawData.sliceArray(0 until firstSilence)
    val xStep: Float = width.toFloat() / withoutSilence.size
    val yMid: Float = height / 2f
    // NOTE: if i want to add zooming in the future, sample rate counting will need to be extracted from this function and calculated during render process
    val mSampleRate: Int = sampleRate ?: linearSampleRateCurve(withoutSilence.size)

    // X is evenly distributed among [xMin, xMax]
    val normedX: FloatArray = withoutSilence
        .mapIndexed { frame, _ -> frame * xStep } // This one needs the map first because it uses frame to determine step
        .filterIndexed { frame, _ -> frame % mSampleRate == 0 }
        .toFloatArray()
    // Y is already normalized [-1, 1], so it can be transformed to represent distance away from the midpoint of the canvas
    val normedY: FloatArray = withoutSilence
        .filterIndexed { frame, _ -> frame % mSampleRate == 0 }
        .map { waveVal -> yMid + yMid * waveVal }
        .toFloatArray()
    return normedX zip normedY
}

// See the visual representation of these curves here: https://www.desmos.com/calculator/fe4x1usvfa
fun linearSampleRateCurve(size: Int): Int {
    return ceil(size / RENDER_STEP_FACTOR).toInt()
}

fun aggressiveSampleRateCurve(size: Int): Int {
    return ceil(MAX_SAMPLE_RATE - ((size - MAX_SAMPLE_SIZE) / STEP_CURVE_DENOMINATOR).pow(2.0)).toInt()
}

fun laxSampleRateCurve(size: Int): Int {
    return ceil((size / STEP_CURVE_DENOMINATOR).pow(2.0)).toInt()
}