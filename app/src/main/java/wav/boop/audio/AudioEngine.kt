package wav.boop.audio

import java.util.HashMap
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class AudioEngine(numThreads: Int) {

    private val executor: ThreadPoolExecutor = ThreadPoolExecutor(
        numThreads, numThreads,
        5, TimeUnit.SECONDS,
        SynchronousQueue()
    )
    private val playingTracks: MutableMap<Int, ThreadedTrack> = HashMap()

    private fun startTrackThread(waveform: () -> ShortArray): ThreadedTrack {
        val threadedTrack = ThreadedTrack(waveform)
        executor.execute(threadedTrack)
        return threadedTrack
    }

    fun play(waveform: () -> ShortArray, id: Int) {
//        if (playingTracks.size < executor.maximumPoolSize) { todo FIGURE OUT HOW TO HANDLE MORE THAN MAX POOL SIZE
            val track = startTrackThread(waveform)
            playingTracks[id] = track
//        }
    }

    fun stop(id: Int) {
        while (playingTracks.containsKey(id)) {
            val track = playingTracks[id]!!
            if (track.isPlaying) {
                track.stop()
                playingTracks.remove(id)
            }
        }
    }
}
