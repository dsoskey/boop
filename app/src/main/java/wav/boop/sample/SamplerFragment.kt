package wav.boop.sample

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.sampler_main.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import wav.boop.R
import wav.boop.file.buildSampleLoader


class SamplerFragment: Fragment() {
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
    var currentAction: SamplerAction = SamplerAction.PLAY

    /** Index of channel currently being chopped. */
    private var currentChoppingChannelIndex: Int? = null

    lateinit var samplerModel: SamplerModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sampler_main, container, false)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onAttach(context: Context) {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.onAttach(context)
    }

    @SuppressLint("SourceLockedOrientationActivity")
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

        val sampleLoader = buildSampleLoader(requireContext(), Json(JsonConfiguration.Stable))
        val samplerFactory = SamplerModelFactory(padToOscId, requireContext(), sampleLoader)
        samplerModel = ViewModelProvider(requireActivity(), samplerFactory)[SamplerModel::class.java]
        samplerModel.loadAutosaves()
        // onClick vs onTouch
        // - onClick for one-handed mode
        // - onTouch for two-handed mode
        record_button.setOnClickListener {
            if (currentAction == SamplerAction.RECORD) {
                currentAction = SamplerAction.PLAY

                clearPads()

                setRecordOff()
            } else {
                currentAction = SamplerAction.RECORD

                setPadIcons()

                setRecordOn()
                setSaveOff()
                setLoadOff()
            }
        }

        save_button.setOnClickListener {
            if (currentAction == SamplerAction.SAVE) {
                currentAction = SamplerAction.PLAY

                clearPads()

                setSaveOff()
            } else {
                currentAction = SamplerAction.SAVE

                setPadIcons()

                setRecordOff()
                setSaveOn()
                setLoadOff()
            }
        }

        load_button.setOnClickListener {
            if (currentAction == SamplerAction.LOAD) {
                currentAction = SamplerAction.PLAY

                clearPads()

                setLoadOff()
            } else {
                currentAction = SamplerAction.LOAD

                setPadIcons()

                setRecordOff()
                setSaveOff()
                setLoadOn()
            }
        }

        startBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    samplerModel.setSampleStartFrame(currentChoppingChannelIndex!!, progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        endBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    samplerModel.setSampleEndFrame(currentChoppingChannelIndex!!, progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        padIds.forEach { id ->
            val button = requireView().findViewById<ImageButton>(id)
            button.setOnTouchListener { view, event ->
                val padId = view.id
                when (currentAction) {
                    SamplerAction.PLAY -> {
                        /**
                         * TODO: Decide how to handle buttons that haven't been loaded yet
                         * 1. Disable the pads and don't play anything if they are disabled.
                         * 2. Don't disable them, pre-fill each pad with noise at model level in addition to synthesizer
                         * 3. Don't disable them, pre-fill with set of default samples.
                         */
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                // play sample
                                samplerModel.setSampleOn(padId, true)
                                // set sample view to show sample.
                                val sample = samplerModel.getSample(padId)
                                val waveRendererView = (waveform_canvas as WaveRendererView)
                                if (sample != null) {
                                    if (!waveRendererView.hasData(padId)) {
                                        waveRendererView.loadData(padId, sample.data.rawData)
                                    }
                                    setSampleChopperBars(sample)
                                }
                                waveRendererView.renderData(padId)
                                currentChoppingChannelIndex = padId
                            }
                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_BUTTON_RELEASE -> {
                                // For now I'm letting the sample run out instead of turning off on release
                                // TODO: In the future this could be configurable.
                                // stop sample
//                                samplerModel.setSampleOn(padId, false)
                            }
                        }
                    }
                    SamplerAction.RECORD -> {
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                // disable all other buttons
                                padIds.filter { it != padId }.forEach {
                                    requireView().findViewById<ImageButton>(it).isClickable = false
                                }
                                // start recording
                                samplerModel.startRecording(padId)
                            }
                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_BUTTON_RELEASE -> {
                                // stop recording and save raw sample
                                samplerModel.stopRecording()
                                // set sample view to show sample
                                val sample = samplerModel.getSample(padId)
                                if (sample != null) {
                                    val waveRendererView = (waveform_canvas as WaveRendererView)
                                    waveRendererView.loadData(padId, sample.data.rawData)
                                    waveRendererView.renderData(padId)
                                    setSampleChopperBars(sample)
                                    currentChoppingChannelIndex = padId
                                }
                                currentAction = SamplerAction.PLAY
                                clearPads()
                                setRecordOff()
                                setSaveOff()
                                setLoadOff()
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

    private fun setSampleChopperBars(sample: Savable<Sample>) {
        startBar.apply {
            max = sample.data.rawData.size
            progress = sample.data.startFrame
        }
        endBar.apply {
            max = sample.data.rawData.size
            progress = sample.data.endFrame
        }
    }
    private fun setRecordOn() {
        record_button.setTextColor(resources.getColor(R.color.temperedSienna))
        record_button.setBackgroundColor(Color.WHITE)
    }
    private fun setRecordOff() {
        record_button.setTextColor(Color.WHITE)
        record_button.setBackgroundColor(resources.getColor(R.color.temperedSienna))
    }
    private fun setSaveOn() {
        save_button.setTextColor(resources.getColor(R.color.temperedSienna))
    }
    private fun setSaveOff() {
        save_button.setTextColor(Color.WHITE)
    }
    private fun setLoadOn() {
        load_button.setTextColor(resources.getColor(R.color.temperedSienna))
    }
    private fun setLoadOff() {
        load_button.setTextColor(Color.WHITE)

    }
    private fun clearPads(){
        padIds.forEach { padId ->
            requireView().findViewById<ImageButton>(padId).apply {
                isClickable = true
                setImageDrawable(null)
            }
        }
    }
    private fun setPadIcons() {
        padIds.forEach { padId ->
            val sample = samplerModel.getSample(padId)
            if (sample != null) {
                val button = requireView().findViewById<ImageButton>(padId)
                button.setImageResource(
                    if (sample.isSaved) {
                        R.drawable.sample_icon_saved
                    } else {
                        R.drawable.sample_icon_unsaved
                    }
                )
            }
        }
    }
}