#include <math.h>
#include "SawWaveGenerator.h"

SawWaveGenerator::SawWaveGenerator(int numVoices) {
    if (numVoices < 1) {
        this->numVoices = 10;
    } else {
        this->numVoices = numVoices;
    }
}

double SawWaveGenerator::getWaveform(double phase) {
    double amplitude = 0.0;
    for (int i = 1; i <= this->numVoices; i++) {
        amplitude += sin(i * phase) / i;
    }
    return amplitude;
}