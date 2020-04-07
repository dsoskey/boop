package wav.boop.pitch

import wav.boop.R

class PitchContainer(
    private val padIds: IntArray,
//    var scale: Scale = Sc,
    var chord: Chord = Chord.MONO,
    private val baseOctave: Int = 4,
    private val baseNoteLetter: NoteLetter = NoteLetter.A,
    private val baseFrequency: Double = 440.0
) {
    public var frequencyMap: MutableMap<Int, Double> = HashMap()
        private set
    public var tonicOctave: Int = baseOctave
        private set
    public var tonicNoteLetter: NoteLetter = baseNoteLetter
        private set
    public var tonicFrequency: Double = baseFrequency
        private set

    init {
        setTonic(baseFrequency)
    }

    public fun setTonic(frequency: Double) {
        val pitches: DoubleArray = getFrequenciesFromTonic(frequency, padIds.size)
        padIds.forEachIndexed { index, id ->
            frequencyMap[id] = pitches[index]
        }
    }

    public fun setTonic(noteLetter: NoteLetter = tonicNoteLetter, octave: Int = tonicOctave) {
        tonicNoteLetter = noteLetter
        tonicOctave = octave
        tonicFrequency = westernTuning(noteLetter, octave, baseOctave, baseNoteLetter, baseFrequency)
        setTonic(tonicFrequency)
    }


}