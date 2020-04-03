package wav.boop.pad

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
import wav.boop.R
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import wav.boop.pitch.NoteLetter
//import wav.boop.pitch.Scale
//import wav.boop.pitch.getFrequenciesForScale
import wav.boop.pitch.getFrequenciesFromTonic;

class PadFragment(
    private val parent: FragmentActivity,
    private val colorScheme: ColorScheme,
    var actionMode: PadAction = PadAction.PLAY
) : Fragment(), View.OnTouchListener {
    private external fun touchEvent(action: Int, frequency: Double)

    private val padIds: IntArray = intArrayOf(
        R.id.grid_0, R.id.grid_1, R.id.grid_2, R.id.grid_3,
        R.id.grid_4, R.id.grid_5, R.id.grid_6, R.id.grid_7,
        R.id.grid_8, R.id.grid_9, R.id.grid_10, R.id.grid_11,
        R.id.grid_12, R.id.grid_13, R.id.grid_14, R.id.grid_15
    )
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
                val frequency = frequencyMap[buttonId]!!
//                getFrequenciesForScale(frequency, Scale.MAJOR_7TH).forEach { gec ->
//                    touchEvent(event.action, gec)
//                }
                touchEvent(event.action, frequency)
                if (event.action == MotionEvent.ACTION_DOWN) {
                    brighten(buttonId)
                } else if (event.action == MotionEvent.ACTION_UP ||
                    event.action == MotionEvent.ACTION_CANCEL ||
                    event.action == MotionEvent.ACTION_BUTTON_RELEASE
                ) {
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
        pad.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#40000000"))
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
        // Setting up buttons for booping
        val pitches: DoubleArray = getFrequenciesFromTonic(NoteLetter.C, 5, padIds.size)
        padIds.forEachIndexed { i: Int, pad: Int ->
            frequencyMap[pad] = pitches[i]
            val grid = fragmentView!!.findViewById<Button>(pad)
            grid.setOnTouchListener(this)
        }
    }

    private fun configureColors () {
        colorScheme.idList.forEach { id ->
            setPadColor(id, colorScheme.getColor(id))
            darken(id)
        }
    }
}
