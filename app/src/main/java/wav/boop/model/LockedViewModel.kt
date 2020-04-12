package wav.boop.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LockedViewModel: ViewModel() {
    val isLocked = MutableLiveData<Boolean>(false)

    fun toggleIsLocked() {
        isLocked.value = !isLocked.value!!
    }
}