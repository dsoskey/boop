package wav.boop.synth

import wav.boop.audio.AudioEngine
import wav.boop.waveform.WaveformEngine

interface Synthesizer {
    var waveformEngine: WaveformEngine
    val audioEngine: AudioEngine

    fun play(frequency: Double, viewId: Int)
    fun stop(viewId: Int)
}
