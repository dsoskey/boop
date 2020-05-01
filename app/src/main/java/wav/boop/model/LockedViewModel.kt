package wav.boop.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel that stores whether the control panel is locked on a specific control module or not
 */
class LockedViewModel: ViewModel() {
    val isLocked = MutableLiveData(false)

    fun toggleIsLocked() {
        isLocked.value = !isLocked.value!!
    }
}