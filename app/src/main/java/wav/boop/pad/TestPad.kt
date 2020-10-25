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
import wav.boop.pitch.getFrequenciesForScale

class TestPad: Fragment() {
    // Native interface
    private external fun setWaveOn(oscIndex: Int, isOn: Boolean)
    private external fun setFrequency(oscIndex: Int, frequency: Double)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.test_pad, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val pitchModel: PitchModel by activityViewModels()
        val pitches = getFrequenciesForScale(pitchModel.tonicFrequency, Scale.CHROMATIC)
        test_pad_button.setOnTouchListener { v, event ->
            val rawIndex = (event.x / (v.width / pitches.size)).toInt()
            val pitchIndex = if (rawIndex >= pitches.size) pitches.size - 1 else rawIndex
            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_BUTTON_RELEASE -> {
                    listOf(0, 1).forEach {
                        setWaveOn(it, false)
                    }
                }
                MotionEvent.ACTION_DOWN -> {
                    listOf(0, 1).forEach {
                        setFrequency(it, pitches[pitchIndex])
                        setWaveOn(it, true)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    listOf(0, 1).forEach {
                        setFrequency(it, pitches[pitchIndex])
                    }
                }
            }
            true
        }
    }
}