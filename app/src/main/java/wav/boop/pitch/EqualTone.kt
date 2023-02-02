package wav.boop.pitch

import kotlin.math.pow

fun coefficient(numTones: Int = 12): Double = 2.0.pow(1.0/numTones.toDouble())

fun westernTuning(
    noteLetter: NoteLetter,
    octave: Int,
    baseOctave: Int = 4,
    baseNoteLetter: NoteLetter = NoteLetter.A,
    baseFrequency: Double = 440.0): Double {
    val halfStepsFrom = ((octave - baseOctave) * 12) + noteLetter.rank - baseNoteLetter.rank
    return westernTuning(baseFrequency, halfStepsFrom)
}

fun westernTuning(
    rootFrequency: Double,
    halfStepsFrom: Int
): Double = rootFrequency * coefficient().pow(halfStepsFrom)