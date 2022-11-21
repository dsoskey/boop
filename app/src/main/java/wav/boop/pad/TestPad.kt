package wav.boop.pad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.test_pad.*
import wav.boop.R
import wav.boop.model.PitchModel
import wav.boop.pitch.Scale

class TestPad: Fragment() {
    // Native interface
    private external fun setWaveOn(oscIndex: Int, isOn: Boolean)
    private external fun setFrequency(oscIndex: Int, frequency: Double)

    companion object {
        val oscillators = arrayOf(40, 41)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.test_pad, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val pitchModel: PitchModel by activityViewModels()
        val pitches = Scale.CHROMATIC.frequencies(pitchModel.tonicFrequency)
        test_pad_button.setOnTouchListener { v, event ->
            val rawIndex = (event.x / (v.width / pitches.size)).toInt()
            val pitchIndex = if (rawIndex >= pitches.size) pitches.size - 1 else rawIndex
            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_BUTTON_RELEASE -> {
                    oscillators.forEach {
                        setWaveOn(it, false)
                    }
                }
                MotionEvent.ACTION_DOWN -> {
                    oscillators.forEach {
                        setFrequency(it, pitches[pitchIndex])
                        setWaveOn(it, true)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    oscillators.forEach {
                        setFrequency(it, pitches[pitchIndex])
                    }
                }
            }
            true
        }
    }
}