package wav.boop.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.adsr_controller.*
import wav.boop.R

class ADSRControlFragment : Fragment() {

    private external fun setAttackLength(milliseconds: Int)
    private external fun setDecayLength(milliseconds: Int)
    private external fun setSustainLevel(amplitude: Float)
    private external fun setReleaseLength(milliseconds: Int)

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

    private lateinit var fragmentView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.adsr_controller, container, false)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        attack_bar.apply {
            maxValue = MAX_MILLIS
            progress = DEFAULT_ATTACK
            clickToSetProgress = false

            setOnProgressChangeListener { numMillis ->
                val milliseconds = if (numMillis > MIN_MILLIS) numMillis else MIN_MILLIS
                attack_description.text = DESCRIPTION_FORMAT
                    .format(milliseconds.toDouble() / 1000.0)
//                    .replace('.', 'a')
                setAttackLength(milliseconds)
            }

        }
        setAttackLength(attack_bar.progress)
        attack_description.text = DESCRIPTION_FORMAT
            .format(attack_bar.progress.toDouble() / 1000.0)
//            .replace('.', 'a')

        decay_bar.apply {
            maxValue = MAX_MILLIS
            progress = DEFAULT_DECAY
            clickToSetProgress = false

            setOnProgressChangeListener { numMillis ->
                val milliseconds = if (numMillis > MIN_MILLIS) numMillis else MIN_MILLIS
                decay_description.text = DESCRIPTION_FORMAT
                    .format(milliseconds.toDouble() / 1000.0)
//                    .replace('.', 'd')
                setDecayLength(milliseconds)
            }
        }
        setDecayLength(decay_bar.progress)
        decay_description.text = DESCRIPTION_FORMAT
            .format(decay_bar.progress.toDouble() / 1000.0)
//            .replace('.', 'd')

        sustain_bar.apply {
            maxValue = MAX_AMPLITUDE
            progress = DEFAULT_SUSTAIN
            clickToSetProgress = false

            setOnProgressChangeListener { rawAmplitude ->
                sustain_description.text = DESCRIPTION_FORMAT
                    .format(rawAmplitude.toDouble() / 100.0)
//                    .replace('.', 's')
                setSustainLevel(rawAmplitude.toFloat() / 100.0f)
            }
        }
        setSustainLevel(sustain_bar.progress.toFloat() / 100.0f)
        sustain_description.text = DESCRIPTION_FORMAT
            .format(sustain_bar.progress.toDouble() / 100.0)
//            .replace('.', 's')

        release_bar.apply {
            maxValue = MAX_MILLIS
            progress = DEFAULT_RELEASE
            clickToSetProgress = false

            setOnProgressChangeListener { numMillis ->
                val milliseconds = if (numMillis > MIN_MILLIS) numMillis else MIN_MILLIS
                release_description.text = DESCRIPTION_FORMAT
                    .format(milliseconds / 1000.0)
//                    .replace('.', 'r')
                setReleaseLength(milliseconds)
            }
        }
        setReleaseLength(release_bar.progress)
        release_description.text = DESCRIPTION_FORMAT
            .format(release_bar.progress.toDouble() / 1000.0)
//            .replace('.', 'r')
    }
}