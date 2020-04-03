package wav.boop.pitch

import kotlin.math.pow

abstract class EqualToneFrequencyGenerator(
    var numTones: Int,
    var baseFrequency: Double
) {
    private fun coefficient(): Double = 2.0.pow(1.0/numTones.toDouble())

    final fun getRelativeFrequency(stepsFromBase: Int) =
        baseFrequency * coefficient().pow(stepsFromBase)
}
