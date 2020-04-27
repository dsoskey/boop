// https://github.com/android/views-widgets-samples/blob/master/ViewPager2/app/src/main/java/androidx/viewpager2/integration/testapp/BaseCardActivity.kt
package wav.boop.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import wav.boop.R
import wav.boop.model.LockedViewModel
import wav.boop.model.PadActionViewModel
import wav.boop.pad.PadFragment
import kotlin.math.max

private const val MIN_SCALE = 0.85f
private const val MIN_ALPHA = 0.5f

class ControlFragment: Fragment() {
    lateinit var fragmentView: View
    private lateinit var subFragments: Array<Fragment>
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.control_container, container, false)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val pitchControlFragment = PitchControlFragment()
        val engineSelectorFragment = EngineSelectorFragment()
        val adsrControlFragment = ADSRControlFragment()
        val colorControlFragment = ColorControlFragment()
        subFragments = arrayOf(pitchControlFragment, engineSelectorFragment, adsrControlFragment, colorControlFragment)

        viewPager = fragmentView.findViewById(R.id.control_pager)
        val isLockedViewModel: LockedViewModel by activityViewModels()
        isLockedViewModel.isLocked.observe(viewLifecycleOwner, Observer { isLocked ->
            viewPager.isUserInputEnabled = !isLocked
        })
        viewPager.setPageTransformer { view, position ->
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
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val padAction: PadActionViewModel by activityViewModels()
                super.onPageSelected(position)
                if (subFragments[position] is ColorControlFragment) {
                    padAction.setPadAction(PadFragment.PadAction.COLOR)
                } else {
                    padAction.setPadAction(PadFragment.PadAction.PLAY)
                }
            }
        })
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = subFragments.size
            override fun createFragment(position: Int): Fragment = subFragments[position]
        }

    }
}