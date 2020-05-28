package wav.boop.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.engine_handler.*
import wav.boop.R
import wav.boop.color.getThemeColor
import wav.boop.model.OscillatorModel

/**
 * Handles configuring the 2 oscillators. Contained within ControlFragment.
 */
class OscillatorControlFragment : Fragment() {
    private companion object {
        private const val SQUARE = "square"
        private const val SIN = "sin"
        private const val SAW = "saw"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.engine_handler, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureButtons(R.id.square_button1, R.id.sin_button1, R.id.saw_button1, 0)
        configureButtons(R.id.square_button2, R.id.sin_button2, R.id.saw_button2, 1)
        configureSliders()
    }

    private fun configureButtons(squareId: Int, sinId: Int, sawId: Int, waveNum: Int) {
        val offTextColor: Int = getThemeColor(requireActivity().theme, R.attr.colorSecondary)
        val onTextColor: Int = getThemeColor(requireActivity().theme, R.attr.colorAccent)

        val squareButton = requireView().findViewById<Button>(squareId)
        val sinButton = requireView().findViewById<Button>(sinId)
        val sawButton = requireView().findViewById<Button>(sawId)

        val oscillatorModel: OscillatorModel by activityViewModels()
        oscillatorModel.oscillators.observe(viewLifecycleOwner, Observer { oscillators ->
            squareButton.setTextColor(offTextColor)
            sinButton.setTextColor(offTextColor)
            sawButton.setTextColor(offTextColor)
            when (oscillators[waveNum].baseWave) {
                SQUARE -> squareButton
                SIN -> sinButton
                else -> sawButton
            }.setTextColor(onTextColor)
        })
        squareButton.apply {
            setTextColor(offTextColor)

            setOnTouchListener { view, _ ->
                oscillatorModel.setWaveform(waveNum, SQUARE)
                view.performClick()
            }
        }

        sinButton.apply {
            setTextColor(onTextColor)

            setOnTouchListener { view, _ ->
                oscillatorModel.setWaveform(waveNum, SIN)
                view.performClick()
            }
        }

        sawButton.apply {
            setTextColor(offTextColor)

            setOnTouchListener { view, _ ->
                oscillatorModel.setWaveform(waveNum, SAW)
                view.performClick()
            }
        }
    }

    private fun configureSliders() {
        val oscillatorModel: OscillatorModel by activityViewModels()
        oscillatorModel.oscillators.observe(viewLifecycleOwner, Observer { oscillators ->
            wave1_amplitude.progress = (100 * oscillators[0].amplitude).toInt()
            wave2_amplitude.progress = (100 * oscillators[1].amplitude).toInt()
        })
        wave1_amplitude.apply {
            progress = 50

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    oscillatorModel.setAmplitude(0, progress / 100.0f)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        wave2_amplitude.apply {
            progress = 50

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    oscillatorModel.setAmplitude(1, progress / 100.0f)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        oscillatorModel.setAmplitude(0, .5f)
        oscillatorModel.setAmplitude(1, .5f)
    }
}