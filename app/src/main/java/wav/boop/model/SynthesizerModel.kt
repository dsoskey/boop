package wav.boop.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import wav.boop.preset.TonicController
import wav.boop.preset.ADSRController
import wav.boop.preset.OscillatorController
import wav.boop.preset.Preset
import wav.boop.preset.PresetLoader
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter

class SynthesizerModel(
    private val presetLoader: PresetLoader,
    private val tonicController: PitchModel,
    private val adsrController: ADSRModel,
    private val oscillatorController: OscillatorModel
): ViewModel(),
    TonicController by tonicController,
    ADSRController by adsrController,
    OscillatorController by oscillatorController {
    companion object {
        const val AUTOSAVE_PREFIX = "autozone"
    }
    var currentFileName: String = "Autozone"

    private fun buildPreset(fileName: String): Preset {
        return Preset(
            fileName,
            adsrController.attack.value!!,
            adsrController.decay.value!!,
            adsrController.sustain.value!!,
            adsrController.release.value!!,
            tonicController.tonicNoteLetter,
            tonicController.tonicOctave,
            oscillatorController.oscillators.value!!
        )
    }

    override fun onCleared() {
//        val fileName = "${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"))}-${AUTOSAVE_PREFIX}"
        val fileName = AUTOSAVE_PREFIX
        println("Clear!")
        presetLoader.savePreset(buildPreset(fileName))
        super.onCleared()
    }

    fun saveCurrentPreset(fileName: String) {
        presetLoader.savePreset(buildPreset(fileName))
    }

    fun loadPreset(fileName: String): Boolean {
        val filePreset = presetLoader.getPreset(fileName)
        if (filePreset != null) {
            currentFileName = fileName
            refresh(filePreset)
        }
        return filePreset != null
    }

    fun getLoadedPresets(): List<String> { return presetLoader.listPresets() }

    fun refresh() { refresh(buildPreset(currentFileName)) }
    private fun refresh(preset: Preset) {
        adsrController.setAttackLength(preset.attack)
        adsrController.setDecayLength(preset.decay)
        adsrController.setSustainLevel(preset.sustain)
        adsrController.setReleaseLength(preset.release)
        tonicController.setTonic(preset.tonicLetter, preset.tonicOctave)
        preset.oscillators.forEachIndexed { index, oscillator ->
            oscillatorController.setAmplitude(index, oscillator.amplitude)
            oscillatorController.setWaveform(index, oscillator.baseWave)
        }
    }
}

class SynthesizerModelFactory(
    private val presetLoader: PresetLoader,
    private val tonicController: PitchModel,
    private val adsrController: ADSRModel,
    private val oscillatorController: OscillatorModel
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(
            PresetLoader::class.java,
            PitchModel::class.java,
            ADSRModel::class.java,
            OscillatorModel::class.java
        ).newInstance(presetLoader, tonicController, adsrController, oscillatorController)
    }
}