package wav.boop

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import wav.boop.synth.Synthesizer
import wav.boop.waveform.SawEngine
import wav.boop.waveform.SineEngine
import wav.boop.waveform.SquareEngine

class EngineSelectorFragment(private val synthesizer: Synthesizer) : Fragment()/*, View.OnTouchListener */ {

    private var fragmentView: View? = null

//    override fun onTouch(view: View, event: MotionEvent): Boolean {
//        val squareButton = fragmentView!!.findViewById<Button>(R.id.square_button)
//        val sineButton = fragmentView!!.findViewById<Button>(R.id.sine_button)
//        val sawButton = fragmentView!!.findViewById<Button>(R.id.saw_button)
//        when (view.id) {
//            R.id.square_button -> {
//                squareButton.setTextColor(Color.parseColor("#FFFFFF"))
//                sineButton.setTextColor(Color.parseColor("#000000"))
//                sawButton.setTextColor(Color.parseColor("#000000"))
//                synthesizer.waveformEngine = SquareEngine()
//            }
//            R.id.sine_button -> {
//                sineButton.setTextColor(Color.parseColor("#FFFFFF"))
//                squareButton.setTextColor(Color.parseColor("#000000"))
//                sawButton.setTextColor(Color.parseColor("#000000"))
//                synthesizer.waveformEngine = SineEngine()
//            }
//            R.id.saw_button -> {
//                sawButton.setTextColor(Color.parseColor("#FFFFFF"))
//                squareButton.setTextColor(Color.parseColor("#000000"))
//                sineButton.setTextColor(Color.parseColor("#000000"))
//                synthesizer.waveformEngine = SawEngine()
//            }
//        }
//        return true
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.engine_handler, container, false)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        configureButtons()
    }

    private fun configureButtons() {
        val squareButton = fragmentView!!.findViewById<Button>(R.id.square_button)
        val sineButton = fragmentView!!.findViewById<Button>(R.id.sine_button)
        val sawButton = fragmentView!!.findViewById<Button>(R.id.saw_button)

        squareButton.setOnTouchListener({ view, _ ->
            squareButton.setTextColor(Color.parseColor("#FFFFFF"))
            sineButton.setTextColor(Color.parseColor("#000000"))
            sawButton.setTextColor(Color.parseColor("#000000"))
            synthesizer.waveformEngine = SquareEngine()
            view.performClick()
        })

        sineButton.setOnTouchListener({ view, _ ->
            squareButton.setTextColor(Color.parseColor("#000000"))
            sineButton.setTextColor(Color.parseColor("#FFFFFF"))
            sawButton.setTextColor(Color.parseColor("#000000"))
            synthesizer.waveformEngine = SineEngine()
            view.performClick()
        })

        sawButton.setOnTouchListener({ view, _ ->
            squareButton.setTextColor(Color.parseColor("#000000"))
            sineButton.setTextColor(Color.parseColor("#000000"))
            sawButton.setTextColor(Color.parseColor("#FFFFFF"))
            synthesizer.waveformEngine = SawEngine()
            view.performClick()
        })
        squareButton.setTextColor(Color.parseColor("#000000"))
        sineButton.setTextColor(Color.parseColor("#000000"))
        sawButton.setTextColor(Color.parseColor("#FFFFFF"))
    }
}