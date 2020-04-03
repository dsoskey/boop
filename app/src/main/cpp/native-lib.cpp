#include <jni.h>
#include <list>
#include <string>
#include <android/input.h>
#include "AudioEngine.h"
#include "waveform/SquareWaveGenerator.h"
#include "waveform/SawWaveGenerator.h"
#include "waveform/SinWaveformGenerator.h"
#include "waveform/TriangleWaveGenerator.h"
#include "Synthesizer.h"

static Synthesizer *synthesizer = new Synthesizer(4);

extern "C" {
    JNIEXPORT void JNICALL
    Java_wav_boop_pad_PadFragment_touchEvent(
            JNIEnv* env,
            jobject obj,
            jint action,
            jdouble frequency) {
        switch (action) {
            case AMOTION_EVENT_ACTION_DOWN:
                synthesizer->setToneOn(frequency);
                break;
            case AMOTION_EVENT_ACTION_UP:
                synthesizer->setToneOff(frequency);
                break;
            default:
                break;
        }
    }

    JNIEXPORT void JNICALL
    Java_wav_boop_menu_EngineSelectorActionProvider_setWaveform(
            JNIEnv* env,
            jobject obj,
            jstring waveform) {
        WaveGenerator* gen;
        std::string wf = env->GetStringUTFChars(waveform, NULL);
        if (wf.compare("sin") == 0) {
            gen = new SinWaveformGenerator();
        } else if (wf.compare("square") == 0) {
            gen = new SquareWaveGenerator();
        } else if (wf.compare("saw") == 0) {
            gen = new SawWaveGenerator(100);
        } else if (wf.compare("triangle") == 0) {
            gen = new TriangleWaveGenerator(10);
        } else {
            gen = new SinWaveformGenerator();
        }
        synthesizer->setWave(gen);
    }

    JNIEXPORT void JNICALL
    Java_wav_boop_MainActivity_startEngine(JNIEnv *env, jobject) {
        synthesizer->startEngines();
    }

    JNIEXPORT void JNICALL
    Java_wav_boop_MainActivity_stopEngine(JNIEnv *env, jobject) {
        synthesizer->stopEngines();
    }
}
