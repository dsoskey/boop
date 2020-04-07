package wav.boop.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import wav.boop.R

class TitleBarFragment(val toggleLock: () -> Unit): Fragment() {
    lateinit var fragmentView: View

    private var locked: Boolean = false

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
            toggleLock()
            locked = !locked
            val resId: Int = if (locked) R.drawable.outline_lock_24 else R.drawable.outline_lock_open_24
            button.setImageResource(resId)
        }
    }
}