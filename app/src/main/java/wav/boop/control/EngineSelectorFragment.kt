package wav.boop.control

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import wav.boop.R
import wav.boop.synth.Synthesizer

class EngineSelectorFragment : Fragment() {
    private val engines: List<String> = listOf("sin", "square", "saw")

    private external fun setWaveform(waveformGenerator: String)
    private lateinit var fragmentView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.engine_handler, container, false)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureButtons()
    }

    private fun configureButtons() {
        val squareButton = fragmentView.findViewById<Button>(R.id.square_button)
        val sineButton = fragmentView.findViewById<Button>(R.id.sin_button)
        val sawButton = fragmentView.findViewById<Button>(R.id.saw_button)

        
        squareButton.setOnTouchListener({ view, _ ->
            squareButton.setTextColor(resources.getColor(R.color.innerRind))
            sineButton.setTextColor(resources.getColor(R.color.meat))
            sawButton.setTextColor(resources.getColor(R.color.meat))
            setWaveform("square")
            view.performClick()
        })

        sineButton.setOnTouchListener({ view, _ ->
            squareButton.setTextColor(resources.getColor(R.color.meat))
            sineButton.setTextColor(resources.getColor(R.color.innerRind))
            sawButton.setTextColor(resources.getColor(R.color.meat))
            setWaveform("sin")
            view.performClick()
        })

        sawButton.setOnTouchListener({ view, _ ->
            squareButton.setTextColor(resources.getColor(R.color.meat))
            sineButton.setTextColor(resources.getColor(R.color.meat))
            sawButton.setTextColor(resources.getColor(R.color.innerRind))
            setWaveform("saw")
            view.performClick()
        })
        squareButton.setTextColor(resources.getColor(R.color.meat))
        sineButton.setTextColor(resources.getColor(R.color.innerRind))
        sawButton.setTextColor(resources.getColor(R.color.meat))
        setWaveform("sin")
    }
}