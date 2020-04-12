package wav.boop.control

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import wav.boop.R
import wav.boop.model.ColorAssignment
import wav.boop.model.ColorScheme

val colorButtonIds: IntArray = intArrayOf(
    R.id.color_button0,  R.id.color_button1,  R.id.color_button2,  R.id.color_button3,
    R.id.color_button10, R.id.color_button11, R.id.color_button12, R.id.color_button13
)

class ColorControlFragment : Fragment() {
    lateinit var fragmentView: View

    private fun setPadColors(assignments: List<ColorAssignment>) {
        colorButtonIds.forEachIndexed { index, buttonId ->
            val button: Button = fragmentView.findViewById(buttonId)
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
        fragmentView = inflater.inflate(R.layout.pad_color_control, container, false)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        configurePresetSelector()
        configureColorButtons()
    }

    private fun configurePresetSelector() {
        val presetSpinner: Spinner = fragmentView.findViewById(R.id.preset_spinner)
        val presetAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ColorScheme.Preset.values())
        presetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        presetSpinner.adapter = presetAdapter
        presetSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val preset: ColorScheme.Preset = ColorScheme.Preset.valueOf(adapterView?.getItemAtPosition(pos).toString())
                val scheme: ColorScheme by activityViewModels()
                val color1: Color = scheme.getColorAtIndex(0) ?: Color.valueOf(ContextCompat.getColor(requireContext(), R.color.meat))
                val color2: Color = scheme.getColorAtIndex(1) ?: Color.valueOf(ContextCompat.getColor(requireContext(), R.color.seeds))
                when (preset) {
                    ColorScheme.Preset.PIANO -> scheme.makePiano(color1, color2)
                    ColorScheme.Preset.GRADIENT -> scheme.makeDiagonalGrandient(color1, color2)
                    ColorScheme.Preset.CHECKER -> scheme.makeChecker(color1, color2)
                    ColorScheme.Preset.VERTICAL_STRIPE -> scheme.makeVerticalStripe(color1, color2)
                    ColorScheme.Preset.HORIZONTAL_STRIPE -> scheme.makeHorizontalStripe(color1, color2)
                    ColorScheme.Preset.MONOCHROME -> scheme.makeMonochrome(color1)
                }
                (adapterView?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(requireContext(), R.color.seeds))
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }
    private fun configureColorButtons() {
        val scheme: ColorScheme by activityViewModels()
        scheme.colorAssignment.observe(viewLifecycleOwner, Observer { assignments -> setPadColors(assignments) })
        colorButtonIds.forEachIndexed { index, buttonId ->
            val button: Button = fragmentView.findViewById(buttonId)
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