package com.example.boop.waveform

class SquareEngine : WaveformEngine {
    override fun getWaveform(frequency: Double): () -> DoubleArray {
        return fun(): DoubleArray {
            val duration = (DEFAULT_SAMPLE_RATE_IN_SECONDS / frequency)
            val mSound = DoubleArray(duration.toInt())
            for (i in mSound.indices) {
                mSound[i] = 2 * Math.floor(i * frequency / DEFAULT_SAMPLE_RATE_IN_SECONDS) - Math.floor(2.0 * i.toDouble() * frequency / DEFAULT_SAMPLE_RATE_IN_SECONDS)
            }
            return mSound
        }
    }
}
