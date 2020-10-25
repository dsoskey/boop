package wav.boop.sample

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.content.pm.ActivityInfo
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.sampler_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import wav.boop.R
import wav.boop.model.OscillatorModel


class SamplerFragment: Fragment() {
    private external fun setWaveOn(oscIndex: Int, isOn: Boolean)

    companion object {
        val OPEN_SAMPLE_ID = 1

        // Left to right list of pads
        val padIds = arrayOf(
            R.id.pad_0, R.id.pad_1, R.id.pad_2, R.id.pad_3,
            R.id.pad_4, R.id.pad_5, R.id.pad_6, R.id.pad_7)
        val padToOscId = padIds.associate { it to padIds.indexOf(it) }
    }
    enum class SamplerAction {
        PLAY,
        RECORD,
        SAVE,
        LOAD
    }
    // TODO: Move to ViewModel
    var currentAction: SamplerAction = SamplerAction.PLAY

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sampler_main, container, false)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onAttach(context: Context) {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.onAttach(context)
    }

    override fun onDetach() {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onDetach()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == OPEN_SAMPLE_ID) {
//            if (resultCode == Activity.RESULT_OK) {
//                data?.data?.also { uri ->
//                    val parcelFileDescriptor: ParcelFileDescriptor =
//                }
//            }
//        }
//    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // onClick vs onTouch
        // - onClick for one-handed mode
        // - onTouch for two-handed mode
        record_button.setOnClickListener {
            if (currentAction == SamplerAction.RECORD) {
                // TODO: clear play button icons
                currentAction = SamplerAction.PLAY
                record_button.setTextColor(Color.WHITE)
            } else {
                // TODO: Set the play button icons
                currentAction = SamplerAction.RECORD
                record_button.setTextColor(Color.RED)
                save_button.setTextColor(Color.WHITE)
                load_button.setTextColor(Color.WHITE)
            }
        }

        save_button.setOnClickListener {
            if (currentAction == SamplerAction.SAVE) {
                // TODO: clear play button icons
                currentAction = SamplerAction.PLAY
                save_button.setTextColor(Color.WHITE)
            } else {
                // TODO: Set the play button icons
                currentAction = SamplerAction.SAVE
                record_button.setTextColor(Color.WHITE)
                save_button.setTextColor(Color.RED)
                load_button.setTextColor(Color.WHITE)
            }
        }

        load_button.setOnClickListener {
            if (currentAction == SamplerAction.LOAD) {
                // TODO: clear play button icons
                currentAction = SamplerAction.PLAY
                load_button.setTextColor(Color.WHITE)
            } else {
                // TODO: Set the play button icons
                currentAction = SamplerAction.LOAD
                record_button.setTextColor(Color.WHITE)
                save_button.setTextColor(Color.WHITE)
                load_button.setTextColor(Color.RED)
            }
        }

        padIds.forEach { id ->
            val button = requireView().findViewById<Button>(id)
            button.setOnTouchListener { view, event ->
                val padId = view.id
                val oscIndex = padToOscId[padId] ?: error("Missing pad")
                val oscillatorModel: OscillatorModel by activityViewModels()
                when (currentAction) {
                    SamplerAction.PLAY -> {
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                // play sample
                                setWaveOn(oscIndex, true)
                                // TODO: set sample view to show sample
                            }
                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_BUTTON_RELEASE -> {
                                // stop sample
                                setWaveOn(oscIndex, false)
                            }
                        }
                    }
                    SamplerAction.RECORD -> {
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                // disable all other buttons
                                padIds.filter { it != padId }.forEach {
                                    requireView().findViewById<Button>(it).isClickable = false
                                }
                                // start recording
                                oscillatorModel.ndkStartRecording(oscIndex)
                            }
                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_BUTTON_RELEASE -> {
                                // stop recording
                                val data: FloatArray = oscillatorModel.ndkStopRecording()
                                // save sample
                                GlobalScope.launch {
                                    val fileName = nameGenerator.generateFileName()
                                    pcmToWavFile(requireContext(), fileName, data)
                                }
                                // set sample view to show sample
                                (waveform_canvas as WaveRendererView).data = data
                                // enable all buttons
                                padIds.forEach { requireView().findViewById<Button>(it).isClickable = true }
                                // clear play button icons TODO: Do I want this
                                currentAction = SamplerAction.PLAY
                                record_button.setTextColor(Color.WHITE)
                                save_button.setTextColor(Color.WHITE)
                                load_button.setTextColor(Color.WHITE)
                            }
                        }
                    }
                    SamplerAction.SAVE -> {
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            // TODO: save file
                        } else if (
                            event.action == MotionEvent.ACTION_UP ||
                            event.action == MotionEvent.ACTION_CANCEL ||
                            event.action == MotionEvent.ACTION_BUTTON_RELEASE
                        ) {
                            // I don't think i need anything for this one
                        }
                    }
                    SamplerAction.LOAD -> {
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            // TODO: confirmation modal if sample exists for this button and not saved
                            // TODO: load file dialog
                            // This code should be enabled
//                            val filePickerIntent = Intent(ACTION_OPEN_DOCUMENT).apply {
//                                addCategory(Intent.CATEGORY_OPENABLE)
//                                type = "application/json"
//                            }
//                            startActivityForResult(filePickerIntent, OPEN_SAMPLE_ID)

                        } else if (
                            event.action == MotionEvent.ACTION_UP ||
                            event.action == MotionEvent.ACTION_CANCEL ||
                            event.action == MotionEvent.ACTION_BUTTON_RELEASE
                        ) {
                            // I don't think i need anything for this one
                        }
                    }
                }

                true
            }
        }
    }
}