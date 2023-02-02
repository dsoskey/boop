package wav.boop.sample

import android.content.ContentValues
import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.OutputStream

/**
 * Saves a list of floats with domain [-1,1] to a wav file.
 *
 * Uses the wav spec as defined here:
 * - http://soundfile.sapp.org/doc/WaveFormat/
 * - http://www-mmsp.ece.mcgill.ca/Documents/AudioFormats/WAVE/WAVE.html
 *   - An html only copy can be found in the docs dir in a pinch
 */
fun pcmToWavFile(applicationContext: Context, fileName: String, data: FloatArray) {
    val resolver = applicationContext.contentResolver

    val audioCollection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

    val fileDetails = ContentValues().apply {
        put(MediaStore.Audio.Media.MIME_TYPE, "audio/wav")
        put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Audio.Media.IS_PENDING, 1)
    }
    val uri: Uri = resolver.insert(audioCollection, fileDetails)!!

    resolver.openOutputStream(uri, "w").use { output ->
        if (output != null) {
            try {
                val audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val sampleRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE).toInt()
                val charset = Charsets.UTF_8
                val numChannels = 1
                val bitsPerSample = 16
                val blockAlign = numChannels * bitsPerSample / 8
                val byteRate = sampleRate * blockAlign
                output.write(RIFF_BYTES) // chunk id [0,4]
                writeInt(output, WAV_METADATA_CHUNK_SIZE + data.size) // size of wav file [4,8]
                output.write(WAVE_BYTES) // format [8,12]

                output.write(FMT_BYTES) // subchunk 1 id [12,16]
                writeInt(output, 16) // size of subchunk 1 for PCM [16,20]
                writeShort(output, 1) // PCM = 1, linear quantization [20,22]
                writeShort(output, numChannels) // Num channels (1 for mono, 2 for stereo) [22,24]
                writeInt(output, sampleRate) // Sample rate [24, 28]
                writeInt(output, byteRate) // Byte rate = SampleRate * NumChannels * BitsPerSample / 8 [28, 32]
                writeShort(output, blockAlign) // Block align = NumChannels * BitsPerSample / 8
                writeShort(output, bitsPerSample) // Bits Per Sample

                output.write("data".toByteArray(charset)) // subchunk 2 id
                writeInt(output, data.size) // size of subchunk 2
                data.forEach {
                    val convFloat: Float = it * 32768
                    writeShort(output,
                        // TODO: Dither
                        when {
                            convFloat > 32767 -> 32767
                            convFloat < -32768 -> -32768
                            else -> convFloat.toInt()
                        }
                    )
                }
            } catch (e: Error) {
                Log.e("TAG", "Failed to write file")
            }
        } else {
            Log.e("TAG", "Not here!")
        }
    }
}

fun writeInt(output: OutputStream, int: Int) {
    output.write(int shr 0)
    output.write(int shr 8)
    output.write(int shr 16)
    output.write(int shr 24)
}

fun writeShort(output: OutputStream, short: Int) {
    output.write(short shr 0)
    output.write(short shr 8)
}
