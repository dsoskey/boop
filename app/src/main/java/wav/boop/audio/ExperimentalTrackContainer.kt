package wav.boop.audio

import android.media.AudioTrack

class ExperimentalTrackContainer(val track: ExperimentalThreadedTrack) {
    var id: Int? = null
        private set
    var waveform: (() -> ShortArray)? = null
        private set

    // We always want them together, so they are set all or nothing
    fun setInfo(id: Int, waveform: () -> ShortArray) {
        this.id = id
        this.waveform = waveform
    }

    fun clearInfo() {
        id = null
        waveform = null
    }
}