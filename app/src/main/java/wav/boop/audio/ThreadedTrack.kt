package wav.boop.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack

class ThreadedTrack(
    private val waveform: () -> ShortArray,
    private val trackBuilder: AudioTrack.Builder = DEFAULT_TRACK_BUILDER
) : Runnable {
    // must be read by main thread un-cached
    @Volatile
    var isPlaying: Boolean = false
        private set

    init {
        isPlaying = false
    }

    override fun run() {
        val track = trackBuilder.build()
        track.play()
        isPlaying = true
        while (isPlaying) {
            val wave = waveform()
            track.write(wave, 0, wave.size)
        }
        track.stop()
        track.release()
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
