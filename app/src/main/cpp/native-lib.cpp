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
static AudioEngine *engine;
static WaveGenerator* SQUARE = new SquareWaveGenerator();
static WaveGenerator* SIN = new SinWaveformGenerator();
static WaveGenerator* SAW = new SawWaveGenerator(69);

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

extern "C" {
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
        } else {
            LOGD("Engine does not exist, call startEngine() to create");
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
    Java_wav_boop_pad_PadFragment_setWaveOn(JNIEnv *env, jobject instance, jint oscIndex, jboolean isOn) {
        if (engine) {
            engine->setSourceOn(oscIndex, isOn);
        } else {
            LOGE("Engine does not exist, call createEngine() to create a new one");
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
    Java_wav_boop_model_PitchContainer_setFrequency(JNIEnv *env, jobject instance, jint oscIndex, jdouble frequency) {
        if (engine) {
            engine->setFrequency(oscIndex, frequency);
        } else {
            LOGE("Engine does not exist, call createEngine() to create a new one");
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
    Java_wav_boop_control_OscillatorControlFragment_setWaveform(JNIEnv *env, jobject instance, jint oscIndex, jstring waveform) {
        if (engine) {
            WaveGenerator* gen;
            std::string wf = env->GetStringUTFChars(waveform, NULL);
            if (wf.compare("sin") == 0) {
                gen = SIN;
            } else if (wf.compare("square") == 0) {
                gen = SQUARE;
            } else if (wf.compare("saw") == 0) {
                gen = SAW;
            } else {
                gen = SIN;
            }
            engine->setWaveform(oscIndex, gen);
        } else {
            LOGE("Engine does not exist, call createEngine() to create a new one");
        }
    }

    /**
     * Sets amplitude for oscillator at oscIndex. Requires engine to be on to work
     * @param env
     * @param instance
     * @param oscIndex - index of oscillator in synthesizer to affect
     * @param amplitude - new amplitude
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_control_OscillatorControlFragment_setAmplitude(JNIEnv *env, jobject instance, jint oscIndex, jfloat amplitude) {
        if (engine) {
            engine->setAmplitude(oscIndex, amplitude);
        } else {
            LOGE("Engine does not exist, call createEngine() to create a new one");
        }
    }

    /**
     * Sets attack length in milliseconds for all oscillators. Requires an engine to be on to work
     * @param env
     * @param instance - index of oscillator in synthesizer to affect
     * @param numMillis - length of attack
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_control_ADSRControlFragment_setAttackLength(JNIEnv *env, jobject instance, jint numMillis) {
        if (engine) {
            engine->setAttackLength(numMillis);
        } else {
            LOGE("Engine does not exist, call createEngine() to create a new one");
        }
    }

    /**
     * Sets decay length in milliseconds for all oscillators. Requires an engine to be on to work
     * @param env
     * @param instance - index of oscillator in synthesizer to affect
     * @param numMillis - length of decay
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_control_ADSRControlFragment_setDecayLength(JNIEnv *env, jobject instance, jint numMillis) {
        if (engine) {
            engine->setDecayLength(numMillis);
        } else {
            LOGE("Engine does not exist, call createEngine() to create a new one");
        }
    }

    /**
     * Sets sustain level amplitude for all oscillators. Requires an engine to be on to work
     * @param env
     * @param instance
     * @param amplitude
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_control_ADSRControlFragment_setSustainLevel(JNIEnv *env, jobject instance, jfloat amplitude) {
        if (engine) {
            engine->setSustainedLevel(amplitude);
        } else {
            LOGE("Engine does not exist, call createEngine() to create a new one");
        }
    }

    /**
     * Sets attack length in milliseconds for all oscillators. Requires an engine to be on to work
     * @param env
     * @param instance - index of oscillator in synthesizer to affect
     * @param numMillis - length of attack
     */
    JNIEXPORT void JNICALL
    Java_wav_boop_control_ADSRControlFragment_setReleaseLength(JNIEnv *env, jobject instance, jint numMillis) {
        if (engine) {
            engine->setReleaseLength(numMillis);
        } else {
            LOGE("Engine does not exist, call createEngine() to create a new one");
        }
    }

    /**
     * Globally sets sample rate and frames per burst
     * @param env
     * @param type
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