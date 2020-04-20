#include <jni.h>
#include <list>
#include <string>
#include <vector>

#include "waveform/SquareWaveGenerator.h"
#include "waveform/SawWaveGenerator.h"
#include "waveform/SinWaveformGenerator.h"
#include "core/AudioEngine.h"
#include "log.h"

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
    *
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

    JNIEXPORT void JNICALL
    Java_wav_boop_MainActivity_stopEngine(JNIEnv *env, jobject instance) {
        if (engine) {
            delete engine;
        } else {
            LOGD("Engine does not exist, call startEngine() to create");
        }
    }

    JNIEXPORT void JNICALL
    Java_wav_boop_pad_PadFragment_setWaveOn(JNIEnv *env, jobject instance, jint oscIndex, jboolean isDown) {
        if (engine) {
            engine->setSourceOn(oscIndex, isDown);
        } else {
            LOGE("Engine does not exist, call createEngine() to create a new one");
        }
    }

    JNIEXPORT void JNICALL
    Java_wav_boop_model_PitchContainer_setFrequency(JNIEnv *env, jobject instance, jint oscIndex, jdouble frequency) {
        if (engine) {
            engine->setFrequency(oscIndex, frequency);
        } else {
            LOGE("Engine does not exist, call createEngine() to create a new one");
        }
    }

    JNIEXPORT void JNICALL
    Java_wav_boop_control_EngineSelectorFragment_setWaveform(JNIEnv *env, jobject instance, jint oscIndex, jstring waveform) {
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

    JNIEXPORT void JNICALL
    Java_wav_boop_control_EngineSelectorFragment_setAmplitude(JNIEnv *env, jobject instance, jint oscIndex, jfloat amplitude) {
        if (engine) {
            engine->setAmplitude(oscIndex, amplitude);
        } else {
            LOGE("Engine does not exist, call createEngine() to create a new one");
        }
    }

        JNIEXPORT void JNICALL
    Java_wav_boop_MainActivity_native_1setDefaultStreamValues(
            JNIEnv *env,
            jobject type,
            jint sampleRate,
            jint framesPerBurst) {
        oboe::DefaultStreamValues::SampleRate = (int32_t) sampleRate;
        oboe::DefaultStreamValues::FramesPerBurst = (int32_t) framesPerBurst;
    }
}