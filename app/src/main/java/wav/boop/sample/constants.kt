package wav.boop.sample

val DEFAULT_CHARSET = Charsets.UTF_8

val RIFF_BYTES = "RIFF".toByteArray(DEFAULT_CHARSET)

val WAVE_BYTES = "WAVE".toByteArray(DEFAULT_CHARSET)

val FMT_BYTES = "fmt ".toByteArray(DEFAULT_CHARSET)

val DATA_BYTES = "data".toByteArray(DEFAULT_CHARSET)

const val WAV_METADATA_CHUNK_SIZE = 44 // 36 + 8