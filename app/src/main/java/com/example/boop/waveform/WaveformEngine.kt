package com.example.boop.waveform

interface WaveformEngine {
    val DEFAULT_SAMPLE_RATE_IN_SECONDS: Int
        get() = 44100

//    fun getWaveform(frequency: Double): ShortArray
    fun getWaveform(frequency: Double): () -> DoubleArray
}