package wav.boop.preset

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecodingException
import java.io.FileNotFoundException

interface PresetLoader {
    fun listPresets(): List<String>
    fun searchPresets(params: Any /* TBD */): List<Preset>
    fun savePreset(preset: Preset)
    fun getPreset(fileName: String): Preset?
}

class DefaultPresetLoader(
    private val context: Context,
    private val serializer: Json
): PresetLoader {

    private val fileMap = mutableMapOf<String, Preset>()
    override fun listPresets(): List<String> = fileMap.keys.toList()
    init {
        context.fileList().forEach { fileName -> getPreset(fileName) }
    }

    override fun getPreset(fileName: String): Preset? {
        if (fileMap.containsKey(fileName)) {
            return fileMap[fileName]!!
        }
        return try {
            val iStream = context.openFileInput(fileName)
            val fileContent: String = iStream.bufferedReader().useLines { lines ->
                lines.fold("") { some, text ->
                    "$some\n$text"
                }
            }
            val preset = serializer.parse(Preset.serializer(), fileContent)
            fileMap[fileName] = preset
            iStream.close()
            preset
        } catch (e: FileNotFoundException) {
            null
        } catch (e: JsonDecodingException) {
            null
        }
    }

    override fun savePreset(preset: Preset) {
        try {
            val fileContents = serializer.stringify(Preset.serializer(), preset)
            val oStream = context.openFileOutput(preset.fileName, 0)
            oStream.use {
                it.write(fileContents.toByteArray())
            }
            oStream.flush()
            oStream.close()
        } catch (e: FileNotFoundException) {
            println("Oops ouch owie")
        }
    }

    override fun searchPresets(params: Any): List<Preset> {
        TODO("Not yet implemented")
    }
}