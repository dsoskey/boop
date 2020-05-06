package wav.boop.control

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import kotlinx.android.synthetic.main.pad_color_control.*
import wav.boop.R
import wav.boop.color.getThemeColor
import wav.boop.model.ColorAssignment
import wav.boop.model.ColorScheme

val colorButtonIds: IntArray = intArrayOf(
    R.id.color_button0,  R.id.color_button1,  R.id.color_button2,  R.id.color_button3,
    R.id.color_button10, R.id.color_button11, R.id.color_button12, R.id.color_button13
)

/**
 * Handles pad color selection. Contained within ControlFragment.
 */
class ColorControlFragment : Fragment() {

    private fun setPadColors(assignments: List<ColorAssignment>) {
        colorButtonIds.forEachIndexed { index, buttonId ->
            val button: Button = requireView().findViewById(buttonId)
            val colorAssignment = assignments.getOrNull(index)
            if (colorAssignment != null) {
                button.setBackgroundColor(colorAssignment.color.toArgb())
                button.isEnabled = true
            } else {
                button.setBackgroundColor(Color.TRANSPARENT)
                button.isEnabled = false

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.pad_color_control, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        configurePresetSelector()
        configureColorButtons()
    }

    private fun configurePresetSelector() {
        val presetAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ColorScheme.Preset.values())
        presetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        preset_spinner.adapter = presetAdapter
        preset_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val preset: ColorScheme.Preset = ColorScheme.Preset.valueOf(adapterView?.getItemAtPosition(pos).toString())
                val scheme: ColorScheme by activityViewModels()
                val color1: Color = scheme.getColorAtIndex(0) ?: Color.valueOf(getThemeColor(requireActivity().theme, R.attr.colorAccent))
                val color2: Color = scheme.getColorAtIndex(1) ?: Color.valueOf(getThemeColor(requireActivity().theme, R.attr.colorOnPrimary))
                when (preset) {
                    ColorScheme.Preset.PIANO -> scheme.makePiano(color1, color2)
                    ColorScheme.Preset.GRADIENT -> scheme.makeDiagonalGrandient(color1, color2)
                    ColorScheme.Preset.CHECKER -> scheme.makeChecker(color1, color2)
                    ColorScheme.Preset.VERTICAL_STRIPE -> scheme.makeVerticalStripe(color1, color2)
                    ColorScheme.Preset.HORIZONTAL_STRIPE -> scheme.makeHorizontalStripe(color1, color2)
                    ColorScheme.Preset.MONOCHROME -> scheme.makeMonochrome(color1)
                }
                (adapterView?.getChildAt(0) as TextView).setTextColor(getThemeColor(requireActivity().theme, R.attr.colorOnSurface))
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun configureColorButtons() {
        val scheme: ColorScheme by activityViewModels()
        scheme.colorAssignment.observe(viewLifecycleOwner, Observer { assignments -> setPadColors(assignments) })
        colorButtonIds.forEachIndexed { index, buttonId ->
            val button: Button = requireView().findViewById(buttonId)
            button.setOnClickListener {
                ColorPickerDialog.newBuilder()
                    .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                    .setAllowPresets(false)
                    .setDialogId(buttonId)
                    .setColor(scheme.getAssignment(index)!!.color.toArgb())
                    .setShowAlphaSlider(true)
                    .show(activity)
            }
        }
    }
}