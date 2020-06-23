package wav.boop.control

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.pitch_control.*
import wav.boop.R
import wav.boop.SPINNER_DROPDOWN_ITEM_ID
import wav.boop.SPINNER_ITEM_ID
import wav.boop.pitch.NoteLetter
import wav.boop.model.PitchModel


/**
 * Controls the base pitch of the synthesizer. Contained within ControlFragment.
 */
class PitchControlFragment: Fragment() {
    private companion object {
        private const val MIN_OCTAVE = 1
        private const val MAX_OCTAVE = 8
    }
    private lateinit var tonicFrequencyEditText: EditText
    private lateinit var pitchModel: PitchModel

    @SuppressLint("SetTextI18n")
    private fun setTonicFrequencyEditText() {
        tonicFrequencyEditText.setText("%.2f".format(pitchModel.tonicFrequency))
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.pitch_control, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val model: PitchModel by activityViewModels()
        pitchModel = model

        configureNoteControls()
        configureOctaveControls()
        configureFrequencyControls()
        configureChordControls()
    }

    private fun configureNoteControls() {
        note_spinner.apply {
            val noteAdapter = ArrayAdapter(requireContext(), SPINNER_ITEM_ID, NoteLetter.values().map { it.text })
            noteAdapter.setDropDownViewResource(SPINNER_DROPDOWN_ITEM_ID)
            adapter = noteAdapter

            onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    pitchModel.setTonic(
                        noteLetter = NoteLetter.values().find { it.text == adapterView?.getItemAtPosition(pos) } ?: NoteLetter.C
                    )
                    setTonicFrequencyEditText()
                }
                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
            setSelection(0)
        }

        up_note.setOnClickListener {
            if (pitchModel.tonicNoteLetter == NoteLetter.B) {
                if (pitchModel.tonicOctave < MAX_OCTAVE) {
                    pitchModel.setTonic(NoteLetter.C, pitchModel.tonicOctave + 1)
                    note_spinner.setSelection(0)
                    octave_spinner.setSelection(octave_spinner.selectedItemPosition + 1)
                }
            } else {
                val nextNoteLetter = NoteLetter.values()[pitchModel.tonicNoteLetter.ordinal + 1]
                pitchModel.setTonic(nextNoteLetter)
                note_spinner.setSelection(nextNoteLetter.ordinal)
            }
            setTonicFrequencyEditText()
        }

        down_note.setOnClickListener {
            if (pitchModel.tonicNoteLetter == NoteLetter.C) {
                if (pitchModel.tonicOctave > MIN_OCTAVE) {
                    pitchModel.setTonic(NoteLetter.C, pitchModel.tonicOctave - 1)
                    note_spinner.setSelection(NoteLetter.values().size - 1)
                    octave_spinner.setSelection(octave_spinner.selectedItemPosition - 1)
                }
            } else {
                val nextNoteLetter = NoteLetter.values()[pitchModel.tonicNoteLetter.ordinal - 1]
                pitchModel.setTonic(nextNoteLetter)
                note_spinner.setSelection(nextNoteLetter.ordinal)
            }
            setTonicFrequencyEditText()
        }
    }

    private fun configureOctaveControls() {
        octave_spinner.apply {
            val octaveAdapter = ArrayAdapter(requireContext(), SPINNER_ITEM_ID, (MIN_OCTAVE..MAX_OCTAVE).toList())
            octaveAdapter.setDropDownViewResource(SPINNER_DROPDOWN_ITEM_ID)
            adapter = octaveAdapter

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    pitchModel.setTonic(
                        octave = adapterView?.getItemAtPosition(pos).toString().toInt()
                    )
                    setTonicFrequencyEditText()
                }
                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
            setSelection(4)
        }

        up_octave.setOnClickListener {
            if (pitchModel.tonicOctave < MAX_OCTAVE) {
                pitchModel.setTonic(octave = pitchModel.tonicOctave + 1)
                octave_spinner.setSelection(pitchModel.tonicOctave - 1)
                setTonicFrequencyEditText()
            }
        }

        down_octave.setOnClickListener {
            if (pitchModel.tonicOctave > MIN_OCTAVE) {
                pitchModel.setTonic(octave = pitchModel.tonicOctave - 1)
                octave_spinner.setSelection(pitchModel.tonicOctave - 1)
                setTonicFrequencyEditText()
            }
        }
    }

    private fun configureFrequencyControls() {
        tonicFrequencyEditText = requireView().findViewById(R.id.tonic_frequency)
        tonicFrequencyEditText.inputType = InputType.TYPE_NULL
        // TODO: Fix bug with fragment collapsing when edit text is touched
//        tonicFrequencyEditText.setOnEditorActionListener { v, actionId, _ ->
//            pitchContainer.setTonic(v.text.toString().toDouble())
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                val imm: InputMethodManager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.hideSoftInputFromWindow(v.windowToken, 0)
//                true
//            } else {
//                false
//            }
//        }
    }

    // TODO: Wire up chord controls into PitchContainer
    private fun configureChordControls() {
//        chord_spinner.apply {
//            val chordAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, Chord.values())
//            chordAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            adapter = chordAdapter
//
//            onItemSelectedListener = object:AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
//                    pitchContainer.chord = Chord.valueOf(adapterView?.getItemAtPosition(pos).toString())
//                    (adapterView?.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.seeds))
//                }
//                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
//            }
//        }
//
//        up_chord.setOnClickListener {
//            val chords = Chord.values()
//            val nextOrdinal = if (pitchContainer.chord.ordinal == chords.size - 1) {
//                0
//            } else {
//                pitchContainer.chord.ordinal + 1
//            }
//            pitchContainer.chord = chords[nextOrdinal]
//            chord_spinner.setSelection(pitchContainer.chord.ordinal)
//        }
//
//        down_chord.setOnClickListener {
//            val chords = Chord.values()
//            val nextOrdinal = if (pitchContainer.chord.ordinal == 0) {
//                chords.size - 1
//            } else {
//                pitchContainer.chord.ordinal - 1
//            }
//            pitchContainer.chord = chords[nextOrdinal]
//            chord_spinner.setSelection(pitchContainer.chord.ordinal)
//        }
    }
}