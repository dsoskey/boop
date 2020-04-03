//
// Created by yello on 3/28/2020.
//

#ifndef BOOPK_TRIANGLEWAVEGENERATOR_H
#define BOOPK_TRIANGLEWAVEGENERATOR_H

#include "WaveGenerator.h"

class TriangleWaveGenerator: public WaveGenerator {
public:
    TriangleWaveGenerator(int numVoices);
    double getWaveform(double phase);
private:
    int numVoices;
};


#endif //BOOPK_TRIANGLEWAVEGENERATOR_H
