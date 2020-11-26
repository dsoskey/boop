package wav.boop.sample

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class Sample(
    val rawData: FloatArray, // Should this be a ByteArray copy of rawFile?
    val rawFileName: String, // Evaluate if this is needed in internal sample file
    var startFrame: Int,
    var endFrame: Int,
    var isLooping: Boolean,
    var startLoopFrame: Int,
    var endLoopFrame: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sample

        if (!rawData.contentEquals(other.rawData)) return false
        if (rawFileName != other.rawFileName) return false
        if (startFrame != other.startFrame) return false
        if (endFrame != other.endFrame) return false
        if (isLooping != other.isLooping) return false
        if (startLoopFrame != other.startLoopFrame) return false
        if (endLoopFrame != other.endLoopFrame) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rawData.contentHashCode()
        result = 31 * result + rawFileName.hashCode()
        result = 31 * result + startFrame
        result = 31 * result + endFrame
        result = 31 * result + isLooping.hashCode()
        result = 31 * result + startLoopFrame
        result = 31 * result + endLoopFrame
        return result
    }
}

class SamplerModel(private val padToChannelIndex: Map<Int, Int>, private val context: Context): ViewModel() {
    // Native interface
    private external fun ndkStartRecording(channelIndex: Int)
    private external fun ndkStopRecording(): FloatArray
    private external fun ndkSetSample(channelIndex: Int, sample: FloatArray)
    private external fun ndkSetSampleOn(channelIndex: Int, isOn: Boolean)

    private val loadedSamples: MutableMap<Int, Savable<Sample>> = HashMap()
    private var currentRecordingChannelIndex: Int? = null // Beware, this might have a race condition

    fun startRecording(padIndex: Int) {
        val channelIndex = padToChannelIndex[padIndex]
        if (channelIndex != null && currentRecordingChannelIndex == null) {
            ndkStartRecording(channelIndex)
            currentRecordingChannelIndex = channelIndex
        }
    }

    // TODO: proper error throwing when race condition is hit instead of returning empty list
    fun stopRecording() {
        val channelIndex = currentRecordingChannelIndex
        if (channelIndex != null) {
            val sampleData: FloatArray = ndkStopRecording()
            val fileName = nameGenerator.generateFileName()
            val sample = Savable(false, Sample(sampleData, fileName, 0, sampleData.size, false, 0 , sampleData.size))
            loadedSamples[channelIndex] = sample
            GlobalScope.launch {
                pcmToWavFile(context, fileName, sampleData)
            }
            currentRecordingChannelIndex = null
        }
    }

    fun getSample(padIndex: Int): Savable<Sample>? {
        return loadedSamples[padToChannelIndex[padIndex]]
    }

    fun setSampleOn(padIndex: Int, isOn: Boolean) {
        val channelIndex = padToChannelIndex[padIndex]
        if (channelIndex != null) {
            ndkSetSampleOn(channelIndex, isOn)
        }
    }
}

class SamplerModelFactory(private val padToChannelIndex: Map<Int, Int>, private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Map::class.java, Context::class.java).newInstance(padToChannelIndex, context)
    }
}