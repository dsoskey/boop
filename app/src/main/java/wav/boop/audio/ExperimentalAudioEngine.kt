package wav.boop.audio

import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ExperimentalAudioEngine(numThreads: Int = 16): AudioEngine {
    private val executor: ThreadPoolExecutor = ThreadPoolExecutor(
        numThreads, numThreads,
        5, TimeUnit.SECONDS,
        SynchronousQueue()
    )
    private val cTracks: List<ExperimentalTrackContainer> =
        List(numThreads, { ExperimentalTrackContainer(
            ExperimentalThreadedTrack(null, AudioEngine.DEFAULT_TRACK_BUILDER)
        ) })

    init {
        for (c in cTracks) {
            executor.execute(c.track)
        }
    }

    override fun play(waveform: () -> ShortArray, id: Int) {
        val trackToStart = cTracks.find { track -> track.id == null }
        trackToStart?.setInfo(id, waveform)
        trackToStart?.track?.waveform = waveform
    }

    override fun stop(id: Int) {
        val trackToStop = cTracks.find { track -> track.id == id }
        trackToStop?.clearInfo()
        trackToStop?.track?.waveform = null

    }
}