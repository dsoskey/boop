package wav.boop.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import wav.boop.file.SerialLoader
import wav.boop.preset.TonicController
import wav.boop.preset.ADSRController
import wav.boop.preset.OscillatorController
import wav.boop.preset.Preset

class SynthesizerModel(
    private val presetLoader: SerialLoader<Preset>,
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

    // TODO: Replace this with saving whenever something changes
    override fun onCleared() {
        val preset = buildPreset(AUTOSAVE_PREFIX)
        presetLoader.save(AUTOSAVE_PREFIX, preset)
        super.onCleared()
    }

    fun saveCurrentPreset(fileName: String) {
        presetLoader.save(fileName, buildPreset(fileName))
    }

    fun loadPreset(fileName: String): Boolean {
        val filePreset = presetLoader.get(fileName)
        if (filePreset != null) {
            currentFileName = fileName
            refresh(filePreset)
        }
        return filePreset != null
    }

    fun getLoadedPresets(): List<String> { return presetLoader.list() }

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
    private val presetLoader: SerialLoader<Preset>,
    private val tonicController: PitchModel,
    private val adsrController: ADSRModel,
    private val oscillatorController: OscillatorModel
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(
            SerialLoader::class.java,
            PitchModel::class.java,
            ADSRModel::class.java,
            OscillatorModel::class.java
        ).newInstance(presetLoader, tonicController, adsrController, oscillatorController)
    }
}
