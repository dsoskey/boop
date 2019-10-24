package com.example.boop

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.boop.pad.*
import com.example.boop.synth.DefaultSynthesizer
import com.example.boop.synth.Synthesizer
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener


class MainActivity : AppCompatActivity(), ColorPickerDialogListener {
    var colorScheme: ColorScheme = ColorScheme.DIAGONAL(
        Color.valueOf(Color.parseColor("#FFF549")),
        Color.valueOf(Color.parseColor("#FFBF07"))
    )
    private var buttonToColor: MutableMap<Int, Int> = colorScheme.buttonToColor()
    private val synthesizer: Synthesizer = DefaultSynthesizer.DEFAULT_SYNTHESIZER
    private val padFragment: PadFragment = PadFragment(this, synthesizer, buttonToColor)

    override fun onDialogDismissed(dialogId: Int) {}
    override fun onColorSelected(dialogId: Int, color: Int) {
        buttonToColor[dialogId] = color
        padFragment.setPadColor(dialogId, Color.valueOf(color))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_color_picker_mode -> {
                padFragment.actionMode = PadFragment.PadAction.COLOR
            }
            else -> padFragment.actionMode = PadFragment.PadAction.PLAY
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onOptionsMenuClosed(menu: Menu?) {
        padFragment.actionMode = PadFragment.PadAction.PLAY
        super.onOptionsMenuClosed(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        val synthesizer = DefaultSynthesizer.DEFAULT_SYNTHESIZER

        val engineTransaction = supportFragmentManager.beginTransaction()
        val engineSelectorFragment = EngineSelectorFragment(synthesizer)
        engineTransaction.add(R.id.side_action, engineSelectorFragment)
        engineTransaction.commit()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.main_action, padFragment)
        fragmentTransaction.commit()
    }
}