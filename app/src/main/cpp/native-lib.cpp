#include <jni.h>
#include <list>
#include <string>
#include <vector>

#include "waveform/SquareWaveGenerator.h"
#include "waveform/SawWaveGenerator.h"
#include "waveform/SinWaveformGenerator.h"
#include "core/AudioEngine.h"
#include "log.h"

// TODO: Decouple creation of engine from starting of engine
// - This would allow me to free up the audio streams without having to recreate the entire engine every damn time

static AudioEngine *engine;
static std::shared_ptr<Synth> synth;
static std::shared_ptr<WaveGenerator> SQUARE = std::make_shared<SquareWaveGenerator>();
static std::shared_ptr<WaveGenerator> SIN = std::make_shared<SinWaveformGenerator>();
static std::shared_ptr<WaveGenerator> SAW = std::make_shared<SawWaveGenerator>(69);

std::vector<int> convertJavaArrayToVector(JNIEnv *env, jintArray intArray) {
    std::vector<int> v;
    jsize length = env->GetArrayLength(intArray);
    if (length > 0) {
        jint *elements = env->GetIntArrayElements(intArray, nullptr);
        v.insert(v.end(), &elements[0], &elements[length]);
        // Unpin the memory for the array, or free the copy.
        env->ReleaseIntArrayElements(intArray, elements, 0);
    }
    return v;
}

std::vector<float> convertJavaArrayToVector(JNIEnv *env, jfloatArray floatArray) {
    std::vector<float> v;
    jsize length = env->GetArrayLength(floatArray);
    if (length > 0) {
        jfloat *elements = env->GetFloatArrayElements(floatArray, nullptr);
        v.insert(v.end(), &elements[0], &elements[length]);
        // Unpin the memory for the array, or free the copy.
        env->ReleaseFloatArrayElements(floatArray, elements, 0);
    }
    return v;
}

extern "C" {
    /**
     * Checks if the engine exists, which implies its running.
     * @param env
     * @param thiz
     * @return boolean: true if engine is running.
     */
    JNIEXPORT jboolean JNICALL
    Java_wav_boop_MainActivity_isEngineRunning(JNIEnv *env, jobject thiz) {
        return static_cast<jboolean>((engine != nullptr));
    }

    /**
    * Start the audio engine
    * @param env
    * @param instance
    * @param jCpuIds - CPU core IDs which the audio process should affine to
    */
    JNIEXPORT void JNICALL
    Java_wav_boop_MainActivity_startEngine(JNIEnv *env, jobject, jintArray jCpuIds) {
        std::vector<int> cpuIds = convertJavaArrayToVector(env, jCpuIds);
        LOGD("cpu ids size: %d", static_cast<int>(cpuIds.size()));
        engine = new AudioEngine(std::move(cpuIds));
        synth = engine->getSynth();
        LOGD("Engine Started");
    }

    /**
     * Stop the audio engine
     * @param env
     * @param instance
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_MainActivity_stopEngine(JNIEnv *env, jobject instance) {
        if (engine) {
            delete engine;
            if (synth) {
                synth.reset();
            }
        } else {
            LOGD("Engine does not exist, call startEngine() to create");
        }
    }

    /**
     * DEPRECATED. use synth equivalent
     * Sets the oscillator at oscIndex to on or off. Requires engine to be on to work
     * @param env
     * @param instance
     * @param oscIndex - index of oscillator in synthesizer to affect
     * @param isOn - should oscillator be on or off
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_pad_PadFragment_setWaveOn(JNIEnv *env, jobject instance, jint oscIndex, jboolean isOn) {
        if (synth) {
            synth->setWaveOn(oscIndex, isOn);
        } else {
            LOGE("Synth does not exist, call createEngine() to create a new one");
        }
    }

    /**
    * Sets the oscillator at oscIndex to on or off. Requires engine to be on to work
    * @param env
    * @param instance
    * @param oscIndex - index of oscillator in synthesizer to affect
    * @param isOn - should oscillator be on or off
    */
    JNIEXPORT void JNICALL
    Java_wav_boop_model_SynthesizerModel_setWaveOn(JNIEnv *env, jobject instance, jint oscIndex, jboolean isOn) {
        if (synth) {
            synth->setWaveOn(oscIndex, isOn);
        } else {
            LOGE("Synth does not exist, call createEngine() to create a new one");
        }
    }

    /**
     * Sets frequency of oscillator at oscIndex. Requires engine to be on to work
     * @param env
     * @param instance
     * @param oscIndex - index of oscillator in synthesizer to affect
     * @param frequency - new frequency of oscillator
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_model_PitchModel_setFrequency(JNIEnv *env, jobject instance, jint oscIndex, jdouble frequency) {
        if (synth) {
            synth->setFrequency(oscIndex, frequency);
        } else {
            LOGE("Synth does not exist, call createEngine() to create a new one");
        }
    }

    /**
     * Sets the oscillator at oscIndex to on or off. Requires engine to be on to work
     * @param env
     * @param instance
     * @param oscIndex - index of oscillator in synthesizer to affect
     * @param isOn - should oscillator be on or off
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_pad_TestPad_setWaveOn(JNIEnv *env, jobject instance, jint oscIndex, jboolean isOn) {
        if (synth) {
            synth->setWaveOn(oscIndex, isOn);
        } else {
            LOGE("Synth does not exist, call createEngine() to create a new one");
        }
    }

    /**
    * Sets the oscillator at oscIndex to on or off. Requires engine to be on to work
    * @param env
    * @param instance
    * @param channelIndex - index of channel in synthesizer to affect
    * @param isOn - should channel be on or off
    */
    JNIEXPORT void JNICALL
    Java_wav_boop_sample_SamplerModel_ndkSetSampleOn(JNIEnv *env, jobject instance, jint channelIndex, jboolean isOn) {
        if (synth) {
            synth->setWaveOn(channelIndex, isOn);
        } else {
            LOGE("Synth does not exist, call createEngine() to create a new one");
        }
    }

    /**
     * Sets frequency of oscillator at oscIndex. Requires engine to be on to work
     * @param env
     * @param instance
     * @param oscIndex - index of oscillator in synthesizer to affect
     * @param frequency - new frequency of oscillator
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_pad_TestPad_setFrequency(JNIEnv *env, jobject instance, jint oscIndex, jdouble frequency) {
        if (synth) {
            synth->setFrequency(oscIndex, frequency);
        } else {
            LOGE("Synth does not exist, call createEngine() to create a new one");
        }
    }

    /**
    * Sets waveform generator for oscillator at oscIndex. Requires engine to be on to work
    * @param env
    * @param instance
    * @param oscIndex - index of oscillator in synthesizer to affect
    * @param waveform - sin, square, or saw
    */
    JNIEXPORT void JNICALL
    Java_wav_boop_model_OscillatorModel_ndkSetWaveform(JNIEnv *env, jobject instance, jint oscIndex, jstring waveform) {
        if (synth) {
            std::shared_ptr<WaveGenerator> gen;
            std::string wf = env->GetStringUTFChars(waveform, NULL);
            if (wf == "sin") {
                gen = SIN;
            } else if (wf == "square") {
                gen = SQUARE;
            } else if (wf == "saw") {
                gen = SAW;
            } else {
                gen = SIN;
            }
            synth->setWave(oscIndex, gen);
        } else {
            LOGE("Synth does not exist, call createEngine() to create a new one");
        }
    }


    JNIEXPORT void JNICALL
    Java_wav_boop_sample_SamplerModel_ndkSetSample(JNIEnv *env, jobject instance, jint channelIndex, jfloatArray sample) {
        if (synth) {
            std::vector<float> data = convertJavaArrayToVector(env, sample);
            synth->setSample(channelIndex, data);
        } else {
            LOGE("Synth does not exist, call createEngine() to create a new one");
        }
    }

    JNIEXPORT void JNICALL
    Java_wav_boop_sample_SamplerModel_ndkStartRecording(JNIEnv *env, jobject instance, jint oscIndex) {
        if (engine) {
            engine->startRecordingSample(oscIndex);
        } else {
            LOGE("Engine does not exist, call createEngine() to create a new one");
        }
    }

    JNIEXPORT jfloatArray JNICALL
    Java_wav_boop_sample_SamplerModel_ndkStopRecording(JNIEnv *env, jobject instance) {
        jfloatArray result;
        if (engine) {
            std::vector<float> sample = engine->stopRecordingSample();
            result = env->NewFloatArray(sample.size());

            if (result != nullptr) {
                env->SetFloatArrayRegion(result, 0, sample.size(), sample.data());
            }

            return result;
        } else {
            LOGE("Engine does not exist, call createEngine() to create a new one");
            return nullptr;
        }
    }

    /**
     * Sets startFrame of sample at oscIndex
     * @param oscIndex - index of sample in sampler to affect
     * @param startFrame - new frame value
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_sample_SamplerModel_ndkSetSampleStart(JNIEnv *env, jobject instance, jint oscIndex, jint startFrame) {
        if (synth) {
            synth->setSampleStart(oscIndex, startFrame);
        } else {
          LOGE("Synth does not exist");
        }
    }

    /**
     * Sets startFrame of sample at oscIndex
     * @param oscIndex - index of sample in sampler to affect
     * @param endFrame - new frame value
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_sample_SamplerModel_ndkSetSampleEnd(JNIEnv *env, jobject instance, jint oscIndex, jint endFrame) {
        if (synth) {
            synth->setSampleEnd(oscIndex, endFrame);
        } else {
            LOGE("Synth does not exist");
        }
    }

    /**
     * Sets amplitude for oscillator at oscIndex. Requires engine to be on to work
     * @param oscIndex - index of oscillator in synthesizer to affect
     * @param amplitude - new amplitude
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_sample_SamplerModel_ndkSetSampleAmplitude(JNIEnv *env, jobject instance, jint oscIndex, jfloat amplitude) {
        if (synth) {
            synth->setAmplitude(oscIndex, amplitude);
        } else {
            LOGE("Synth does not exist, call createEngine() to create a new one");
        }
    }

    /**
     * Sets amplitude for oscillator at oscIndex. Requires engine to be on to work
     * @param oscIndex - index of oscillator in synthesizer to affect
     * @param amplitude - new amplitude
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_model_OscillatorModel_ndkSetAmplitude(JNIEnv *env, jobject instance, jint oscIndex, jfloat amplitude) {
        if (synth) {
            synth->setAmplitude(oscIndex, amplitude);
        } else {
            LOGE("Synth does not exist, call createEngine() to create a new one");
        }
    }


    /**
    * Sets attack length in milliseconds for all oscillators. Requires an engine to be on to work
    * @param numMillis - length of attack
    */
    JNIEXPORT void JNICALL
    Java_wav_boop_model_ADSRModel_ndkSetAttackLength(JNIEnv *env, jobject instance, jint numMillis) {
        if (synth) {
            synth->setAttackLength(numMillis);
        } else {
            LOGE("Synth does not exist, call createEngine() to create a new one");
        }
    }

    /**
     * Sets decay length in milliseconds for all oscillators. Requires an engine to be on to work
     * @param numMillis - length of decay
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_model_ADSRModel_ndkSetDecayLength(JNIEnv *env, jobject instance, jint numMillis) {
        if (synth) {
            synth->setDecayLength(numMillis);
        } else {
            LOGE("Synth does not exist, call createEngine() to create a new one");
        }
    }

    /**
     * Sets sustain level amplitude for all oscillators. Requires an engine to be on to work
     * @param amplitude
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_model_ADSRModel_ndkSetSustainLevel(JNIEnv *env, jobject instance, jfloat amplitude) {
        if (synth) {
            synth->setSustainedLevel(amplitude);
        } else {
            LOGE("Synth does not exist, call createEngine() to create a new one");
        }
    }

    /**
     * Sets attack length in milliseconds for all oscillators. Requires an engine to be on to work
     * @param numMillis - length of attack
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_model_ADSRModel_ndkSetReleaseLength(JNIEnv *env, jobject instance, jint numMillis) {
        if (synth) {
            synth->setReleaseLength(numMillis); // TODO: See if this is working nice
        } else {
            LOGE("Synth does not exist, call createEngine() to create a new one");
        }
    }

    /**
     * Globally sets sample rate and frames per burst
     * @param sampleRate
     * @param framesPerBurst
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_MainActivity_setDefaultStreamValues(
            JNIEnv *env,
            jobject type,
            jint sampleRate,
            jint framesPerBurst) {
        oboe::DefaultStreamValues::SampleRate = (int32_t) sampleRate;
        oboe::DefaultStreamValues::FramesPerBurst = (int32_t) framesPerBurst;
    }
}