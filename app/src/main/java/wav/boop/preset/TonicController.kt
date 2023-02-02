package wav.boop.preset

import wav.boop.pitch.NoteLetter

interface TonicController {
    fun setTonic(frequency: Double)
    fun setTonic(noteLetter: NoteLetter)
    fun setTonic(octave: Int)
    fun setTonic(noteLetter: NoteLetter, octave: Int)
}