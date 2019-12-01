package wav.boop.waveform

import kotlin.math.sin
import kotlin.math.pow
import wav.boop.DEFAULT_SAMPLE_RATE_IN_SECONDS

class TriangleEngine(var numVoices: Int = 3) : WaveformEngine {
//    sin((2n-1)*x)*(-1)^n/(2n-1)^2
//    n = num harmonic OR harmonic * base freq
//    x = time
    private fun baseFunction (harmonic: Int, time: Int): Double {
        return (sin((2 * harmonic - 1) * time.toDouble()) * (-1.0).pow(harmonic) / (2 * harmonic.toDouble() - 1).pow(2))
    }

    override fun getWaveform(frequency: Double): () -> DoubleArray {
        return fun (): DoubleArray {
            val duration = DEFAULT_SAMPLE_RATE_IN_SECONDS / frequency
            val mSound = DoubleArray(duration.toInt())
            for (i in mSound.indices) {
                mSound[i] = 0.0
                for (harmonic in 1.rangeTo(numVoices)) {
                    mSound[i] += baseFunction(harmonic, i)
                }
            }
            return mSound
        }
    }
}