package wav.boop.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.preset_control.*
import wav.boop.R
import wav.boop.model.SynthesizerModel


class PresetControlFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.preset_control, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val synthModel: SynthesizerModel by activityViewModels()

        preset_filename.apply {
            setText(synthModel.currentFileName)
            val presetAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, synthModel.getLoadedPresets())
            presetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            setAdapter(presetAdapter)
            threshold = 0
        }

        save_button.apply {
            setOnClickListener {
                val fileName = preset_filename.text.toString()
                synthModel.saveCurrentPreset(fileName)
                Toast.makeText(context, "Saved preset: $fileName", LENGTH_SHORT).show()
            }
        }

        load_button.apply {
            setOnClickListener {
                val fileName = preset_filename.text.toString()
                val loaded = synthModel.loadPreset(fileName)
                val toastText = if (loaded) {
                    "Loaded preset: $fileName"
                } else {
                    "Could not find preset: $fileName"
                }
                Toast.makeText(context, toastText, LENGTH_SHORT).show()
            }
        }
    }
}