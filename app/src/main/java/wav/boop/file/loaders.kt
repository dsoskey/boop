package wav.boop.file

import android.content.Context
import kotlinx.serialization.json.Json
import wav.boop.preset.Preset
import wav.boop.sample.Sample

fun buildPresetLoader(context: Context): DefaultSerialLoader<Preset> {
    return DefaultSerialLoader(
        "presets",
        context,
        { Json.encodeToString(Preset.serializer(), it) },
        { Json.decodeFromString(Preset.serializer(), it) }
    )
}

fun buildSampleLoader(context: Context): DefaultSerialLoader<Sample> {
    return DefaultSerialLoader(
        "samples",
        context,
        { Json.encodeToString(Sample.serializer(), it) },
        { Json.decodeFromString(Sample.serializer(), it) }
    )
}