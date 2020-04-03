//
// Created by yello on 3/28/2020.
//

#ifndef BOOPK_SAWWAVEGENERATOR_H
#define BOOPK_SAWWAVEGENERATOR_H


#include "WaveGenerator.h"

class SawWaveGenerator: public WaveGenerator {
public:
    SawWaveGenerator(int numVoices);
    double getWaveform(double phase);
private:
    int numVoices;
};


#endif //BOOPK_SAWWAVEGENERATOR_H
