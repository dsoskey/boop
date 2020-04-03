//
// Created by yello on 3/15/2020.
//
#include <aaudio/AAudio.h>
#include <jni.h>
#include "Oscillator.h"

#ifndef WAVEMAKER_AUDIOENGINE_H
#define WAVEMAKER_AUDIOENGINE_H

class AudioEngine {
public:
    bool start();
    void stop();
    void restart();
    void setToneOn(double frequency);
    void setToneOff();
    void setWave(WaveGenerator* waveGenerator);

private:
    Oscillator oscillator_;
    AAudioStream *stream_;
};



#endif //WAVEMAKER_AUDIOENGINE_H
