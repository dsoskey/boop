package wav.boop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import wav.boop.pad.TestPad


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
