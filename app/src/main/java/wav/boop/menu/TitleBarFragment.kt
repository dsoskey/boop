package wav.boop.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.title_bar.*
import wav.boop.R
import wav.boop.model.LockedViewModel

class TitleBarFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.title_bar, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lock_button.setOnClickListener {
            val lockedViewModel: LockedViewModel by activityViewModels()
            lockedViewModel.toggleIsLocked()

            val resId: Int = if (lockedViewModel.isLocked.value!!)
                R.drawable.outline_lock_24
            else
                R.drawable.outline_lock_open_24
            lock_button.setImageResource(resId)
        }
    }
}