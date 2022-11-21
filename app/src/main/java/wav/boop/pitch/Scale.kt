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
    CHROMATIC((0..12).toList().toIntArray()), // ALL OF THEM
    ISOMORPHIC_HIGH((7..21 step 2).toList().toIntArray()),
    ISOMORPHIC_LOW((0..16 step 2).toList().toIntArray());

    fun frequencies(rootFrequency: Double): DoubleArray =
        steps.map { step ->
            westernTuning(rootFrequency, step)
        }.toDoubleArray()
}

enum class Chord(val steps: IntArray) {
    MONO(intArrayOf(0)),
    FIFTH(intArrayOf(0, 7)),
    MAJOR(intArrayOf(0, 4, 7)),
    MINOR(intArrayOf(0, 3, 7)),
    MAJOR_7TH(intArrayOf(0, 4, 7, 11)),
    MINOR_7TH(intArrayOf(0, 3, 7, 11)),
    ONE_HUNDRED_GECS((1..100).toList().toIntArray());

    fun frequencies(rootFrequency: Double) : DoubleArray =
        steps.map { step ->
            westernTuning(rootFrequency, step)
        }.toDoubleArray()
}