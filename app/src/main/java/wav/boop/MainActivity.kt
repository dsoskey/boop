package wav.boop

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import wav.boop.pad.*
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import wav.boop.visualisation.ClassicOscilloscopeFragment


class MainActivity : AppCompatActivity(), ColorPickerDialogListener {
    private external fun startEngine()
    private external fun stopEngine()

    var colorScheme: ColorScheme = ColorScheme.piano(
        Color.valueOf(Color.parseColor("#FFEC00")),
        Color.valueOf(Color.parseColor("#AB00FF"))
    )
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val engineSelectorExpandListener = object: MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                return true // Return true to collapse action view
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                padFragment.actionMode = PadFragment.PadAction.PLAY
                colorScheme.idList.forEach { id -> padFragment.darken(id) }
                return true // Return true to expand action view
            }
        }
        val engineSelectorMenuItem = menu?.findItem(R.id.action_color_picker_mode)
        engineSelectorMenuItem?.setOnActionExpandListener(engineSelectorExpandListener)

        val colorPickerExpandListener = object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                padFragment.actionMode = PadFragment.PadAction.PLAY
                colorScheme.idList.forEach { id -> padFragment.darken(id) }
                return true // Return true to collapse action view
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                padFragment.actionMode = PadFragment.PadAction.COLOR
                colorScheme.idList.forEach { id -> padFragment.brighten(id) }
                return true // Return true to expand action view
            }
        }
        val colorPickerMenuItem = menu?.findItem(R.id.action_color_picker_mode)
        colorPickerMenuItem?.setOnActionExpandListener(colorPickerExpandListener)
        return true
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startEngine()

        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        val oscilloscopeTransaction = supportFragmentManager.beginTransaction()
        val oscilloscopeFragment = ClassicOscilloscopeFragment()
        oscilloscopeTransaction.add(R.id.side_action, oscilloscopeFragment)
        oscilloscopeTransaction.commit()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        padFragment = PadFragment(this, colorScheme)
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