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

/**
 * Contains the pads used to play sounds.
 */
class PadFragment : Fragment() {
    // Native interface
    private external fun setWaveOn(oscIndex: Int, isOn: Boolean)

    private var actionMode = PadAction.PLAY
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

    private fun setPadColors(assignments: List<ColorAssignment>) {
        assignments.forEach { assignment ->
            assignment.padIds.forEach { padId ->
                setPadColor(padId, assignment.color)
            }
        }
    }
    private fun setPadColor(id: Int, color: Color) {
        requireView().findViewById<Button>(id).setBackgroundColor(color.toArgb())
    }
    // TODO: Make alpha a color resource
    private fun darken(id: Int) {
        requireView().findViewById<Button>(id).backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#40000000"))
    }
    private fun brighten(id: Int) {
        requireView().findViewById<Button>(id).backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#00000000"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.pad_grid, container, false)
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

        padIds.forEach { padId: Int ->
            val pad: Button = requireView().findViewById(padId)
            pad.setOnTouchListener { view, event ->
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

            val color = scheme.getColor(padId)
            if (color != null) {
                setPadColor(padId, color)
            }
            darken(padId)
        }
    }
}
