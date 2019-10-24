package com.example.boop.waveform

import kotlin.math.cos

class SawEngine(var numVoices: Int = 4) : WaveformEngine {
//    class SawEngine(var waveNG1: WaveformEngine, var numVoices: Int = 2) : WaveformEngine {
//    override fun getWaveform(frequency: Double): () -> DoubleArray {
//        val waves = arrayOfNulls<DoubleArray>(numVoices)
//        for (i in waves.indices) {
//            waves[i] = waveNG1.getWaveform(frequency * i)()
//            println(waves[i])
//        }
//        println("Hi dude!")
//        return fun (): DoubleArray {
//            val mSound = DoubleArray(waves[0]!!.size)
//            for (i in mSound.indices) {
//                mSound[i] = 0.0
//                for (wave in waves) {
//                    mSound[i] += wave!![i]
//                }
//            }
//            return mSound
//        }
//    }
    private fun baseSineFunction(frequency: Double, amplitude: Double, time: Int): Double {
        return amplitude * cos(2.0 * frequency * Math.PI * time.toDouble() / (DEFAULT_SAMPLE_RATE_IN_SECONDS))// TODO: Read playback speed?

}

    override fun getWaveform(frequency: Double): () -> DoubleArray {
        return fun (): DoubleArray {
            val duration = DEFAULT_SAMPLE_RATE_IN_SECONDS / frequency
            val mSound = DoubleArray(duration.toInt())
            for (i in mSound.indices) {
                mSound[i] = 0.0
                for (harmonic in 1.rangeTo(numVoices)) {
                    mSound[i] += baseSineFunction(frequency * harmonic, 1.0 / harmonic, i)
                }
            }
            return mSound
        }
    }
}