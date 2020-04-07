package wav.boop

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import wav.boop.pad.*
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import wav.boop.control.ControlFragment
import wav.boop.control.EngineSelectorFragment
import wav.boop.control.PitchControlFragment
import wav.boop.menu.TitleBarFragment
import wav.boop.pitch.PitchContainer


class MainActivity : AppCompatActivity(), ColorPickerDialogListener {
    private external fun startEngine()
    private external fun stopEngine()

    private val pitchContainer: PitchContainer = PitchContainer(padIds)
    lateinit var colorScheme: ColorScheme
    lateinit var controlFragment: ControlFragment
    lateinit var padFragment: PadFragment

    override fun onDialogDismissed(dialogId: Int) {}
    override fun onColorSelected(dialogId: Int, colorInt: Int) {
        val color = Color.valueOf(colorInt)
        colorScheme.withColor(dialogId, color)
        padFragment.setPadColor(dialogId, color)
    }

    override fun onOptionsMenuClosed(menu: Menu?) {
        padFragment.actionMode = PadFragment.PadAction.PLAY
        super.onOptionsMenuClosed(menu)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.toolbar_menu, menu)
//
//        val engineSelectorExpandListener = object: MenuItem.OnActionExpandListener {
//            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
//                return true // Return true to collapse action view
//            }
//
//            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
//                padFragment.actionMode = PadFragment.PadAction.PLAY
//                colorScheme.idList.forEach { id -> padFragment.darken(id) }
//                return true // Return true to expand action view
//            }
//        }
//        val engineSelectorMenuItem = menu?.findItem(R.id.action_bar_engine_handler)
//        engineSelectorMenuItem?.setOnActionExpandListener(engineSelectorExpandListener)
//        val colorPickerExpandListener = object : MenuItem.OnActionExpandListener {
//            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
//                padFragment.actionMode = PadFragment.PadAction.PLAY
//                colorScheme.idList.forEach { id -> padFragment.darken(id) }
//                return true // Return true to collapse action view
//            }
//
//            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
//                padFragment.actionMode = PadFragment.PadAction.COLOR
//                colorScheme.idList.forEach { id -> padFragment.brighten(id) }
//                return true // Return true to expand action view
//            }
//        }
//        val colorPickerMenuItem = menu?.findItem(R.id.action_color_picker_mode)
//        colorPickerMenuItem?.setOnActionExpandListener(colorPickerExpandListener)
//        return true
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startEngine()
        colorScheme = ColorScheme.piano(
            Color.valueOf(resources.getColor(R.color.meat)),
            Color.valueOf(resources.getColor(R.color.seeds))
        )

        val pitchControlFragment = PitchControlFragment(pitchContainer)
        val engineSelectorFragment = EngineSelectorFragment()
        val controlTransaction = supportFragmentManager.beginTransaction()
        controlFragment = ControlFragment(arrayOf(pitchControlFragment, engineSelectorFragment))
        controlTransaction.add(R.id.side_action, controlFragment)
        controlTransaction.commit()

        val titleBarTransaction = supportFragmentManager.beginTransaction()
        val titleBarFragment = TitleBarFragment({ controlFragment.toggleLockControl() })
        titleBarTransaction.add(R.id.title_bar, titleBarFragment)
        titleBarTransaction.commit()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        padFragment = PadFragment(this, pitchContainer, colorScheme)
        fragmentTransaction.add(R.id.main_action, padFragment)
        fragmentTransaction.commit()
    }

    override fun onDestroy() {
        stopEngine()
        super.onDestroy()
    }

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}