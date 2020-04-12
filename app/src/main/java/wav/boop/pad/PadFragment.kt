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
import wav.boop.model.PitchContainer
import wav.boop.pitch.getFrequenciesForChord

val padIds: IntArray = intArrayOf(
    R.id.grid_0, R.id.grid_1, R.id.grid_2, R.id.grid_3,
    R.id.grid_4, R.id.grid_5, R.id.grid_6, R.id.grid_7,
    R.id.grid_8, R.id.grid_9, R.id.grid_10, R.id.grid_11,
    R.id.grid_12, R.id.grid_13, R.id.grid_14, R.id.grid_15
)

class PadFragment : Fragment() {
    private external fun touchEvent(action: Int, frequency: Double)

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
            val pitchContainer: PitchContainer by activityViewModels()
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
                        val frequency = pitchContainer.frequencyMap[buttonId]!!
                        getFrequenciesForChord(frequency, pitchContainer.chord).forEach { gec ->
                            touchEvent(event.action, gec)
                        }
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
