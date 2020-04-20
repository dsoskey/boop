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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import wav.boop.R
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import wav.boop.model.ColorAssignment
import wav.boop.model.ColorScheme
import wav.boop.model.PadActionViewModel

class PadFragment : Fragment() {
    private external fun setWaveOn(oscIndex: Int, isOn: Boolean)

    var actionMode = PadAction.PLAY
        set(value) {
            padIds.forEach { id ->
                when(value) {
                    PadAction.COLOR -> brighten(id)
                    PadAction.PLAY -> darken(id)
                }
            }
            field = value
        }
    enum class PadAction {
        COLOR, PLAY
    }
    lateinit var fragmentView: View

    fun setPadColors(assignments: List<ColorAssignment>) {
        assignments.forEach { assignment ->
            assignment.padIds.forEach { padId ->
                setPadColor(padId, assignment.color)
            }
        }
    }
    fun setPadColor(id: Int, color: Color) {
        val pad = fragmentView.findViewById<Button>(id)
        pad.setBackgroundColor(color.toArgb())
    }
    fun darken(id: Int) {
        val pad = fragmentView.findViewById<Button>(id)
        pad.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#40000000"))
    }
    fun brighten(id: Int) {
        val pad = fragmentView.findViewById<Button>(id)
        pad.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.pad_grid, container, false)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val scheme: ColorScheme by activityViewModels()
        scheme.colorAssignment.observe(viewLifecycleOwner, Observer { assignments ->
            setPadColors(assignments)
        })
        val actionModel: PadActionViewModel by activityViewModels()
        actionMode = actionModel.padAction.value!!
        actionModel.padAction.observe(viewLifecycleOwner, Observer { actionMode -> this.actionMode = actionMode })
        padIds.forEach { pad: Int ->
            val grid = fragmentView.findViewById<Button>(pad)
            grid.setOnTouchListener { view, event ->
                val buttonId = view.id
                when (actionMode) {
                    PadAction.COLOR -> {
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            ColorPickerDialog.newBuilder()
                                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                                .setAllowPresets(false)
                                .setDialogId(buttonId)
                                .setColor(scheme.getColor(buttonId)!!.toArgb())
                                .show(activity)
                        }
                    }
                    PadAction.PLAY -> {
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            (padToOscillator[buttonId] ?: error("")).forEach { oscId ->
                                setWaveOn(oscId, true)
                            }
                            brighten(buttonId)
                        } else if (event.action == MotionEvent.ACTION_UP ||
                            event.action == MotionEvent.ACTION_CANCEL ||
                            event.action == MotionEvent.ACTION_BUTTON_RELEASE
                        ) {
                            (padToOscillator[buttonId] ?: error("")).forEach { oscId ->
                                setWaveOn(oscId, false)
                            }
                            darken(buttonId)
                        }
                    }
                }
                true
            }
        }
        padIds.forEach { id ->
            val color = scheme.getColor(id)
            if (color != null) {
                setPadColor(id, color)
            }
            darken(id)
        }
    }
}
