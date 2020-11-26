package wav.boop.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wav.boop.pad.padToOscillator
import wav.boop.preset.OscillatorController
import wav.boop.preset.OscillatorState

class OscillatorModel: ViewModel(), OscillatorController {
    val oscillators = MutableLiveData<MutableList<OscillatorState>>(mutableListOf(
        OscillatorState("SIN", .5f),
        OscillatorState("SIN", .5f)
    ))

    // Native interface for configuring waveforms
    private external fun ndkSetWaveform(oscIndex: Int, waveform: String)
    private external fun ndkSetAmplitude(oscIndex: Int, amplitude: Float)

    override fun setAmplitude(waveNum: Int, amplitude: Float) {
        padToOscillator.forEach{ (_, oscIndices) ->
            oscIndices.forEach { oscIndex ->
                if (oscIndex % 2 == waveNum) ndkSetAmplitude(oscIndex, amplitude)
            }
        }
        oscillators.value!![waveNum] = oscillators.value!![waveNum].copy(amplitude = amplitude)
        oscillators.value = oscillators.value!!.take(2).toMutableList()
    }

    override fun setWaveform(waveNum: Int, waveform: String) {
        padToOscillator.forEach{ (_, oscIndices) ->
            oscIndices.forEach { oscIndex ->
                if (oscIndex % 2 == waveNum) ndkSetWaveform(oscIndex, waveform)
            }
        }
        oscillators.value!![waveNum] = oscillators.value!![waveNum].copy(baseWave = waveform)
        oscillators.value = oscillators.value!!.take(2).toMutableList()
    }
}