package wav.boop.preset

interface ADSRController {
    fun setAttackLength(milliseconds: Int)
    fun setDecayLength(milliseconds: Int)
    fun setSustainLevel(amplitude: Float)
    fun setReleaseLength(milliseconds: Int)
}