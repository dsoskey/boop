package wav.boop.sample

import kotlin.math.pow

val DEFAULT_CHARSET = Charsets.UTF_8

val RIFF_BYTES = "RIFF".toByteArray(DEFAULT_CHARSET)

val WAVE_BYTES = "WAVE".toByteArray(DEFAULT_CHARSET)

val FMT_BYTES = "fmt ".toByteArray(DEFAULT_CHARSET)

val DATA_BYTES = "data".toByteArray(DEFAULT_CHARSET)

const val WAV_METADATA_CHUNK_SIZE = 44 // 36 + 8

const val MAX_SAMPLE_SIZE = 480_000 // 10s sample @ 48 kHz

const val MAX_SAMPLE_RATE = 64.0

const val RENDER_STEP_FACTOR: Double = MAX_SAMPLE_SIZE / MAX_SAMPLE_RATE

val STEP_CURVE_DENOMINATOR = MAX_SAMPLE_SIZE / MAX_SAMPLE_RATE.pow(0.5)