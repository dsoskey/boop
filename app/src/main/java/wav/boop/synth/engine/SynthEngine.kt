package wav.boop.synth.engine

import wav.boop.waveform.WaveformEngine

interface SynthEngine {
    fun startEngine()
    fun stopEngine()
    fun getTrackIds(): IntArray
    fun subscribeToTrack(trackId: Int, listener: (waveform: DoubleArray) -> Unit): Int
    fun unsubscribeFromTrack(trackId: Int, listenerId: Int)
    fun setWaveform(trackId: Int, waveform: WaveformEngine)
    fun playTrack(trackId: Int, frequency: Double)
    fun stopTrack(trackId: Int)
}