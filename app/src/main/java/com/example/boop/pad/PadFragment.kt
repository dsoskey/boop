package com.example.boop.pad

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.boop.R
import com.example.boop.synth.Synthesizer
import com.jaredrummler.android.colorpicker.ColorPickerDialog

class PadFragment(
    private val parent: FragmentActivity,
    private val synthesizer: Synthesizer,
    private val colorScheme: ColorScheme,
    var actionMode: PadAction = PadAction.PLAY
) : Fragment(), View.OnTouchListener {

    enum class PadAction {
        COLOR, PLAY
    }
    private var fragmentView: View? = null
    private var frequencyMap: MutableMap<Int, Double> = HashMap()

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val buttonId = view.id
        when (actionMode) {
            PadAction.COLOR -> {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                        .setAllowPresets(false)
                        .setDialogId(buttonId)
                        .setColor(colorScheme.getColor(buttonId).toArgb())
                        .show(parent)
                }
            }
            PadAction.PLAY -> {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    synthesizer.play(frequencyMap[buttonId]!!, buttonId)
                    brighten(buttonId)
                } else if (event.action == MotionEvent.ACTION_UP ||
                    event.action == MotionEvent.ACTION_CANCEL ||
                    event.action == MotionEvent.ACTION_BUTTON_RELEASE
                ) {
                    synthesizer.audioEngine.stop(buttonId)
                    darken(buttonId)
                }
            }
        }
        return true
    }

    fun setPadColors(scheme: ColorScheme) {
        for (id in scheme.idList) {
            setPadColor(id, scheme.getColor(id))
        }
    }
    fun setPadColor(id: Int, color: Color) {
        val pad = fragmentView!!.findViewById<Button>(id)
        pad.setBackgroundColor(color.toArgb())
    }
    fun darken(id: Int) {
        val pad = fragmentView!!.findViewById<Button>(id)
        pad.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#80000000"))
    }
    fun brighten(id: Int) {
        val pad = fragmentView!!.findViewById<Button>(id)
        pad.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.pad_grid, container, false)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureFrequencies()
        configureColors()
    }

    private fun configureFrequencies() {
        frequencyMap[R.id.grid_0] = 523.25 //C5
        frequencyMap[R.id.grid_1] = 554.37 //C#5/Db5
        frequencyMap[R.id.grid_2] = 587.33 //D5
        frequencyMap[R.id.grid_3] = 622.25 //D#5/Eb5
        frequencyMap[R.id.grid_4] = 659.25 //E5
        frequencyMap[R.id.grid_5] = 698.46 //F5
        frequencyMap[R.id.grid_6] = 739.99 //F#5/Gb5
        frequencyMap[R.id.grid_7] = 783.99 //G5
        frequencyMap[R.id.grid_8] = 830.61 //G#5/Ab5
        frequencyMap[R.id.grid_9] = 880.00 //A5
        frequencyMap[R.id.grid_10] = 932.33 //A#5/Bb5
        frequencyMap[R.id.grid_11] = 987.77 //B5
        frequencyMap[R.id.grid_12] = 1046.50 //C6
        frequencyMap[R.id.grid_13] = 1108.73 //C#6/Db6
        frequencyMap[R.id.grid_14] = 1174.66 //D6
        frequencyMap[R.id.grid_15] = 1244.51 //D#6/Eb6

        // Setting up buttons for booping
        val grid0 = fragmentView!!.findViewById<Button>(R.id.grid_0)
        grid0.setOnTouchListener(this)
        val grid1 = fragmentView!!.findViewById<Button>(R.id.grid_1)
        grid1.setOnTouchListener(this)
        val grid2 = fragmentView!!.findViewById<Button>(R.id.grid_2)
        grid2.setOnTouchListener(this)
        val grid3 = fragmentView!!.findViewById<Button>(R.id.grid_3)
        grid3.setOnTouchListener(this)
        val grid4 = fragmentView!!.findViewById<Button>(R.id.grid_4)
        grid4.setOnTouchListener(this)
        val grid5 = fragmentView!!.findViewById<Button>(R.id.grid_5)
        grid5.setOnTouchListener(this)
        val grid6 = fragmentView!!.findViewById<Button>(R.id.grid_6)
        grid6.setOnTouchListener(this)
        val grid7 = fragmentView!!.findViewById<Button>(R.id.grid_7)
        grid7.setOnTouchListener(this)
        val grid8 = fragmentView!!.findViewById<Button>(R.id.grid_8)
        grid8.setOnTouchListener(this)
        val grid9 = fragmentView!!.findViewById<Button>(R.id.grid_9)
        grid9.setOnTouchListener(this)
        val grid10 = fragmentView!!.findViewById<Button>(R.id.grid_10)
        grid10.setOnTouchListener(this)
        val grid11 = fragmentView!!.findViewById<Button>(R.id.grid_11)
        grid11.setOnTouchListener(this)
        val grid12 = fragmentView!!.findViewById<Button>(R.id.grid_12)
        grid12.setOnTouchListener(this)
        val grid13 = fragmentView!!.findViewById<Button>(R.id.grid_13)
        grid13.setOnTouchListener(this)
        val grid14 = fragmentView!!.findViewById<Button>(R.id.grid_14)
        grid14.setOnTouchListener(this)
        val grid15 = fragmentView!!.findViewById<Button>(R.id.grid_15)
        grid15.setOnTouchListener(this)
    }

    private fun configureColors () {
        colorScheme.idList.forEach { id ->
            setPadColor(id, colorScheme.getColor(id))
            darken(id)
        }
    }
}
