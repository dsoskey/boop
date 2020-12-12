package wav.boop.sample

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import wav.boop.file.SerialLoader

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
     * TODO: proper error throwing when race condition is hit instead of returning empty list
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