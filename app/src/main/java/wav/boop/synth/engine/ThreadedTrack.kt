package wav.boop.synth.engine

import wav.boop.waveform.FixedSizeWaveform
import wav.boop.waveform.WaveformEngine

class ThreadedTrack(
    private val refreshRate: Long,
    private val sampleRate: Int,
    var waveformEngine: WaveformEngine): Runnable {
    // must be read by main thread un-cached
    @Volatile
    var isRunning: Boolean = false
        private set
    private var isPlaying: Boolean = false
    private var fixedSizeWaveform: FixedSizeWaveform? = null
    private val emptyWave = DoubleArray(sampleRate, {0.0})
    val subscribers: MutableMap<Int, (waveform: DoubleArray) -> Unit> = HashMap()

    override fun run() {
        isRunning = true
        while(isRunning) {
            renderAndSendWaveform()
            Thread.sleep(refreshRate)
        }
    }

    private fun renderAndSendWaveform() {
        val waveform = if (isPlaying) {
            // fixed size waveform is always set before isPlaying is set for the first time
            fixedSizeWaveform!!.getWaveform(sampleRate)//
        } else {
            emptyWave
        }
        subscribers.values.forEach { action -> action(waveform) }
    }

    fun play(frequency: Double) {
        fixedSizeWaveform = FixedSizeWaveform(waveformEngine.getWaveform(frequency))
        isPlaying = true
    }
    fun pause() {
        isPlaying = false
    }
    fun stop() {
        isRunning = false
    }
}