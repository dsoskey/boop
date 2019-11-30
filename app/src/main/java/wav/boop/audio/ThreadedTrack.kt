package wav.boop.audio

import android.media.AudioTrack

class ThreadedTrack(private val waveform: () -> ShortArray, private val trackBuilder: AudioTrack.Builder) : Runnable {

    // must be read by main thread un-cached
    @Volatile
    var isPlaying: Boolean = false
        private set

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
}
