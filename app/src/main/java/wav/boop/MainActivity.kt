package wav.boop

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import wav.boop.control.ControlFragment
import wav.boop.control.colorButtonIds
import wav.boop.menu.TitleBarFragment
import wav.boop.model.ColorAssignment
import wav.boop.model.ColorScheme
import wav.boop.pad.PadFragment
import wav.boop.pad.padIds


class MainActivity : AppCompatActivity(), ColorPickerDialogListener {
    private external fun startEngine()
    private external fun stopEngine()

    lateinit var colorScheme: ColorScheme

    override fun onDialogDismissed(dialogId: Int) {}
    override fun onColorSelected(dialogId: Int, colorInt: Int) {
        val color = Color.valueOf(colorInt)
        when {
            padIds.contains(dialogId) -> {
                colorScheme.setColorForButtons(color, dialogId)
            }
            colorButtonIds.contains(dialogId) -> {
                val assignment: ColorAssignment? = colorScheme.getAssignment(colorButtonIds.indexOf(dialogId))
                if (assignment != null) {
                    colorScheme.setColorForButtons(color, assignment.padIds)
                }
            }
            else -> println("Called from nowhere!")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startEngine()
        colorScheme = ViewModelProvider(this)[ColorScheme::class.java]

        colorScheme.makePiano(
            Color.valueOf(ContextCompat.getColor(applicationContext, R.color.meat)),
            Color.valueOf(ContextCompat.getColor(applicationContext, R.color.seeds))
        )

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val padFragment = PadFragment()
        fragmentTransaction.add(R.id.main_action, padFragment)
        fragmentTransaction.commit()

        val controlTransaction = supportFragmentManager.beginTransaction()
        val controlFragment = ControlFragment()
        controlTransaction.add(R.id.side_action, controlFragment)
        controlTransaction.commit()

        val titleBarTransaction = supportFragmentManager.beginTransaction()
        val titleBarFragment = TitleBarFragment()
        titleBarTransaction.add(R.id.title_bar, titleBarFragment)
        titleBarTransaction.commit()
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