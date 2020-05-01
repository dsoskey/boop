package wav.boop.visualisation

import java.util.*

// TODO If needed: Move details of queue management to different object

/**
 * Manages a Fixed-size Queue of frequencies (Double?s)
 *   IN - AudioEngine handles pushing frequencies it has queued up for playing.
 *  OUT - ClassicOscilloscopeFragment polls for currentModel to display
 */
class HistoricalOscilloscope(val size: Int = DEFAULT_SAMPLE_RATE_IN_SECONDS * 20): Oscilloscope {
    private val queue: LinkedList<Double?> = LinkedList()

    init {
        for (i in 0.until(size)) {
            queue.push(null)
        }
    }

    override fun getCurrentModel(): DoubleArray {
        val nonNulls = queue.filterNotNull()
        val median = nonNulls.sorted().let {
            if (it.isEmpty()) {
                0.0
            } else if (it.size % 2 == 0) {
                (it[it.size / 2] + it[(it.size - 1) / 2]) / 2
            } else {
                it[it.size / 2]
            }
        }
        return queue.map { value: Double? ->
            when (value) {
                // Nulls are converted to median to put them at center when normalized
                null -> median
                else -> value
            }
        }.toDoubleArray()
    }

    fun push(value: Double?) {
        if (queue.size >= size) {
            queue.pop()
        }
        queue.push(value)
    }
}