package wav.boop.pitch

enum class Scale(val steps: IntArray) {
    IONIAN(intArrayOf(0, 2, 4, 5, 7, 9, 11, 12)), // C 1
    MAJOR(intArrayOf(0, 2, 4, 5, 7, 9, 11, 12)), // C 1
    DORIAN(intArrayOf(0, 2, 3, 5, 7, 9, 10, 12)), //D 3
    PHRYGIAN(intArrayOf(0, 1, 3, 5, 7, 8, 10, 12)), // E 5
    LYDIAN(intArrayOf(0, 2, 4, 6, 7, 9, 11, 12)), // F 6
    MIXOLYDIAN(intArrayOf(0, 2, 4, 5, 7, 9, 10, 12)), // G 8
    AEOLIAN(intArrayOf(0, 2, 3, 5, 7, 8, 10, 12)), // A 10
    MINOR(intArrayOf(0, 2, 3, 5, 7, 8, 10, 12)), // A 10
    LOCRIAN(intArrayOf(0, 1, 3, 5, 6, 8, 10, 12)),  // B 12
    CHROMATIC(intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)), // ALL OF THEM
    MAJOR_7TH(intArrayOf(0, 4, 7, 11))
}

val ONE_HUNDRED_GECS = getFrequenciesFromTonic(NoteLetter.A_FLAT, 1, 100)

fun getFrequenciesForScale(
    rootFrequency: Double,
    scale: Scale
): DoubleArray = scale.steps.map { step -> westernTuning(rootFrequency, step) }.toDoubleArray()

fun getFrequenciesForScale(
    noteLetter: NoteLetter,
    octave: Int,
    scale: Scale,
    baseOctave: Int = 4,
    baseNoteLetter: NoteLetter = NoteLetter.A,
    baseFrequency: Double = 440.0
): DoubleArray = getFrequenciesForScale(
    westernTuning(noteLetter, octave, baseOctave, baseNoteLetter, baseFrequency),
    scale
)

fun getFrequenciesFromTonic(
    tonicLetter: NoteLetter,
    tonicOctave: Int,
    numFrequencies: Int,
    baseOctave: Int = 4,
    baseNoteLetter: NoteLetter = NoteLetter.A,
    baseFrequency: Double = 440.0
): DoubleArray = (0 until numFrequencies).map { step: Int ->
    westernTuning(
        westernTuning(tonicLetter, tonicOctave, baseOctave, baseNoteLetter, baseFrequency),
        step
    ) }.toDoubleArray()
//
//val jawn = 0..4.