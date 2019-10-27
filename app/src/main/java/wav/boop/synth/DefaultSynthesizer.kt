package wav.boop.synth

import wav.boop.audio.AudioEngine
import wav.boop.waveform.*

class DefaultSynthesizer(override var waveformEngine: WaveformEngine, override var audioEngine: AudioEngine) :
    Synthesizer {

    override fun play(frequency: Double, viewId: Int) {
        audioEngine.play(toShortArray(frequency), viewId)
    }

    override fun stop(viewId: Int) {
        audioEngine.stop(viewId)
    }

    private fun toShortArray(frequency: Double): () -> ShortArray {
        return fun(): ShortArray{
            val wave: DoubleArray = waveformEngine.getWaveform(frequency)()
            val shortWave = ShortArray(wave.size)
            for (i in wave.indices) {
                shortWave[i] = (wave[i] * java.lang.Short.MAX_VALUE).toShort()
            }
            return shortWave
        }
    }

    companion object {
        var DEFAULT_SYNTHESIZER = DefaultSynthesizer(SawEngine(100), AudioEngine(16))
    }
}
