package wav.boop.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.HashMap
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class AudioEngine(numThreads: Int) {

    private val trackBuilder: AudioTrack.Builder
    private val executor: ThreadPoolExecutor

    private val playingTracks: MutableMap<Int, ThreadedTrack>

    init {
        trackBuilder = DEFAULT_TRACK_BUILDER
        playingTracks = HashMap()
        executor = ThreadPoolExecutor(
            numThreads, numThreads,
            5, TimeUnit.SECONDS,
            SynchronousQueue()
        )
    }

    private fun startTrackThread(waveform: () -> ShortArray): ThreadedTrack {
        val threadedTrack = ThreadedTrack(waveform, trackBuilder)
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

    companion object {

        val DEFAULT_TRACK_BUILDER: AudioTrack.Builder = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(44100)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(
                AudioTrack.getMinBufferSize(
                    44100,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_8BIT
                )
            )
            .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
            .setTransferMode(AudioTrack.MODE_STREAM)
    }
}
