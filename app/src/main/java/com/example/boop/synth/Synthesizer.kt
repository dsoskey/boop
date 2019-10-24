package com.example.boop.synth

import com.example.boop.audio.AudioEngine
import com.example.boop.waveform.WaveformEngine

interface Synthesizer {
    var waveformEngine: WaveformEngine
    val audioEngine: AudioEngine

    fun play(frequency: Double, viewId: Int)
    fun stop(viewId: Int)
}
