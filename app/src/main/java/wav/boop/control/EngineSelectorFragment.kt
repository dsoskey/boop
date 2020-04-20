package wav.boop.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import wav.boop.R
import wav.boop.pad.padToOscillator

class EngineSelectorFragment : Fragment() {
    private val engines: List<String> = listOf("sin", "square", "saw")

    private external fun setWaveform(oscIndex: Int, waveform: String)
    private external fun setAmplitude(oscIndex: Int, amplitude: Float)
    private lateinit var fragmentView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.engine_handler, container, false)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureButtons(R.id.square_button1, R.id.sin_button1, R.id.saw_button1, 0)
        configureButtons(R.id.square_button2, R.id.sin_button2, R.id.saw_button2, 1)
        configureSliders()
    }

    private fun configureButtons(squareId: Int, sinId: Int, sawId: Int, waveNum: Int) {
        val squareButton = fragmentView.findViewById<Button>(squareId)
        val sineButton = fragmentView.findViewById<Button>(sinId)
        val sawButton = fragmentView.findViewById<Button>(sawId)

        
        squareButton.setOnTouchListener({ view, _ ->
            squareButton.setTextColor(resources.getColor(R.color.innerRind))
            sineButton.setTextColor(resources.getColor(R.color.meat))
            sawButton.setTextColor(resources.getColor(R.color.meat))
            padToOscillator.forEach{ (_, oscIndices) ->
                oscIndices.forEach { oscIndex ->
                    if (oscIndex % 2 == waveNum) setWaveform(oscIndex, "square")
                }
            }
            view.performClick()
        })

        sineButton.setOnTouchListener({ view, _ ->
            squareButton.setTextColor(resources.getColor(R.color.meat))
            sineButton.setTextColor(resources.getColor(R.color.innerRind))
            sawButton.setTextColor(resources.getColor(R.color.meat))
            padToOscillator.forEach{ (_, oscIndices) ->
                oscIndices.forEach { oscIndex ->
                    if (oscIndex % 2 == waveNum) setWaveform(oscIndex, "sin")
                }
            }
            view.performClick()
        })

        sawButton.setOnTouchListener({ view, _ ->
            squareButton.setTextColor(resources.getColor(R.color.meat))
            sineButton.setTextColor(resources.getColor(R.color.meat))
            sawButton.setTextColor(resources.getColor(R.color.innerRind))
            padToOscillator.forEach{ (_, oscIndices) ->
                oscIndices.forEach { oscIndex ->
                    if (oscIndex % 2 == waveNum) setWaveform(oscIndex, "saw")
                }
            }
            view.performClick()
        })
        squareButton.setTextColor(resources.getColor(R.color.meat))
        sineButton.setTextColor(resources.getColor(R.color.innerRind))
        sawButton.setTextColor(resources.getColor(R.color.meat))
        padToOscillator.forEach{ (_, oscIndices) ->
            oscIndices.forEach { oscIndex ->
                if (oscIndex % 2 == waveNum) setWaveform(oscIndex, "sin")
            }
        }
    }
    private fun configureSliders(vararg sliderIds: Int) {
        val ampSlider1: SeekBar = fragmentView.findViewById(R.id.wave1_amplitude);
        ampSlider1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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
        ampSlider1.progress = 50

        val ampSlider2: SeekBar = fragmentView.findViewById(R.id.wave2_amplitude);
        ampSlider2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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
        ampSlider2.progress = 50
        padToOscillator.forEach{ (_, oscIndices) ->
            oscIndices.forEach { oscIndex ->
                setAmplitude(oscIndex, .5f)
            }
        }
    }
}