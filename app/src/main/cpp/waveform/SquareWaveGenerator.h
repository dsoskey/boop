//
// Created by yello on 3/26/2020.
//

#ifndef BOOPK_SQUAREWAVEGENERATOR_H
#define BOOPK_SQUAREWAVEGENERATOR_H


#include "WaveGenerator.h"

class SquareWaveGenerator: public WaveGenerator {
    double getWaveform(double phase);
};


#endif //BOOPK_SQUAREWAVEGENERATOR_H
