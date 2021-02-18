package wav.boop.sample

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import wav.boop.file.SerialLoader
import kotlin.collections.HashMap

/**
 * Model that manages all interactions with the Sampler's data.
 */
class SamplerModel(
    private val padToChannelIndex: Map<Int, Int>,
    private val context: Context,
    private val sampleLoader: SerialLoader<Sample>
): ViewModel() {
    companion object {
        const val AUTOSAVE_PREFIX = "autozone"

        fun autoSaveFileName(channelIndex: Int): String { return "${AUTOSAVE_PREFIX}_${channelIndex}" }
    }

    // Native interface
    private external fun ndkStartRecording(channelIndex: Int)
    private external fun ndkStopRecording(): FloatArray
    private external fun ndkSetSample(channelIndex: Int, sample: FloatArray)
    private external fun ndkSetSampleOn(channelIndex: Int, isOn: Boolean)
    private external fun ndkSetSampleStart(channelIndex: Int, startFrame: Int)
    private external fun ndkSetSampleEnd(channelIndex: Int, endFrame: Int)
    private external fun ndkSetSampleAmplitude(channelIndex: Int, amplitude: Float)

    /** Map of Channel Index -> Sample loaded onto Channel. */
    private val loadedSamples: MutableMap<Int, Savable<Sample>> = HashMap()

    /** Index of channel currently being recorded to. */
    private var currentRecordingChannelIndex: Int? = null // Beware, this might have a race condition

    /**
     * Restores any autosaves located in the sampleLoader's subdirectory.
     * NOTE: This will overwrite any currently loaded samples, so use with care.
     */
    fun loadAutosaves() {
        padToChannelIndex.values.forEach {
            val sample = sampleLoader.get(autoSaveFileName(it))
            if (sample != null) {
                loadedSamples[it] = Savable(false, sample) // TODO: Might need to store the savable wrapper too
                ndkSetSample(it, sample.rawData)
                ndkSetSampleStart(it, sample.startFrame)
                ndkSetSampleEnd(it, sample.endFrame)
                ndkSetSampleAmplitude(it, sample.amplitude)
            }
        }
    }

    /** map of channelIndex --> currently running autosave job */
    private val currentAutosaveJobs: MutableMap<Int, Job?> = HashMap()
    private fun startAutosave(channelIndex: Int, sample: Savable<Sample>) {
        if (currentAutosaveJobs[channelIndex] == null) {
            currentAutosaveJobs[channelIndex] = viewModelScope.launch(Dispatchers.IO) {
                sampleLoader.save(autoSaveFileName(channelIndex), sample.data)
                currentAutosaveJobs[channelIndex] = null
            }
        }
    }

    /**
     * Starts recording for a channel tied to a specific pad.
     */
    fun startRecording(padIndex: Int) {
        val channelIndex = padToChannelIndex[padIndex]
        if (channelIndex != null && currentRecordingChannelIndex == null) {
            ndkStartRecording(channelIndex)
            currentRecordingChannelIndex = channelIndex
        }
    }

    /**
     * If there is a channel currently recording input, this function
     * - stops recording
     * - sets the loaded sample
     * - saves a raw version of the sample as a wav
     * - saves the active sample for restoring when app restarts
     */
    fun stopRecording() {
        val channelIndex = currentRecordingChannelIndex
        if (channelIndex != null) {
            val sampleData: FloatArray = ndkStopRecording()
            val fileName = nameGenerator.generateFileName()
            val sample = Savable(false, Sample(sampleData, fileName, 0, sampleData.size, false, 0 , sampleData.size))
            loadedSamples[channelIndex] = sample
            GlobalScope.launch {
                // Save raw data to a wav
                pcmToWavFile(context, fileName, sampleData)
                // Save active sample to restore when app restarts.
                sampleLoader.save(autoSaveFileName(channelIndex), sample.data)
            }
            currentRecordingChannelIndex = null
        } else {
            throw RuntimeException("stopRecording called without calling startRecording")
        }
    }

    /**
     * Gets a loaded sample based on the pad index, if it exists.
     */
    fun getSample(padIndex: Int): Savable<Sample>? {
        return loadedSamples[padToChannelIndex[padIndex]]
    }

    /**
     * Sets a sample to play on/off.
     */
    fun setSampleOn(padIndex: Int, isOn: Boolean) {
        val channelIndex = padToChannelIndex[padIndex]
        if (channelIndex != null) {
            ndkSetSampleOn(channelIndex, isOn)
        }
    }

    fun setSampleStartFrame(padIndex: Int, startFrame: Int) {
        val channelIndex = padToChannelIndex[padIndex] ?: error("padIndex not found in map: $padToChannelIndex")
        val sample = loadedSamples[channelIndex]!!
        if (startFrame < sample.data.endFrame) {
            sample.data.startFrame = startFrame
            startAutosave(channelIndex, sample)
            ndkSetSampleStart(channelIndex, startFrame)
        }
    }

    fun setSampleEndFrame(padIndex: Int, endFrame: Int) {
        val channelIndex = padToChannelIndex[padIndex] ?: error("padIndex not found in map: $padToChannelIndex")
        val sample = loadedSamples[channelIndex]!!
        if (endFrame > sample.data.startFrame) {
            sample.data.endFrame = endFrame
            startAutosave(channelIndex, sample)
            ndkSetSampleEnd(channelIndex, endFrame)
        }
    }

    fun setSampleAmplitude(padIndex: Int, amplitude: Float) {
        val channelIndex = padToChannelIndex[padIndex] ?: error("padIndex not found in map: $padToChannelIndex")
        val sample = loadedSamples[channelIndex]!!
        sample.data.amplitude = amplitude
        startAutosave(channelIndex, sample)
        ndkSetSampleAmplitude(channelIndex, amplitude)
    }
}

/**
 * Factory to allow Model to have a non-empty constructor.
 */
class SamplerModelFactory(
    private val padToChannelIndex: Map<Int, Int>,
    private val context: Context,
    private val sampleLoader: SerialLoader<Sample>
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(
            Map::class.java,
            Context::class.java,
            SerialLoader::class.java
        ).newInstance(padToChannelIndex, context, sampleLoader)
    }
}