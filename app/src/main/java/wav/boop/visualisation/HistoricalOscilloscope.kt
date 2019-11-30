package wav.boop.visualisation

import wav.boop.DEFAULT_SAMPLE_RATE_IN_SECONDS
import java.util.*
import kotlin.concurrent.thread

// TODO If needed: Move details of queue management to different object

/**
 * Manages a Fixed-size Queue of frequencies (Double?s)
 *   IN - DefaultAudioEngine handles pushing frequencies it has queued up for playing.
 *  OUT - ClassicOscilloscopeFragment polls for currentModel to display
 */
class HistoricalOscilloscope(val size: Int = DEFAULT_SAMPLE_RATE_IN_SECONDS * 20, private val pollingFrequency: Int = 1): Oscilloscope {
    private val history: LinkedList<Double?> = LinkedList()
    private val futureEvents: LinkedList<Double> = LinkedList()
    private val collector: Thread

    val onHistoryChanged: MutableList<(DoubleArray) -> Unit> = mutableListOf()

    init {
        for (i in 0.until(size)) {
            history.push(null)
        }
        collector = thread(true) {
            while (true) {
                pushHistory(futureEvents.poll())
                Thread.sleep(pollingFrequency.toLong())
                println("Collect!")
            }
        }
    }

    override fun getCurrentModel(): DoubleArray {
        val nonNulls = history.filterNotNull()
        val median = nonNulls.sorted().let {
            if (it.isEmpty()) {
                0.0
            } else if (it.size % 2 == 0) {
                (it[it.size / 2] + it[(it.size - 1) / 2]) / 2
            } else {
                it[it.size / 2]
            }
        }
        return history.map { value: Double? ->
            when (value) {
                // Nulls are converted to median to put them at center when normalized
                null -> median
                else -> value
            }
        }.toDoubleArray()
    }

    private fun pushHistory(value: Double?) {
        if (history.size >= size) {
            println("Historical!")
            history.pop()
            onHistoryChanged.forEach{it(getCurrentModel())}
        }
        history.push(value)
    }

    fun pushFutureEvent(value: Double) {
        futureEvents.push(value)
    }
}