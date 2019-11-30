package wav.boop.audio

import android.media.AudioTrack

class ExperimentalThreadedTrack(var waveform: (() -> ShortArray)?, private val trackBuilder: AudioTrack.Builder) : Runnable {

    // must be read by main thread un-cached
    @Volatile
    var trackIsRegistered: Boolean = false
        private set

    override fun run() {
        val track = trackBuilder.build()
        track.play()
        trackIsRegistered = true
        while (trackIsRegistered) {
            if (waveform == null) {
                track.write(ShortArray(1, { 0 }), 0, 1)
                println("Emit null!")
            } else {
                val wave = waveform?.invoke()
                track.write(wave!!, 0, wave.size)
                println("Emit wave!")
            }
        }
        track.stop()
        track.release()
    }

    fun stop() {
        trackIsRegistered = false
    }
}