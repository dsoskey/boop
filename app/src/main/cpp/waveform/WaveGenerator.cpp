#include "WaveGenerator.h"

#define TWO_PI (3.14159 * 2)

void WaveGenerator::setCutoffFrequency(double frequency) {
    highPassCutoff = frequency;
    timeConstant = 1.0 / (TWO_PI * highPassCutoff);
}

double WaveGenerator::getHighPassCutoff() {
    return highPassCutoff;
}

double WaveGenerator::getTimeConstant() {
    return  1.0 / (TWO_PI * highPassCutoff);
}