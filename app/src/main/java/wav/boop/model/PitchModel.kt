package wav.boop.model

import androidx.lifecycle.ViewModel
import wav.boop.pad.getOscillatorsForIsoKey
import wav.boop.pitch.Chord
import wav.boop.pitch.NoteLetter
import wav.boop.pitch.Scale
import wav.boop.pitch.westernTuning
import wav.boop.preset.TonicController

/**
 * ViewModel that manages the pitches of the synthesizer.
 */
class PitchModel: ViewModel(), TonicController {
    // Native interface
    private external fun setFrequency(oscIndex: Int, frequency: Double)

    var chord: Chord = Chord.MONO
    private val baseOctave: Int = 4
    private val baseNoteLetter: NoteLetter = NoteLetter.A
    private val baseFrequency: Double = 440.0

    private val gb3: Double = 184.997
    var frequencyMap: MutableMap<Int, Double> = HashMap()
        private set

    var pitches: DoubleArray = doubleArrayOf()
        private set
    var tonicOctave: Int = baseOctave
        private set
    var tonicNoteLetter: NoteLetter = baseNoteLetter
        private set
    var tonicFrequency: Double = baseFrequency
        private set

    init {
        setTonic(gb3)
    }

    override fun setTonic(frequency: Double) {
        pitches = Scale.ISOMORPHIC_HIGH.frequencies(frequency)
            .plus(Scale.ISOMORPHIC_LOW.frequencies(frequency * 2))
            .plus(Scale.ISOMORPHIC_HIGH.frequencies(frequency * 2))
            .plus(Scale.ISOMORPHIC_LOW.frequencies(frequency * 4))
            .plus(Scale.ISOMORPHIC_HIGH.frequencies(frequency * 4))

        pitches.forEachIndexed { index, pitch ->
            getOscillatorsForIsoKey(index).forEach { oscIndex ->
                setFrequency(oscIndex, pitch)
            }
        }
    }

    override fun setTonic(octave: Int) {
        setTonic(tonicNoteLetter, octave)
    }

    override fun setTonic(noteLetter: NoteLetter) {
        setTonic(noteLetter, tonicOctave)
    }

    override fun setTonic(noteLetter: NoteLetter, octave: Int) {
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