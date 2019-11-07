package wav.boop.waveform

import kotlin.math.cos
import wav.boop.DEFAULT_SAMPLE_RATE_IN_SECONDS

class SawEngine(var numVoices: Int = 4) : WaveformEngine {
    private fun baseSineFunction(frequency: Double, amplitude: Double, time: Int): Double {
        return amplitude * cos(2.0 * frequency * Math.PI * time.toDouble() / (DEFAULT_SAMPLE_RATE_IN_SECONDS))
}

    override fun getWaveform(frequency: Double): () -> DoubleArray {
        return fun (): DoubleArray {
            val duration = DEFAULT_SAMPLE_RATE_IN_SECONDS / frequency
            val mSound = DoubleArray(duration.toInt())
            for (i in mSound.indices) {
                mSound[i] = 0.0
                for (harmonic in 1.rangeTo(numVoices)) {
                    mSound[i] += baseSineFunction(frequency * harmonic, 1.0 / harmonic, i)
                }
            }
            return mSound
        }
    }
}