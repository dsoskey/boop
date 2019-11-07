package wav.boop.waveform

import kotlin.math.cos
import wav.boop.DEFAULT_SAMPLE_RATE_IN_SECONDS

class SineEngine : WaveformEngine {
    override fun getWaveform(frequency: Double): () -> DoubleArray {
        return fun(): DoubleArray {
            val duration = DEFAULT_SAMPLE_RATE_IN_SECONDS / frequency
            val mSound = DoubleArray(duration.toInt())
            for (i in mSound.indices) {
                // TODO: Read playback speed?
                mSound[i] = cos(2.0 * frequency * Math.PI * i.toDouble() / (DEFAULT_SAMPLE_RATE_IN_SECONDS))
            }
            return mSound
        }
    }
}