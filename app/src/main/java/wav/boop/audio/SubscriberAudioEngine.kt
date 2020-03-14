package wav.boop.audio

import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class SubscriberAudioEngine(numThreads: Int) {
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

    // Assumption, thread ids are indexes essentially
    // TODO: Make ids more robust
    // TODO: make an interface
    val tracks: MutableMap<Int, SubscriberTrack> = HashMap()
    init {
        1.rangeTo(numThreads).forEach { id ->
            val threadedTrack = SubscriberTrack(10L)
            tracks[id] = threadedTrack
        }
        startEngine()
    }

    fun startEngine() {
        tracks.values.forEach { track -> exeutor.execute(track) }
    }

    fun stopEngine() {
        tracks.values.forEach { track -> track.stop() }
    }
}