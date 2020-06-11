package wav.boop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import wav.boop.control.ControlFragment
import wav.boop.model.*
import wav.boop.pad.PadFragment
import wav.boop.pad.TestPad
import wav.boop.preset.DefaultPresetLoader


/**
 * Contains components used in rendering the extra play controls.
 * TODO: Determine if I can just refer to the play control component instead of creating this fragment
 */
class PlaySpaceFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_play_space, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Add play panel. What should be on it
        val sideTransaction = parentFragmentManager.beginTransaction()
        val testPad = TestPad()
        sideTransaction.add(R.id.side_action, testPad)
        sideTransaction.commit()
    }
}
