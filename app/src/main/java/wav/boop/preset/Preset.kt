package wav.boop.preset

import kotlinx.serialization.Serializable
import wav.boop.pitch.NoteLetter

@Serializable
data class OscillatorState(val baseWave: String, val amplitude: Float)

@Serializable
data class Preset(
    val fileName: String,
    var attack: Int, // milliseconds
    var decay: Int, // milliseconds
    var sustain: Float, // [0, 1]
    var release: Int, // milliseconds
    // TODO: Make tonic the frequency and figure out
    var tonicLetter: NoteLetter,
    var tonicOctave: Int,
    val oscillators: List<OscillatorState>
)
