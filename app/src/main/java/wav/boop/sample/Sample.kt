package wav.boop.sample

import kotlinx.serialization.Serializable

@Serializable
data class Sample(
    val rawData: FloatArray, // Should this be a ByteArray copy of rawFile?
    val rawFileName: String, // TODO: Evaluate if this is needed in internal sample file
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