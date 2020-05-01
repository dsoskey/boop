package wav.boop.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.engine_handler.*
import wav.boop.R
import wav.boop.pad.padToOscillator

/**
 * Handles configuring the 2 oscillators. Contained within ControlFragment.
 */
class OscillatorControlFragment : Fragment() {
    private companion object {
        private const val SQUARE = "square"
        private const val SIN = "sin"
        private const val SAW = "saw"
    }

    // Native interface for configuring waveforms
    private external fun setWaveform(oscIndex: Int, waveform: String)
    private external fun setAmplitude(oscIndex: Int, amplitude: Float)

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
        val offTextColor: Int = ContextCompat.getColor(requireContext(), R.color.meat)
        val onTextColor: Int = ContextCompat.getColor(requireContext(), R.color.innerRind)

        val squareButton = requireView().findViewById<Button>(squareId)
        val sinButton = requireView().findViewById<Button>(sinId)
        val sawButton = requireView().findViewById<Button>(sawId)

        squareButton.apply {
            setTextColor(offTextColor)

            setOnTouchListener({ view, _ ->
                squareButton.setTextColor(onTextColor)
                sinButton.setTextColor(offTextColor)
                sawButton.setTextColor(offTextColor)
                padToOscillator.forEach{ (_, oscIndices) ->
                    oscIndices.forEach { oscIndex ->
                        if (oscIndex % 2 == waveNum) setWaveform(oscIndex, SQUARE)
                    }
                }
                view.performClick()
            })
        }

        sinButton.apply {
            setTextColor(onTextColor)

            setOnTouchListener({ view, _ ->
                squareButton.setTextColor(offTextColor)
                sinButton.setTextColor(onTextColor)
                sawButton.setTextColor(offTextColor)
                padToOscillator.forEach { (_, oscIndices) ->
                    oscIndices.forEach { oscIndex ->
                        if (oscIndex % 2 == waveNum) setWaveform(oscIndex, SIN)
                    }
                }
                view.performClick()
            })
        }

        sawButton.apply {
            setTextColor(offTextColor)

            setOnTouchListener({ view, _ ->
                squareButton.setTextColor(offTextColor)
                sinButton.setTextColor(offTextColor)
                sawButton.setTextColor(onTextColor)
                padToOscillator.forEach { (_, oscIndices) ->
                    oscIndices.forEach { oscIndex ->
                        if (oscIndex % 2 == waveNum) setWaveform(oscIndex, SAW)
                    }
                }
                view.performClick()
            })
        }

        padToOscillator.forEach{ (_, oscIndices) ->
            oscIndices.forEach { oscIndex ->
                if (oscIndex % 2 == waveNum) setWaveform(oscIndex, SIN)
            }
        }
    }
    private fun configureSliders() {
        wave1_amplitude.apply {
            progress = 50

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    padToOscillator.forEach{ (_, oscIndices) ->
                        oscIndices.forEach { oscIndex ->
                            if (oscIndex % 2 == 0) setAmplitude(oscIndex, progress / 100.0f)
                        }
                    }
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
                    padToOscillator.forEach{ (_, oscIndices) ->
                        oscIndices.forEach { oscIndex ->
                            if (oscIndex % 2 == 1) setAmplitude(oscIndex, progress / 100.0f)
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        padToOscillator.forEach{ (_, oscIndices) ->
            oscIndices.forEach { oscIndex ->
                setAmplitude(oscIndex, .5f)
            }
        }
    }
}