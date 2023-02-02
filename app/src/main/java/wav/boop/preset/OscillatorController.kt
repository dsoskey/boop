package wav.boop.preset

interface OscillatorController {
    // Native interface for configuring waveforms
    fun setWaveform(waveNum: Int, waveform: String)
    fun setAmplitude(waveNum: Int, amplitude: Float)

}