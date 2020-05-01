package wav.boop.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wav.boop.pad.PadFragment

/**
 * ViewModel that stores current action for pads.
 */
class PadActionViewModel: ViewModel() {
    val padAction = MutableLiveData(PadFragment.PadAction.PLAY)

    fun setPadAction(padAction: PadFragment.PadAction) {
        this.padAction.value = padAction
    }
}