// https://github.com/android/views-widgets-samples/blob/master/ViewPager2/app/src/main/java/androidx/viewpager2/integration/testapp/BaseCardActivity.kt
package wav.boop.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
//import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.control_container.*
import wav.boop.R
//import wav.boop.model.LockedViewModel
import wav.boop.model.PadActionViewModel
import wav.boop.pad.PadFragment
import kotlin.math.max

/**
 * Top level fragment for control panel.
 */
class ControlFragment: Fragment() {
    private companion object {
        private const val MIN_SCALE = 0.85f
        private const val MIN_ALPHA = 0.5f
    }

    private lateinit var subFragments: Array<Fragment>

    val args: ControlFragmentArgs by navArgs()

    fun setPage(index: Int) {
        control_pager.currentItem = index
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.control_container, container, false)
    }

    private fun initializeSubfragments() {
        val newSubfragments = ArrayList<Fragment>()
        args.subfragmentIds.forEach {
            newSubfragments.add(when (it) {
                R.id.preset_menu_option -> PresetControlFragment()
                R.id.pitch_menu_option -> PitchControlFragment()
                R.id.oscillator_menu_option -> OscillatorControlFragment()
                R.id.adsr_menu_option -> ADSRControlFragment()
                else -> Fragment()
            })
        }
        subFragments = newSubfragments.toTypedArray()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeSubfragments()
//        val isLockedViewModel: LockedViewModel by activityViewModels()
//        isLockedViewModel.isLocked.observe(viewLifecycleOwner, Observer { isLocked ->
//            control_pager.isUserInputEnabled = !isLocked
//        })

        control_pager.setPageTransformer { view, position ->
            view.apply {
                val pageWidth = width
                val pageHeight = height
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        alpha = 0f
                    }
                    position <= 1 -> { // [-1,1]
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = max(MIN_SCALE, 1 - Math.abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }

                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        // Fade the page relative to its size.
                        alpha = (MIN_ALPHA +
                                (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                    }
                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        alpha = 0f
                    }
                }
            }
        }
        control_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val padAction: PadActionViewModel by activityViewModels()
                super.onPageSelected(position)
                if (subFragments[position] is ColorControlFragment) {
                    padAction.setPadAction(PadFragment.PadAction.COLOR)
                } else {
                    padAction.setPadAction(PadFragment.PadAction.PLAY)
                }
                setLRVisibility()
            }
        })
        control_pager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = subFragments.size
            override fun createFragment(position: Int): Fragment = subFragments[position]
        }
        // smoothScroll:false allows us to set the menu item selected from
        control_pager.setCurrentItem(args.index, false)

        left.setOnClickListener {
            if (control_pager.currentItem > 0) {
                setPage(control_pager.currentItem - 1)
            }
        }
        right.setOnClickListener {
            if (control_pager.currentItem < subFragments.size - 1) {
                setPage(control_pager.currentItem + 1)
            }
        }
        setLRVisibility()
    }

    private fun setLRVisibility() {
        left.visibility = if (control_pager.currentItem == 0) {
            View.GONE
        } else {
            View.VISIBLE
        }
        right.visibility = if (control_pager.currentItem == subFragments.size - 1) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}