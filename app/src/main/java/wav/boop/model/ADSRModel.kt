package wav.boop.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wav.boop.preset.ADSRController

class ADSRModel: ViewModel(), ADSRController {
    // Native interface
    private external fun ndkSetAttackLength(milliseconds: Int)
    private external fun ndkSetDecayLength(milliseconds: Int)
    private external fun ndkSetSustainLevel(amplitude: Float)
    private external fun ndkSetReleaseLength(milliseconds: Int)

    val attack = MutableLiveData<Int>(1042)
    val decay = MutableLiveData<Int>(2084)
    val sustain = MutableLiveData<Float>(.5f)
    val release = MutableLiveData<Int>(208)

    override fun setAttackLength(milliseconds: Int) {
        ndkSetAttackLength(milliseconds)
        attack.value = milliseconds
    }
    override fun setDecayLength(milliseconds: Int) {
        ndkSetDecayLength(milliseconds)
        decay.value = milliseconds
    }
    override fun setSustainLevel(amplitude: Float) {
        ndkSetSustainLevel(amplitude)
        sustain.value = amplitude
    }
    override fun setReleaseLength(milliseconds: Int) {
        ndkSetReleaseLength(milliseconds)
        release.value = milliseconds
    }
}