package wav.boop.waveform

import kotlin.math.cos

class SineEngine : WaveformEngine {
    override fun getWaveform(frequency: Double): () -> DoubleArray {
        return fun(): DoubleArray {
            val duration = DEFAULT_SAMPLE_RATE_IN_SECONDS / frequency
            val mSound = DoubleArray(duration.toInt())
            for (i in mSound.indices) {
//                mSound[i] = sin(2.0 * Math.PI * i.toDouble() / (DEFAULT_SAMPLE_RATE_IN_SECONDS / frequency))// TODO: Read playback speed?
                mSound[i] = cos(2.0 * frequency * Math.PI * i.toDouble() / (DEFAULT_SAMPLE_RATE_IN_SECONDS))// TODO: Read playback speed?
            }
            return mSound
        }
    }
}