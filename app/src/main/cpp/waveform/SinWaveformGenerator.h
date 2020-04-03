//
// Created by yello on 3/26/2020.
//

#ifndef BOOPK_SINWAVEFORMGENERATOR_H
#define BOOPK_SINWAVEFORMGENERATOR_H

#include "WaveGenerator.h"

class SinWaveformGenerator: public WaveGenerator {
public:
    double getWaveform(double phase);
};


#endif //BOOPK_SINWAVEFORMGENERATOR_H
