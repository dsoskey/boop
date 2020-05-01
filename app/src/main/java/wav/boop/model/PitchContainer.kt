package wav.boop.model

import androidx.lifecycle.ViewModel
import wav.boop.pad.padIds
import wav.boop.pad.padToOscillator
import wav.boop.pitch.Chord
import wav.boop.pitch.NoteLetter
import wav.boop.pitch.getFrequenciesFromTonic
import wav.boop.pitch.westernTuning

/**
 * ViewModel that manages the pitches of the synthesizer.
 */
class PitchContainer: ViewModel() {
    // Native interface
    private external fun setFrequency(oscIndex: Int, frequency: Double)

    var chord: Chord = Chord.MONO
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
            (padToOscillator[id] ?: error("")).forEach { oscIndex ->
                setFrequency(oscIndex, pitches[index])
            }
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