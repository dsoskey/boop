package wav.boop.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.adsr_controller.*
import wav.boop.R
import wav.boop.model.ADSRModel

/**
 * Handles interactions with ADSR. Contained within ControlFragment.
 */
class ADSRControlFragment : Fragment() {
    private companion object {
        private const val MAX_MILLIS: Int = 5000
        private const val MIN_MILLIS: Int = 50
        private const val MAX_AMPLITUDE: Int = 69
        private const val DESCRIPTION_FORMAT: String = "%.1f"
        private const val DEFAULT_ATTACK: Int = 1042
        private const val DEFAULT_DECAY: Int = 2084
        private const val DEFAULT_SUSTAIN: Int = 50
        private const val DEFAULT_RELEASE: Int = 208
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.adsr_controller, container, false)
    }

    /**
     * Configures ADSR seekbars
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adsrModel: ADSRModel by activityViewModels()
        adsrModel.attack.observe(viewLifecycleOwner, Observer { milliseconds -> attack_description.text = DESCRIPTION_FORMAT
            .format(milliseconds.toDouble() / 1000.0) })
        attack_bar.apply {
            maxValue = MAX_MILLIS
            progress = adsrModel.attack.value!!
            clickToSetProgress = false

            setOnProgressChangeListener { numMillis ->
                val milliseconds = if (numMillis > MIN_MILLIS) numMillis else MIN_MILLIS
                adsrModel.setAttackLength(milliseconds)
            }
        }

        adsrModel.decay.observe(viewLifecycleOwner, Observer { milliseconds -> decay_description.text = DESCRIPTION_FORMAT
            .format(milliseconds.toDouble() / 1000.0) })
        decay_bar.apply {
            maxValue = MAX_MILLIS
            progress = adsrModel.decay.value!!
            clickToSetProgress = false

            setOnProgressChangeListener { numMillis ->
                val milliseconds = if (numMillis > MIN_MILLIS) numMillis else MIN_MILLIS
                adsrModel.setDecayLength(milliseconds)
            }
        }

        adsrModel.sustain.observe(viewLifecycleOwner, Observer { rawAmplitude -> sustain_description.text = DESCRIPTION_FORMAT
            .format(rawAmplitude.toDouble()) })
        sustain_bar.apply {
            maxValue = MAX_AMPLITUDE
            progress = (adsrModel.sustain.value!! * 100).toInt()
            clickToSetProgress = false

            setOnProgressChangeListener { rawAmplitude ->
                adsrModel.setSustainLevel(rawAmplitude.toFloat() / 100.0f)
            }
        }

        adsrModel.release.observe(viewLifecycleOwner, Observer { milliseconds -> release_description.text = DESCRIPTION_FORMAT
            .format(milliseconds / 1000.0) })
        release_bar.apply {
            maxValue = MAX_MILLIS
            progress = adsrModel.release.value!!
            clickToSetProgress = false

            setOnProgressChangeListener { numMillis ->
                val milliseconds = if (numMillis > MIN_MILLIS) numMillis else MIN_MILLIS
                adsrModel.setReleaseLength(milliseconds)
            }
        }
    }
}