package wav.boop.file

import android.content.Context
import kotlinx.serialization.json.Json
import wav.boop.preset.Preset
import wav.boop.sample.Sample

fun buildPresetLoader(context: Context, json: Json): DefaultSerialLoader<Preset> {
    return DefaultSerialLoader(
        "presets",
        context,
        { json.stringify(Preset.serializer(), it) },
        { json.parse(Preset.serializer(), it) }
    )
}

fun buildSampleLoader(context: Context, json: Json): DefaultSerialLoader<Sample> {
    return DefaultSerialLoader(
        "samples",
        context,
        { json.stringify(Sample.serializer(), it) },
        { json.parse(Sample.serializer(), it) }
    )
}