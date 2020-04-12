package wav.boop.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wav.boop.pad.PadFragment

class PadActionViewModel: ViewModel() {
    val padAction = MutableLiveData<PadFragment.PadAction>(PadFragment.PadAction.PLAY)

    fun setPadAction(padAction: PadFragment.PadAction) {
        this.padAction.value = padAction
    }
}