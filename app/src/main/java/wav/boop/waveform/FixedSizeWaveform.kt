package wav.boop.waveform

/**
 * Wrapper class that takes a waveform generating function and manages the remainder for getting a continuous waveform of a fixed size.
 */
class FixedSizeWaveform(val getBaseWaveForm: () -> DoubleArray) {
    private var remainder: DoubleArray = doubleArrayOf()

    fun getWaveform(size: Int): DoubleArray {
        val fixedSizeWaveform = mutableListOf<Double>()
        if (remainder.size > size) {
            fixedSizeWaveform.addAll(remainder.slice(0.until(size)))
            remainder = remainder.sliceArray(size.until(remainder.size))
        } else {
            fixedSizeWaveform.addAll(0, remainder.asList()) // Performance tuning??? Wait till it works first
            while (fixedSizeWaveform.size < size) {
                val baseWaveform = getBaseWaveForm()
                val sizeToFill = size - fixedSizeWaveform.size
                if (baseWaveform.size > sizeToFill) {
                    // Add beginning of slice and set new remainder to end of slice
                    fixedSizeWaveform.addAll(baseWaveform.slice(0.until(sizeToFill)))
                    remainder = baseWaveform.sliceArray(sizeToFill.until(baseWaveform.size))
                } else {
                    // Add full slice to waveform
                    fixedSizeWaveform.addAll(baseWaveform.toList())
                }
            }
        }
        return fixedSizeWaveform.toDoubleArray()
    }
}