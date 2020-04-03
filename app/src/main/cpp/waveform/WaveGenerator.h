//
// Created by yello on 3/21/2020.
//

#ifndef BOOPK_WAVEGENERATOR_H
#define BOOPK_WAVEGENERATOR_H


#include <jni.h>

class WaveGenerator {
public:
    virtual double getWaveform(double phase) = 0;
};


#endif //BOOPK_WAVEGENERATOR_H
