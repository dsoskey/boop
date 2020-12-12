package wav.boop.file

import android.content.Context
import kotlinx.serialization.json.JsonDecodingException
import java.io.File
import java.io.FileNotFoundException

interface SerialLoader<T> {
    fun list(): List<String>
    fun search(params: Any /* TBD */): List<T>
    fun save(fileName: String, data: T)
    fun get(fileName: String): T?
}

class DefaultSerialLoader<T>(
    subdirPath: String,
    context: Context,
    private val stringify: (T) -> String,
    private val parse: (String) -> T
): SerialLoader<T> {
    private val fileMap = mutableMapOf<String, T>()
    private val subDir = File(context.filesDir, subdirPath)
    override fun list(): List<String> = fileMap.keys.toList()
    init {
        subDir.mkdir()
        subDir.list()?.forEach { get(it) }
    }

    override fun get(fileName: String): T? {
        if (fileMap.containsKey(fileName)) {
            return fileMap[fileName]!!
        }
        return try {
            val iStream = File(subDir, fileName).inputStream()
            val fileContent: String = iStream.bufferedReader().useLines { lines ->
                lines.fold("") { some, text ->
                    "$some\n$text"
                }
            }
            val data = parse(fileContent)
            fileMap[fileName] = data
            iStream.close()
            data
        } catch (e: FileNotFoundException) {
            null
        } catch (e: JsonDecodingException) {
            null
        }
    }

    override fun save(fileName: String, data: T) {
        try {
            val fileContents = stringify(data)
            val oStream = File(subDir, fileName).outputStream()
            oStream.use {
                it.write(fileContents.toByteArray())
            }
            oStream.flush()
            oStream.close()
        } catch (e: FileNotFoundException) {
            println("Oops ouch owie")
        }
    }

    override fun search(params: Any): List<T> {
        TODO("Not yet implemented")
    }
}