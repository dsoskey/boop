package wav.boop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.pad_play_main.*
import wav.boop.pad.PadFragment

class PadPlayFragment: Fragment() {
    companion object {
        val subfragmendIds = intArrayOf(
            R.id.preset_menu_option,
            R.id.pitch_menu_option,
            R.id.oscillator_menu_option,
            R.id.adsr_menu_option
        )
    }

    // Gotten from https://www.youtube.com/watch?v=2k8x8V77CrU
    private val navController by lazy { requireActivity().findNavController(R.id.pad_play_nav_host_fragment) }
    private val appBarConfig by lazy { AppBarConfiguration(navController.graph, drawer_layout) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.pad_play_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Something about this config doesn't hook up the hamburger button.
        val parentActivity = requireActivity() as AppCompatActivity
        parentActivity.setSupportActionBar(title_bar)
        parentActivity.setupActionBarWithNavController(navController, appBarConfig)
        pad_play_navigation.setupWithNavController(navController)

        val basePadTransaction = parentFragmentManager.beginTransaction()
        val basePadFragment = PadFragment()
        basePadTransaction.add(R.id.main_action, basePadFragment)
        basePadTransaction.commit()

        pad_play_navigation.setNavigationItemSelectedListener {
            val index = if (subfragmendIds.indexOf(it.itemId) != -1) {
                subfragmendIds.indexOf(it.itemId)
            } else {
                0
            }

            val action = PlaySpaceFragmentDirections.openSynthesizerControls(subfragmendIds).setIndex(index)
            navController.navigate(action)
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }
    }
}