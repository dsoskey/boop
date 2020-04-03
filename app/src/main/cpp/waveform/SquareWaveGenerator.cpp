//
// Created by yello on 3/26/2020.
//

#include "SquareWaveGenerator.h"

#define TWO_PI (3.14159 * 2)


double SquareWaveGenerator::getWaveform(double phase) {
    if (phase < TWO_PI / 2.0) {
        return -1.0;
    } else {
        return 1.0;
    }
}