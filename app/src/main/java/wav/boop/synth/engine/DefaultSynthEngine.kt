package wav.boop.synth.engine

import wav.boop.waveform.SineEngine
import wav.boop.waveform.WaveformEngine
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class DefaultSynthEngine(
    numThreads: Int,
    val refreshRate: Long,
    val sampleRate: Int,
    val defaultWaveformEngine: WaveformEngine = SineEngine()
): SynthEngine {
    init {
        assert(numThreads > 0)
    }
    private val exeutor: ThreadPoolExecutor = ThreadPoolExecutor(
        numThreads,
        numThreads,
        5,
        TimeUnit.SECONDS,
        SynchronousQueue()
    )
    private val tracks: MutableMap<Int, ThreadedTrack> = HashMap()
    init {
        1.rangeTo(numThreads).forEach { id ->
            val threadedTrack = ThreadedTrack(refreshRate, sampleRate, defaultWaveformEngine)
            tracks[id] = threadedTrack
        }
        startEngine()
    }

    override fun startEngine() {
        tracks.values.forEach { track -> exeutor.execute(track) }
    }

    override fun stopEngine() {
        tracks.values.forEach { track -> track.stop() }
    }

    override fun getTrackIds(): IntArray = tracks.keys.toIntArray()
    override fun subscribeToTrack(trackId: Int, listener: (waveform: DoubleArray) -> Unit): Int {
        if (tracks.containsKey(trackId)) {
            val subscriberId = Random.nextInt(1, Int.MAX_VALUE)
            tracks[trackId]!!.subscribers[subscriberId] = listener
            return subscriberId
        }
        return 0 // TODO: Improve error handling and not return a zero for fail case
    }

    override fun unsubscribeFromTrack(trackId: Int, listenerId: Int) {
        if (tracks.containsKey(trackId)) {
            tracks[trackId]!!.subscribers.remove(listenerId)
        }
    }

    override fun setWaveform(trackId: Int, waveform: WaveformEngine) {
        if (tracks.containsKey(trackId)) {
            tracks[trackId]!!.waveformEngine = waveform
        }
    }

    override fun playTrack(trackId: Int, frequency: Double) {
        if (tracks.containsKey(trackId)) {
            tracks[trackId]!!.play(frequency)
        }
    }

    override fun stopTrack(trackId: Int) {
        if (tracks.containsKey(trackId)) {
            tracks[trackId]!!.pause()
        }
    }
}