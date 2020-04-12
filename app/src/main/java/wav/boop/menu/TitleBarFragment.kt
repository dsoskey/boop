package wav.boop.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import wav.boop.R
import wav.boop.model.LockedViewModel

class TitleBarFragment: Fragment() {
    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.title_bar, container, false)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val button: ImageButton = fragmentView.findViewById(R.id.lock_button)
        button.setOnClickListener { v ->
            val lockedViewModel: LockedViewModel by activityViewModels()
            lockedViewModel.toggleIsLocked()

            val resId: Int = if (lockedViewModel.isLocked.value!!)
                R.drawable.outline_lock_24
            else
                R.drawable.outline_lock_open_24
            button.setImageResource(resId)
        }
    }
}