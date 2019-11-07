package wav.boop.synth

import wav.boop.audio.AudioEngine
import wav.boop.waveform.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSynthesizer @Inject constructor(): Synthesizer {

    override var waveformEngine: WaveformEngine = SineEngine()
    override var audioEngine: AudioEngine = AudioEngine(16)

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
}
