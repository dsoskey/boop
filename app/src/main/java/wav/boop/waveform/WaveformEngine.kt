package wav.boop.waveform

interface WaveformEngine {
    fun getWaveform(frequency: Double): () -> DoubleArray
}