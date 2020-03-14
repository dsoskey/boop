package wav.boop.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack

class SubscriberTrack(
    private val refreshRate: Long = 1000L,
    private val trackBuilder: AudioTrack.Builder = ThreadedTrack.DEFAULT_TRACK_BUILDER
): Runnable {
    // must be read by main thread un-cached
    @Volatile
    var isPlaying: Boolean = false
        private set
    private var track = trackBuilder.build()

    override fun run() {
        track = trackBuilder.build()
        track.play()
        isPlaying = true
        while (isPlaying) {
//            Thread.sleep(1L)
            print('e')
        }
        track.stop()
        track.release()
    }

    private fun toShortArray(waveform: DoubleArray): ShortArray {
        return waveform
            .map { doubleVal ->
                (doubleVal * java.lang.Short.MAX_VALUE).toShort()
            }.toShortArray()
    }

    fun queueWave(): (DoubleArray) -> Unit {
        return fun(waveform: DoubleArray) {
            track.write(toShortArray(waveform), 0, waveform.size, AudioTrack.WRITE_NON_BLOCKING)
        }
    }

    fun start() {
        isPlaying = true
    }

    fun stop() {
        isPlaying = false
    }

    companion object {
        val DEFAULT_TRACK_BUILDER: AudioTrack.Builder = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(44100)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(
                AudioTrack.getMinBufferSize(
                    44100,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_8BIT
                )
            )
            .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
            .setTransferMode(AudioTrack.MODE_STREAM)
    }
}