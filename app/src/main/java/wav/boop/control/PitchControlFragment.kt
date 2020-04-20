package wav.boop.control

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.pitch_control.*
import wav.boop.R
import wav.boop.pitch.Chord
import wav.boop.pitch.NoteLetter
import wav.boop.model.PitchContainer
import wav.boop.pad.padToOscillator


class PitchControlFragment: Fragment() {
    lateinit var fragmentView: View
    lateinit var tonicFrequencyEditText: EditText
    lateinit var pitchContainer: PitchContainer
    private val minOctave = 1
    private val maxOctave = 8

    @SuppressLint("SetTextI18n")
    private fun setTonicFrequencyEditText() {
        tonicFrequencyEditText.setText("%.2f".format(pitchContainer.tonicFrequency))
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.pitch_control, container, false)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val container: PitchContainer by activityViewModels()
        pitchContainer = container

        configureNoteControls()
        configureOctaveControls()
        configureFrequencyControls()
        configureChordControls()
    }

    private fun configureNoteControls() {
        val noteSpinner: Spinner = fragmentView.findViewById(R.id.note_spinner)
        val noteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, NoteLetter.values())
        noteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        noteSpinner.adapter = noteAdapter
        noteSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                pitchContainer.setTonic(
                    noteLetter = NoteLetter.valueOf(adapterView?.getItemAtPosition(pos).toString())
                )
                setTonicFrequencyEditText()
                (adapterView?.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.seeds))
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        noteSpinner.setSelection(0)

        val upButton: Button = fragmentView.findViewById(R.id.up_note)
        upButton.setOnClickListener {
            if (pitchContainer.tonicNoteLetter == NoteLetter.B) {
                if (pitchContainer.tonicOctave < maxOctave) {
                    pitchContainer.setTonic(NoteLetter.C, pitchContainer.tonicOctave + 1)
                    noteSpinner.setSelection(0)
                    octave_spinner.setSelection(octave_spinner.selectedItemPosition + 1)
                }
            } else {
                val nextNoteLetter = NoteLetter.values()[pitchContainer.tonicNoteLetter.ordinal + 1]
                pitchContainer.setTonic(nextNoteLetter)
                noteSpinner.setSelection(nextNoteLetter.ordinal)
            }
            setTonicFrequencyEditText()
        }

        val downButton: Button = fragmentView.findViewById(R.id.down_note)
        downButton.setOnClickListener {
            if (pitchContainer.tonicNoteLetter == NoteLetter.C) {
                if (pitchContainer.tonicOctave > minOctave) {
                    pitchContainer.setTonic(NoteLetter.C, pitchContainer.tonicOctave - 1)
                    noteSpinner.setSelection(NoteLetter.values().size - 1)
                    octave_spinner.setSelection(octave_spinner.selectedItemPosition - 1)
                }
            } else {
                val nextNoteLetter = NoteLetter.values()[pitchContainer.tonicNoteLetter.ordinal - 1]
                pitchContainer.setTonic(nextNoteLetter)
                noteSpinner.setSelection(nextNoteLetter.ordinal)
            }
            setTonicFrequencyEditText()
        }
    }

    private fun configureOctaveControls() {
        val octaveSpinner: Spinner = fragmentView.findViewById(R.id.octave_spinner)
        val octaveAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, (minOctave..maxOctave).toList())
        octaveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        octaveSpinner.adapter = octaveAdapter
        octaveSpinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                pitchContainer.setTonic(
                    octave = adapterView?.getItemAtPosition(pos).toString().toInt()
                )
                setTonicFrequencyEditText()
                (adapterView?.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.seeds))
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        octaveSpinner.setSelection(4)

        val upButton: Button = fragmentView.findViewById(R.id.up_octave)
        upButton.setOnClickListener {
            if (pitchContainer.tonicOctave < maxOctave) {
                pitchContainer.setTonic(octave = pitchContainer.tonicOctave + 1)
                octaveSpinner.setSelection(pitchContainer.tonicOctave - 1)
                setTonicFrequencyEditText()
            }
        }

        val downButton: Button = fragmentView.findViewById(R.id.down_octave)
        downButton.setOnClickListener {
            if (pitchContainer.tonicOctave > minOctave) {
                pitchContainer.setTonic(octave = pitchContainer.tonicOctave - 1)
                octaveSpinner.setSelection(pitchContainer.tonicOctave - 1)
                setTonicFrequencyEditText()
            }
        }
    }

    private fun configureFrequencyControls() {
        tonicFrequencyEditText = fragmentView.findViewById(R.id.tonic_frequency)
        tonicFrequencyEditText.setOnEditorActionListener { v, actionId, _ ->
            pitchContainer.setTonic(v.text.toString().toDouble())
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm: InputMethodManager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else {
                false
            }
        }
    }

    private fun configureChordControls() {
        val chordSpinner: Spinner = fragmentView.findViewById(R.id.chord_spinner)
        val chordAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, Chord.values())
        chordAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        chordSpinner.adapter = chordAdapter
        chordSpinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                pitchContainer.chord = Chord.valueOf(adapterView?.getItemAtPosition(pos).toString())
                (adapterView?.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.seeds))
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        val upButton: Button = fragmentView.findViewById(R.id.up_chord)
        upButton.setOnClickListener {
            val chords = Chord.values()
            val nextOrdinal = if (pitchContainer.chord.ordinal == chords.size - 1) {
                0
            } else {
                pitchContainer.chord.ordinal + 1
            }
            pitchContainer.chord = chords[nextOrdinal]
            chord_spinner.setSelection(pitchContainer.chord.ordinal)
        }

        val downButton: Button = fragmentView.findViewById(R.id.down_chord)
        downButton.setOnClickListener {
            val chords = Chord.values()
            val nextOrdinal = if (pitchContainer.chord.ordinal == 0) {
                chords.size - 1
            } else {
                pitchContainer.chord.ordinal - 1
            }
            pitchContainer.chord = chords[nextOrdinal]
            chord_spinner.setSelection(pitchContainer.chord.ordinal)
        }
    }
}