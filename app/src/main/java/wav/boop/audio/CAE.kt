package wav.boop.audio
//
//import kotlin.concurrent.thread
//
//class CAE(val numTracks: Int = 16): AudioEngine {
//    private val cTracks: List<ExperimentalTrackContainer> = List(numTracks, { ExperimentalTrackContainer(AudioEngine.DEFAULT_TRACK_BUILDER.build()) })
//
//    init {
//        thread(true) {
//            while (true) {
//                for (cTrack in cTracks) {
//                    if (cTrack.waveform != null) {
//                        val wave = cTrack.waveform?.invoke()
//                        thread(true) {
//                            cTrack.track.play()
//                            cTrack.track.write(wave!!, 0, wave.size)
//                            cTrack.track.stop()
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    override fun play(waveform: () -> ShortArray, id: Int) {
//        val trackToStart = cTracks.find { track -> track.id == null }
//        trackToStart?.setInfo(id, waveform)
//    }
//
//    override fun stop(id: Int) {
//        val trackToStop = cTracks.find { track -> track.id == id }
//        trackToStop?.clearInfo()
//    }
//}