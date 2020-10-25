package wav.boop.sample

import android.content.ContentValues
import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.lang.Exception
import java.lang.NumberFormatException

fun wavToPcm(applicationContext: Context, fileName: String): FloatArray {
    val finalData = mutableListOf<Float>()

    val resolver = applicationContext.contentResolver

    val audioCollection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

    val fileDetails = ContentValues().apply {
        put(MediaStore.Audio.Media.MIME_TYPE, "audio/wav")
        put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Audio.Media.IS_PENDING, 1)
    }
    val uri: Uri = resolver.insert(audioCollection, fileDetails)!!

    resolver.openInputStream(uri).use { output ->
        if (output != null) {
            try {
                val bytes = output.readBytes()
                val metadata = bytes.copyOfRange(0, WAV_METADATA_CHUNK_SIZE)
                val data = bytes.copyOfRange(WAV_METADATA_CHUNK_SIZE, bytes.size)

                // validate metadata
                assert(metadata.copyOfRange(0, 4).contentEquals(RIFF_BYTES)) {"Not a RIFF" }
                // Ignore [4, 8], full size
                assert(metadata.copyOfRange(8, 12).contentEquals(WAVE_BYTES)) { "Not a WAVE file" }
                assert(metadata.copyOfRange(12, 16).contentEquals(FMT_BYTES))
                // Ignore [16, 20], size of subchunk 1
                assert(readShort(metadata.copyOfRange(20, 22)) == 1.toShort()) { "NOT PCM" }
                val numChannels = readShort(metadata.copyOfRange(22, 24))
                val sampleRate = readInt(metadata.copyOfRange(24, 28))
                val byteRate  = readShort(metadata.copyOfRange(28, 32))
                val blockAlign = readShort(metadata.copyOfRange(32, 34))
                val bitsPerSample = readShort(metadata.copyOfRange(34, 36))
                // Ignore [32, 36],
                assert(metadata.copyOfRange(36, 40).contentEquals(DATA_BYTES)) { "NOT DATA" }
//                assert(readInt(metadata.copyOfRange(40, 44)))
                var bytesToProcess = mutableListOf<Byte>()
                for (index in data.indices) {
                    bytesToProcess.add(data[index])
                    if (bytesToProcess.size == 2) {
                        finalData.add(readShort(bytesToProcess.toByteArray()).toFloat() / 32768)
                        bytesToProcess = mutableListOf()
                    }
                }
            } catch (e: Exception) {
                Log.e("TAG", "Failed to read file")
            }
        }
    }
    return finalData.toFloatArray()
}

fun readShort(byteArray: ByteArray): Short {
    return if (byteArray.size == 2) {
        (byteArray[1].toInt() shl 8 + byteArray[0].toShort()).toShort()
    } else {
        throw NumberFormatException("This needs to be length 2. Actual length ${byteArray.size}")
    }
}

fun readInt(byteArray: ByteArray): Int {
    return if (byteArray.size == 4) {
        (byteArray[3].toInt() shl 24) +
        (byteArray[2].toInt() shl 16) +
        (byteArray[1].toInt() shl 8) +
         byteArray[0].toInt()
    } else {
        throw NumberFormatException("This needs to be length 4. Actual length ${byteArray.size}")
    }
}