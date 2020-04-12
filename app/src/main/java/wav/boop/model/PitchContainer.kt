package wav.boop.model

import androidx.lifecycle.ViewModel
import wav.boop.pad.padIds
import wav.boop.pitch.Chord
import wav.boop.pitch.NoteLetter
import wav.boop.pitch.getFrequenciesFromTonic
import wav.boop.pitch.westernTuning

class PitchContainer: ViewModel() {
    var chord: Chord =
        Chord.MONO
    private val baseOctave: Int = 4
    private val baseNoteLetter: NoteLetter = NoteLetter.A
    private val baseFrequency: Double = 440.0
    var frequencyMap: MutableMap<Int, Double> = HashMap()
        private set
    var tonicOctave: Int = baseOctave
        private set
    var tonicNoteLetter: NoteLetter = baseNoteLetter
        private set
    var tonicFrequency: Double = baseFrequency
        private set

    init {
        setTonic(baseFrequency)
    }

    fun setTonic(frequency: Double) {
        val pitches: DoubleArray =
            getFrequenciesFromTonic(frequency, padIds.size)
        padIds.forEachIndexed { index, id ->
            frequencyMap[id] = pitches[index]
        }
    }

    fun setTonic(noteLetter: NoteLetter = tonicNoteLetter, octave: Int = tonicOctave) {
        tonicNoteLetter = noteLetter
        tonicOctave = octave
        tonicFrequency = westernTuning(
            noteLetter,
            octave,
            baseOctave,
            baseNoteLetter,
            baseFrequency
        )
        setTonic(tonicFrequency)
    }
}