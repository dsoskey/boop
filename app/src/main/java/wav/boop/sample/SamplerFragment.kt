package wav.boop.sample

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_CREATE_DOCUMENT
import android.content.Intent.ACTION_OPEN_DOCUMENT
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
        // Left to right list of pads
        val padIds = arrayOf(
            R.id.pad_0, R.id.pad_1, R.id.pad_2, R.id.pad_3,
            R.id.pad_4, R.id.pad_5, R.id.pad_6, R.id.pad_7)
        val padToOscId = padIds.associate { it to padIds.indexOf(it) }
        val oscToPadId = padIds.associateBy { padIds.indexOf(it) }
        const val OPCODE_LOAD = 1
        const val OPCODE_SAVE = 2
        val JSON = Json(JsonConfiguration.Stable)
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

    /**
     * Handles intent for saving/loading samples.
     * @param requestCode - code containing logic instructions for activity result
     * request code structure: 3+ digit number: O+PP
     * (O) Op Code: which action to take
     *  - 1: Load sample to oscId
     *  - 2: Save oscId's sample to file
     * (P) oscId: which pad to take the action for
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val opCode = requestCode / 100
        val oscId = requestCode % 100
        if (padToOscId.values.contains(oscId)) {
            when (opCode) {
                OPCODE_LOAD -> {
                    if (resultCode == Activity.RESULT_OK) {
                        data?.data?.also { uri ->
                            requireContext().applicationContext.contentResolver.openInputStream(uri)?.use { inputStream ->
                                // For now, assume it's json of a Sample
                                val sample: Sample = JSON.parse(Sample.serializer(), inputStream.bufferedReader().use { it.readText() })
                                samplerModel.setSample(oscId, sample)
                                val waveRendererView = (waveform_canvas as WaveRendererView)
                                val padId = oscToPadId[oscId]!!
                                waveRendererView.loadData(padId, sample.rawData)
                                waveRendererView.renderData(padId)
                                setPadIcons()
                            }
                        }
                    }
                }
                OPCODE_SAVE -> {
                    if (resultCode == Activity.RESULT_OK) {
                        val sample = samplerModel.getSample(oscId)
                        if (sample != null) {
                            data?.data?.also { uri ->
                                // Does this need to run on a separate thread or does it already run off the IO thread
                                requireContext().applicationContext.contentResolver.openOutputStream(uri)?.use { outputStream ->
                                    outputStream.bufferedWriter().use { it.write(JSON.stringify(Sample.serializer(), sample.data)) }
                                }
                                sample.isSaved = true
                                setPadIcons()

                            }
                        }
                    }
                }
            }

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sampleLoader = buildSampleLoader(requireContext(), JSON)
        val samplerFactory = SamplerModelFactory(requireContext(), sampleLoader)
        samplerModel = ViewModelProvider(requireActivity(), samplerFactory)[SamplerModel::class.java]
        samplerModel.loadAutosaves(padToOscId.values)
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
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser && currentChoppingChannelIndex != null) {
                    samplerModel.setSampleStartFrame(currentChoppingChannelIndex!!, progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        endBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser && currentChoppingChannelIndex != null) {
                    samplerModel.setSampleEndFrame(currentChoppingChannelIndex!!, progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        sample_amplitude.apply {
            maxValue = 100
            setOnProgressChangeListener {
                if (currentChoppingChannelIndex != null) {
                    val amplitude: Float = it / 100f
                    samplerModel.setSampleAmplitude(currentChoppingChannelIndex!!, amplitude)
                }
            }
        }

        padIds.forEach { id ->
            val button = requireView().findViewById<ImageButton>(id)
            button.setOnTouchListener { view, event ->
                val padId = view.id
                val channelIndex = getChannelIndex(padId)
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
                                samplerModel.setSampleOn(channelIndex, true)
                                // set sample view to show sample.
                                val sample = samplerModel.getSample(channelIndex)
                                val waveRendererView = (waveform_canvas as WaveRendererView)
                                // Setting this var needs to happen before setting the progress on amplitude seekbar,
                                // the vertical seekbar can't tell the difference between a user and programmatic progress change
                                currentChoppingChannelIndex = channelIndex
                                if (sample != null) {
                                    if (!waveRendererView.hasData(padId)) {
                                        waveRendererView.loadData(padId, sample.data.rawData)
                                    }
                                    setSampleChopperBars(sample)
                                }
                                waveRendererView.renderData(padId)
                            }
                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_BUTTON_RELEASE -> {
                                // For now I'm letting the sample run out instead of turning off on release
                                // TODO: In the future this could be configurable.
                                // stop sample
                                // samplerModel.setSampleOn(channelIndex, false)
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
                                samplerModel.startRecording(channelIndex)
                            }
                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_BUTTON_RELEASE -> {
                                // stop recording and save raw sample
                                samplerModel.stopRecording()
                                // set sample view to show sample
                                val sample = samplerModel.getSample(channelIndex)
                                if (sample != null) {
                                    val waveRendererView = (waveform_canvas as WaveRendererView)
                                    waveRendererView.loadData(padId, sample.data.rawData)
                                    waveRendererView.renderData(padId)
                                    currentChoppingChannelIndex = channelIndex
                                    setSampleChopperBars(sample)
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
                            val fileSaverIntent = Intent(ACTION_CREATE_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "application/json"
                            }
                            startActivityForResult(fileSaverIntent, 100 * OPCODE_SAVE + channelIndex)
                        }
                    }
                    SamplerAction.LOAD -> {
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            val filePickerIntent = Intent(ACTION_OPEN_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "application/json" // TODO: Figure out a way to handle all kinds of audio files, including a custom spec that contains sample metadata
                            }
                            startActivityForResult(filePickerIntent, 100 * OPCODE_LOAD + channelIndex)
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
        sample_amplitude.progress = (sample.data.amplitude * 100).toInt()
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
        padToOscId.forEach { pair ->
            val padId = pair.key
            val oscId = pair.value
            val sample = samplerModel.getSample(oscId)
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
    private fun getChannelIndex(padIndex: Int): Int {
        return padToOscId[padIndex] ?: error("$padIndex not found in map: $padToOscId")
    }
}